package com.challenge.assistant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.challenge.assistant.dto.response.CreateConversationResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    @PostMapping
    public ResponseEntity<CreateConversationResponse> createConversation() {
        CreateConversationResponse response =
                new CreateConversationResponse(UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getConversation(@PathVariable String id) {
        return ResponseEntity.ok("Conversation " + id);
    }
}
