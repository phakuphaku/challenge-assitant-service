package com.challenge.assistant.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
public class ConversationEntity {

    @Id
    private String id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageEntity> messages = new ArrayList<>();

    protected ConversationEntity() {
        // JPA
    }

    public ConversationEntity(String id) {
        this.id = id;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<MessageEntity> getMessages() {
        return messages;
    }

    public void addMessage(MessageEntity message) {
        messages.add(message);
        message.setConversation(this);
    }
}
