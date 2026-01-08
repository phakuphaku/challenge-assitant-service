package com.challenge.assistant.dto.response;

public class AssistantResponse {

    private String reply;
    private String source;

    public AssistantResponse(String reply, String source) {
        this.reply = reply;
        this.source = source;
    }

    public String getReply() {
        return reply;
    }

    public String getSource() {
        return source;
    }
}