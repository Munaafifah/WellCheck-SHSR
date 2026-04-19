package com.SmartHealthRemoteSystem.SHSR.Communication.controller;

import com.SmartHealthRemoteSystem.SHSR.Communication.dto.CreateChatRequest;
import com.SmartHealthRemoteSystem.SHSR.Communication.model.ChatSession;
import com.SmartHealthRemoteSystem.SHSR.Communication.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // UCR021 — POST /api/chats
    @PostMapping
    public ResponseEntity<?> createChat(@Valid @RequestBody CreateChatRequest request) {
        try {
            ChatSession created = chatService.createChat(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    // UCR024 — GET /api/chats/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<?> getChatsByUser(@PathVariable String userId) {
        try {
            List<ChatSession> sessions = chatService.getChatsByUser(userId);
            return ResponseEntity.ok(sessions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // UCR025 — PUT /api/chats/{chatId}/close
    @PutMapping("/{chatId}/close")
    public ResponseEntity<?> closeChat(@PathVariable String chatId) {
        try {
            ChatSession closed = chatService.closeChat(chatId);
            return ResponseEntity.ok(closed);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
}
