package com.SmartHealthRemoteSystem.SHSR.Communication.service;

import com.SmartHealthRemoteSystem.SHSR.Communication.dto.SendMessageRequest;
import com.SmartHealthRemoteSystem.SHSR.Communication.model.ChatSession;
import com.SmartHealthRemoteSystem.SHSR.Communication.model.ChatStatus;
import com.SmartHealthRemoteSystem.SHSR.Communication.model.Message;
import com.SmartHealthRemoteSystem.SHSR.Communication.model.MessageStatus;
import com.SmartHealthRemoteSystem.SHSR.Communication.repository.ChatRepository;
import com.SmartHealthRemoteSystem.SHSR.Communication.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository,
                          ChatRepository chatRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
    }

    // UCR022 + UCR023 — Send message
    public Message sendMessage(SendMessageRequest request) {
        ChatSession session = chatRepository.findByChatId(request.getChatId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Chat session not found: " + request.getChatId()));

        if (session.getStatus() == ChatStatus.CLOSED) {
            throw new IllegalStateException(
                    "Cannot send a message to a closed chat session.");
        }

        if (!session.getParticipants().contains(request.getSenderId())) {
            throw new IllegalArgumentException(
                    "Sender '" + request.getSenderId() + "' is not a participant of this chat.");
        }

        Message message = Message.builder()
                .messageId(UUID.randomUUID().toString())
                .chatId(request.getChatId())
                .senderId(request.getSenderId())
                .content(request.getContent())
                .imagingReferenceId(request.getImagingReferenceId())
                .status(MessageStatus.SENT)
                .timestamp(LocalDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    // UCR024 — Get message history (chronological)
    public List<Message> getMessages(String chatId) {
        if (chatId == null || chatId.isBlank()) {
            throw new IllegalArgumentException("chatId must not be blank.");
        }

        chatRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Chat session not found: " + chatId));

        return messageRepository.findByChatIdOrderByTimestampAsc(chatId);
    }
}
