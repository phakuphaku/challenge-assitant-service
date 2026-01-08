package com.challenge.assistant.service.impl;

import org.springframework.stereotype.Service;
import com.challenge.assistant.service.AssistantService;

@Service
public class AssistantServiceImpl implements AssistantService {

    @Override
    public String respond(String message) {
        return "Respuesta dummy del asistente";
    }
}
