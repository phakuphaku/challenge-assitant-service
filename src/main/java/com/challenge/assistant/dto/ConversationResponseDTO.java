package com.challenge.assistant.dto;

import java.util.List;

public class ConversationResponseDTO {
    private Long id;
    private List<MessageResponseDTO> messages;

    public ConversationResponseDTO(Long id, List<MessageResponseDTO> messages) {
        this.id = id;
        this.messages = messages;
    }

    public Long getId() { return id; }
    public List<MessageResponseDTO> getMessages() { return messages; }
}
