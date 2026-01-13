package com.challenge.assistant.controller;

import com.challenge.assistant.dto.ConversationResponseDTO;
import com.challenge.assistant.dto.MessageResponseDTO;
import com.challenge.assistant.dto.request.UserMessageRequest;
import com.challenge.assistant.model.Conversation;
import com.challenge.assistant.model.Message;
import com.challenge.assistant.persistence.repository.ConversationRepository;
import com.challenge.assistant.persistence.repository.MessageRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @PostMapping
    public ResponseEntity<ConversationResponseDTO> createConversation() {
        Conversation conv = new Conversation();
        conversationRepository.save(conv);

        ConversationResponseDTO dto = new ConversationResponseDTO(
            conv.getId(),
            conv.getMessages().stream()
                    .map(m -> new MessageResponseDTO(m.getContent()))
                    .toList()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @PostMapping("/{id}/messages")
    public ResponseEntity<ConversationResponseDTO> addMessage(
            @PathVariable Long id,
            @Valid @RequestBody UserMessageRequest content) {

        return conversationRepository.findById(id)
                .map(conv -> {
                    Message msg = new Message();
                    msg.setContent(content.getMessage());
                    conv.addMessage(msg);
                    conversationRepository.save(conv);

                    ConversationResponseDTO dto = new ConversationResponseDTO(
                            conv.getId(),
                            conv.getMessages().stream()
                                    .map(m -> new MessageResponseDTO(m.getContent()))
                                    .toList()
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ConversationResponseDTO> getConversation(@PathVariable Long id) {
        return conversationRepository.findById(id)
                .map(conv -> {
                    ConversationResponseDTO dto = new ConversationResponseDTO(
                            conv.getId(),
                            conv.getMessages().stream()
                                    .map(m -> new MessageResponseDTO(m.getContent()))
                                    .toList()
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
