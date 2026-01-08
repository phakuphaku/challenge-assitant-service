package com.challenge.assistant.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.challenge.assistant.dto.request.UserMessageRequest;
import com.challenge.assistant.dto.response.AssistantResponse;
import com.challenge.assistant.dto.response.CreateConversationResponse;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    private final RestTemplate restTemplate;

    public ConversationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<CreateConversationResponse> createConversation() {
        CreateConversationResponse response =
                new CreateConversationResponse(UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<AssistantResponse> postMessage(
            @PathVariable String id,
            @RequestBody UserMessageRequest request) {

        String userMessage = request.getMessage();

        if (userMessage != null && userMessage.contains("?")) {
            Map<?, ?> apiResponse =
                    restTemplate.getForObject("https://yesno.wtf/api", Map.class);

            String answer = apiResponse.get("answer").toString();
            return ResponseEntity.ok(new AssistantResponse(answer, "yesno.wtf"));
        }

        return ResponseEntity.ok(
                new AssistantResponse(
                        "Could you please rephrase your question?",
                        "fallback"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getConversation(@PathVariable String id) {
        return ResponseEntity.ok("Conversation " + id);
    }
}
