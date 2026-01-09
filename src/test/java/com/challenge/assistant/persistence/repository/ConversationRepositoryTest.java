package com.challenge.assistant.persistence.repository;

import com.challenge.assistant.model.Conversation;
import com.challenge.assistant.model.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ConversationRepositoryTest {

    @Autowired
    private ConversationRepository conversationRepository;

    @SuppressWarnings("unused")
    @Autowired
    private MessageRepository messageRepository;

    @Test
    void testSaveConversationWithMessage() {
        Conversation conv = new Conversation();
        Message msg = new Message();
        msg.setContent("Hola!");

        conv.addMessage(msg);

        conversationRepository.save(conv);

        assertThat(conv.getId()).isNotNull();
        assertThat(msg.getId()).isNotNull();
        assertThat(conversationRepository.findById(conv.getId()).get().getMessages())
                .hasSize(1)
                .first()
                .extracting(Message::getContent)
                .isEqualTo("Hola!");
    }
}
