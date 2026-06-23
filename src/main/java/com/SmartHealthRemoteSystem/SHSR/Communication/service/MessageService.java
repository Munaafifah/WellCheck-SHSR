package com.SmartHealthRemoteSystem.SHSR.Communication.service;

import com.SmartHealthRemoteSystem.SHSR.Communication.dto.SendMessageRequest;
import com.SmartHealthRemoteSystem.SHSR.Communication.model.ChatSession;
import com.SmartHealthRemoteSystem.SHSR.Communication.model.ChatStatus;
import com.SmartHealthRemoteSystem.SHSR.Communication.model.Message;
import com.SmartHealthRemoteSystem.SHSR.Communication.model.MessageStatus;
import com.SmartHealthRemoteSystem.SHSR.Communication.repository.ChatRepository;
import com.SmartHealthRemoteSystem.SHSR.Communication.repository.MessageRepository;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.service.SchedulingNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository    chatRepository;
    private final SchedulingNotificationService notificationService;

    @Autowired
    public MessageService(MessageRepository messageRepository,
                          ChatRepository chatRepository,
                          SchedulingNotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.chatRepository    = chatRepository;
        this.notificationService = notificationService;
    }

    // UCR022 + UCR023 — Send message from a participant
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
                .reportId(request.getReportId())
                .reportLink(request.getReportLink())
                .status(MessageStatus.SENT)
                .timestamp(LocalDateTime.now())
                .build();

        Message saved = messageRepository.save(message);

        // UCR012 — notify every other participant in the chat
        notifyOtherParticipants(session, request.getSenderId(), request.getContent());

        return saved;
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

    // System-generated message — bypasses participant check; used for automated report alerts
    public Message sendSystemMessage(String chatId, String content,
                                     String reportId, String reportLink) {
        ChatSession session = chatRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Chat session not found: " + chatId));

        Message message = Message.builder()
                .messageId(UUID.randomUUID().toString())
                .chatId(chatId)
                .senderId("SYSTEM")
                .content(content)
                .reportId(reportId)
                .reportLink(reportLink)
                .status(MessageStatus.SENT)
                .timestamp(LocalDateTime.now())
                .build();

        Message saved = messageRepository.save(message);

        notifyOtherParticipants(session, "SYSTEM", content);

        return saved;
    }

    private void notifyOtherParticipants(ChatSession session, String senderId, String content) {
        if (session.getParticipants() == null) return;
        for (String participantId : session.getParticipants()) {
            if (!participantId.equals(senderId)) {
                notificationService.notifyMessageReceived(participantId, senderId, content);
            }
        }
    }
}