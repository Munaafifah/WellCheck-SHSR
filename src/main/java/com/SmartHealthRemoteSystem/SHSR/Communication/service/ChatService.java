package com.SmartHealthRemoteSystem.SHSR.Communication.service;

import com.SmartHealthRemoteSystem.SHSR.Communication.dto.CreateChatRequest;
import com.SmartHealthRemoteSystem.SHSR.Communication.model.ChatSession;
import com.SmartHealthRemoteSystem.SHSR.Communication.model.ChatStatus;
import com.SmartHealthRemoteSystem.SHSR.Communication.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    // UCR021 — Initiate Chat (throws if an identical active session already exists)
    public ChatSession createChat(CreateChatRequest request) {
        List<String> participants = request.getParticipants();
        String createdBy = request.getCreatedBy();

        if (participants == null || participants.size() < 2) {
            throw new IllegalArgumentException("A chat session requires at least 2 participants.");
        }
        if (createdBy == null || createdBy.isBlank()) {
            throw new IllegalArgumentException("createdBy must not be blank.");
        }

        List<String> normalizedParticipants = participants.stream()
                .filter(p -> p != null)
                .map(String::trim)
                .filter(p -> !p.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (normalizedParticipants.size() < 2) {
            throw new IllegalArgumentException("A chat session requires at least 2 unique participants.");
        }
        if (normalizedParticipants.stream().noneMatch(p -> p.equals(createdBy.trim()))) {
            throw new IllegalArgumentException("createdBy must be one of the participants.");
        }

        Set<String> requestedSet = new HashSet<>(normalizedParticipants);
        boolean duplicateExists = chatRepository.findByStatus(ChatStatus.ACTIVE)
                .stream()
                .anyMatch(existing -> {
                    List<String> ep = existing.getParticipants();
                    return ep != null && new HashSet<>(ep).equals(requestedSet);
                });

        if (duplicateExists) {
            throw new IllegalStateException(
                    "An active chat session with the same participants already exists.");
        }

        ChatSession session = ChatSession.builder()
                .chatId(UUID.randomUUID().toString())
                .participants(normalizedParticipants)
                .createdBy(createdBy.trim())
                .subject(request.getSubject())
                .status(ChatStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        return chatRepository.save(session);
    }

    // UCR024 — List chats for a user
    public List<ChatSession> getChatsByUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank.");
        }
        return chatRepository.findByParticipantsContaining(userId);
    }

    // UCR025 — Soft delete (close)
    public ChatSession closeChat(String chatId) {
        if (chatId == null || chatId.isBlank()) {
            throw new IllegalArgumentException("chatId must not be blank.");
        }
        ChatSession session = chatRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Chat session not found: " + chatId));

        if (session.getStatus() == ChatStatus.CLOSED) {
            throw new IllegalStateException("Chat session is already closed.");
        }

        session.setStatus(ChatStatus.CLOSED);
        session.setClosedAt(LocalDateTime.now());
        return chatRepository.save(session);
    }

    // Report Integration — returns existing active session for the participant set,
    // or creates a new one. Never throws on duplicate (unlike createChat).
    public ChatSession createOrGetChatSessionForReport(List<String> participants,
                                                       String createdBy,
                                                       String subject) {
        List<String> normalized = participants.stream()
                .filter(p -> p != null && !p.isBlank())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());

        if (normalized.size() < 2) {
            throw new IllegalArgumentException(
                    "createOrGetChatSessionForReport requires at least 2 unique participants.");
        }

        Set<String> requestedSet = new HashSet<>(normalized);

        Optional<ChatSession> existing = chatRepository.findByStatus(ChatStatus.ACTIVE)
                .stream()
                .filter(s -> s.getParticipants() != null
                        && new HashSet<>(s.getParticipants()).equals(requestedSet))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get();
        }

        ChatSession session = ChatSession.builder()
                .chatId(UUID.randomUUID().toString())
                .participants(new ArrayList<>(normalized))
                .createdBy(createdBy != null ? createdBy.trim() : normalized.get(0))
                .subject(subject)
                .status(ChatStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        return chatRepository.save(session);
    }
}
