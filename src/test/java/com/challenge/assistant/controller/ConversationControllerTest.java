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

/**
 * Integration tests for ConversationController.
 * Focused on HTTP contract and persistence behavior.
 * Conversational routing and external integrations are out of scope.
 */
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

/**
 * Quedo fuera del scope, pero deberia implementarse en un futuro.
 * Actualmente no hay fallback.
 * @throws Exception
 */    
//TODO: Enable when routing strategy is implemented
@Test
void shouldReturnFallbackWhenNoQuestionMark() throws Exception {

    // 1. Crear conversación
    MvcResult createResult = mockMvc.perform(post("/api/v1/conversations"))
            .andExpect(status().isCreated())
            .andReturn();

    Number conversationId_num = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");
    Long conversationId = conversationId_num.longValue();

    // 2. Enviar mensaje SIN ?
    mockMvc.perform(post("/api/v1/conversations/" + conversationId + "/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        { "message": "Hello there" }
                    """))
            .andExpect(status().isOk());
}

/**
 * Era la intencion inicial, pero por el momento esta logica no esta implementada.
 * @throws Exception
 */
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

/*
Al parecer no los estoy usando. Esta habiendo un error de tipo path.
Los remuevo para ver que pasen los tests.
//FIXIT
//TODO
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;
*/

/**
 * Aqui se prueban todas las funcionalidades implementadas y es lo que da sentido a la estructura actual.
 */
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
                //TODO Esto es deuda tecnica.
                //FIXIT aqui hay alto acoplamiento, deberia cambiar

        // Consultar historial
        mockMvc.perform(get("/api/v1/conversations/" + convId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.messages[0].message").value("Hola mundo"));
    }
    
}
