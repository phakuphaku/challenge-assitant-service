package com.challenge.assistant.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void shouldCreateConversation() throws Exception {
        mockMvc.perform(post("/api/v1/conversations"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.conversationId").exists());
    }

    @Test
    void shouldReturnFallbackWhenNoQuestionMark() throws Exception {
        mockMvc.perform(post("/api/v1/conversations/123/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "message": "Hello there" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value("fallback"));
    }

    @Test
    void shouldCallExternalApiWhenQuestionMarkPresent() throws Exception {
        MockRestServiceServer server =
                MockRestServiceServer.createServer(restTemplate);

        server.expect(requestTo("https://yesno.wtf/api"))
                .andRespond(withSuccess(
                        """
                        { "answer": "yes" }
                        """,
                        MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/v1/conversations/123/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "message": "Is this working?" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("yes"))
                .andExpect(jsonPath("$.source").value("yesno.wtf"));

        server.verify();
    }
}
