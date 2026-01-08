package com.challenge.assistant.controller;

import org.springframework.web.bind.annotation.*;

import com.challenge.assistant.dto.request.UserMessageRequest;
import com.challenge.assistant.dto.response.AssistantResponse;
import com.challenge.assistant.service.AssistantService;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    private final AssistantService assistantService;

    public ConversationController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/messages")
    public AssistantResponse receiveMessage(@RequestBody UserMessageRequest request) {
        String response = assistantService.respond(request.getMessage());
        return new AssistantResponse(response);
    }
}
