package com.SmartHealthRemoteSystem.SHSR.Communication.controller;

import com.SmartHealthRemoteSystem.SHSR.Communication.dto.SendMessageRequest;
import com.SmartHealthRemoteSystem.SHSR.Communication.model.Message;
import com.SmartHealthRemoteSystem.SHSR.Communication.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // UCR022 + UCR023 — POST /api/messages
    @PostMapping
    public ResponseEntity<?> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        try {
            Message saved = messageService.sendMessage(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    // UCR024 — GET /api/messages/{chatId}
    @GetMapping("/{chatId}")
    public ResponseEntity<?> getMessages(@PathVariable String chatId) {
        try {
            List<Message> messages = messageService.getMessages(chatId);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
