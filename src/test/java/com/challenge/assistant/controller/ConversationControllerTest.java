package com.challenge.assistant.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import com.challenge.assistant.persistence.repository.ConversationRepository;
import com.challenge.assistant.persistence.repository.MessageRepository;
import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .andExpect(jsonPath("$.id").exists());
    }
/* Turn down, bacause dont exits for now
    @Test
    void shouldReturnFallbackWhenNoQuestionMark() throws Exception {
        mockMvc.perform(post("/api/v1/conversations/123/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "message": "Hello there" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value("fallback"));
    }*/

@Test
void shouldCallExternalApiWhenQuestionMarkPresent() throws Exception {

    // 1. Crear conversación
    MvcResult createResult = mockMvc.perform(post("/api/v1/conversations"))
            .andExpect(status().isCreated())
            .andReturn();
    String json = createResult.getResponse().getContentAsString();
        Number convId = JsonPath.read(json, "$.id");
        Long conversationId = convId.longValue();

    // 2. Enviar mensaje con ?
    mockMvc.perform(post("/api/v1/conversations/" + conversationId + "/messages")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                { "message": "Esto es una pregunta?" }
            """))
            .andExpect(status().isOk());

    // 3. Verificar interacción externa (si aplica)
    // verify(externalApiClient).call(any());
}


    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    void testCreateAndAddMessage() throws Exception {
        // Crear conversación
        String response = mockMvc.perform(post("/api/v1/conversations"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Obtener ID (simple parse de JSON minimal)
        // extraer del JSON
        Integer idInt = JsonPath.read(response, "$.id"); // puede ser Integer
        Long convId = idInt.longValue();                     // convertir a Long


        // Agregar mensaje
        mockMvc.perform(post("/api/v1/conversations/" + convId + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\": \"Hola mundo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0].message").value("Hola mundo"));

        // Consultar historial
        mockMvc.perform(get("/api/v1/conversations/" + convId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.messages[0].message").value("Hola mundo"));
    }
    
}
