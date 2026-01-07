# challenge-assitant-service

Repositorio correspondiente a un challenge técnico para el diseño e implementación de un microservicio Java, enfocado en un sistema conversacional distribuido.

Este repositorio implementa un microservicio conceptual denominado **Conversation Orchestrator Service**, cuyo objetivo es representar un componente central dentro de una arquitectura de asistentes virtuales empresariales.

El alcance funcional se mantiene deliberadamente acotado para priorizar claridad arquitectónica, extensibilidad y buenas prácticas de diseño.

---

## Rol del microservicio

El **Conversation Orchestrator Service** es responsable de:

- Recibir mensajes de usuarios
- Mantener el historial de una conversación
- Orquestar la lógica de decisión para generar una respuesta
- Integrarse con servicios externos como parte del flujo conversacional
- Devolver respuestas estructuradas a los consumidores del servicio

Este servicio actúa como un **orquestador síncrono**, desacoplado de los canales de entrada (chat web, móvil, etc.) y de los motores de decisión específicos.

---

## Decisión de diseño principal

Para simular la integración con un servicio externo de toma de decisiones (por ejemplo, un motor de reglas o un servicio de recomendación), se utiliza la API pública **yesno.wtf**.

Esta API permite representar de forma simple:

- Dependencia de servicios externos
- Respuestas no determinísticas
- Latencias y posibles fallos de integración

Se utiliza como un *placeholder liviano* que puede ser reemplazado en el futuro por un servicio interno o de terceros sin afectar el diseño general del microservicio.

---

## API propuesta

El microservicio expone una API REST síncrona con los siguientes endpoints:

- **POST /api/v1/conversations**  
  Crea una nueva conversación y devuelve su identificador.

- **POST /api/v1/conversations/{id}/messages**  
  Recibe un mensaje de usuario, lo procesa mediante la lógica del asistente y devuelve la respuesta generada.

- **GET /api/v1/conversations/{id}**  
  Devuelve el historial completo de la conversación, incluyendo mensajes del usuario y respuestas del asistente.

Los contratos de entrada y salida se definen mediante DTOs explícitos para desacoplar la API pública del modelo interno.

---

## Lógica del asistente

La lógica de procesamiento del asistente se implementa mediante reglas simples, con el objetivo de mantener el diseño claro y extensible:

- Si el mensaje contiene una pregunta cerrada (`?`), se realiza una llamada al servicio externo **yesno.wtf** para obtener una respuesta.
- En caso contrario, el asistente devuelve una respuesta de fallback solicitando reformular la consulta.

Este enfoque permite:
- Incorporar nuevas reglas sin modificar la API
- Reemplazar la lógica actual por mecanismos más avanzados (por ejemplo NLP o LLMs) sin cambios estructurales

---

## Persistencia

El microservicio persiste el historial de conversación de forma estructurada, separando claramente los conceptos de:

- Conversación
- Mensaje
- Rol del mensaje (usuario / asistente)
- Timestamp

La persistencia se implementa de forma simple, priorizando claridad del modelo y facilidad de testeo.

---

## Gestión de errores

El servicio contempla el manejo de errores en los siguientes escenarios:

- Entradas inválidas (validaciones de request)
- Conversaciones inexistentes
- Fallos en la comunicación con servicios externos
- Errores internos inesperados

Los errores se exponen mediante respuestas HTTP consistentes y mensajes claros, permitiendo a los consumidores del servicio reaccionar adecuadamente.

---

## Observabilidad

Aunque no se implementa una solución completa de monitoreo, el microservicio está diseñado para exponer métricas relevantes en un entorno productivo.

### Métricas técnicas sugeridas
- Tiempo de respuesta por endpoint
- Tasa de errores HTTP (4xx / 5xx)
- Latencia de llamadas a servicios externos
- Cantidad de llamadas fallidas a APIs externas

### Métricas funcionales sugeridas
- Cantidad de conversaciones creadas
- Mensajes procesados por conversación
- Uso de reglas de decisión (fallback vs integración externa)

Estas métricas permiten evaluar tanto la salud técnica del servicio como su comportamiento funcional dentro de un sistema conversacional.

---

## Escalabilidad

El microservicio está diseñado para ser **horizontalmente escalable**:

- Es stateless a nivel de procesamiento
- La persistencia permite desacoplar el estado de las instancias
- Puede escalarse mediante replicación y balanceo de carga

---

## Seguridad (opcional)

Por simplicidad, no se implementa autenticación en esta versión.

En un entorno productivo, el servicio podría protegerse mediante:
- JWT para autenticación entre servicios
- API Gateway como punto de entrada
- Validación de scopes o roles por endpoint

---

## Diagramas

Se incluyen diagramas de referencia para facilitar la comprensión del diseño:

- **Diagrama de componentes**: muestra la relación entre el microservicio y sus dependencias.
- **Diagrama de secuencia**: describe el flujo de procesamiento de un mensaje de usuario.

---

## Fuera de alcance

Los siguientes aspectos quedan explícitamente fuera del alcance de esta implementación:

- Procesamiento de lenguaje natural (NLP)
- Integración con modelos LLM
- Autenticación y autorización avanzadas
- Orquestación multi-servicio
- Comunicación en tiempo real (WebSockets)

Estos elementos podrían incorporarse en una evolución posterior del sistema sin requerir cambios estructurales significativos.
