package com.challenge.assistant.dto.response;

public class CreateConversationResponse {

    private String conversationId;

    public CreateConversationResponse(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }
}
