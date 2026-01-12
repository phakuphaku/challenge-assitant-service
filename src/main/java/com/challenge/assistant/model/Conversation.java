package com.challenge.assistant.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    // getter y setter de id
    public Long getId() { return id; }
    // getter y setter de messages
    public List<Message> getMessages() { return messages; }
    public void addMessage(Message message) {
        messages.add(message);
        message.setConversation(this);
    }
}
