package com.challenge.assistant.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.challenge.assistant.model.Conversation;

import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
}
