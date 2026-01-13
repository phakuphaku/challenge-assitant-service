# challenge-assitant-service

Repositorio correspondiente a un challenge técnico para el diseño e implementación de un microservicio Java, enfocado en un sistema conversacional distribuido.

Este repositorio implementa un microservicio conceptual denominado **Conversation Orchestrator Service**, cuyo objetivo es representar un componente central dentro de una arquitectura de asistentes virtuales empresariales.

El alcance funcional se mantiene deliberadamente acotado para priorizar claridad arquitectónica, extensibilidad y buenas prácticas de diseño.

## Índice
- Descripción general del proyecto: Propósito del challenge y objetivo del microservicio.
- Rol del microservicio: Responsabilidades principales del Conversation Orchestrator Service dentro de una arquitectura conversacional.
- Decisión de diseño principal: Uso de la API externa yesno.wtf como simulación de un motor de decisión.
- API propuesta: Endpoints REST expuestos, responsabilidades y contratos de entrada/salida.
- Lógica del asistente: Reglas de procesamiento de mensajes y comportamiento de fallback.
- Persistencia:  Modelo de datos y criterios de diseño para el almacenamiento del historial conversacional.
- Gestión de errores:  Estrategia de manejo de errores y respuestas HTTP.
- Observabilidad: Métricas técnicas y funcionales sugeridas para entornos productivos.
- Escalabilidad: Consideraciones de diseño para escalado horizontal.
- Seguridad (opcional): Enfoque y posibles extensiones de seguridad no implementadas.
- Diagramas:Diagramas de componentes y de secuencia incluidos en el proyecto.
- Estructura del proyecto: Organización por capas y responsabilidades de cada paquete.
- Fuera de alcance: Funcionalidades explícitamente excluidas de esta implementación.
- Estado actual de la implementación: Qué está implementado hoy y cuáles son los próximos pasos planificados.

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

El microservicio persiste el historial de conversación utilizando JPA, con un modelo simple compuesto por:

- Conversation
- Message

Cada conversación contiene una colección de mensajes asociados.  
El modelo prioriza claridad y facilidad de testeo.

> Aspectos como rol del mensaje (usuario / asistente), timestamps y metadatos adicionales se consideran extensiones naturales del modelo, pero quedan fuera del alcance de esta implementación inicial.

---

## Gestión de errores

El servicio maneja de forma explícita los siguientes escenarios:

- Conversaciones inexistentes (404)
- Requests inválidos (400)
- Flujo conversacional no compatible con reglas definidas (fallback)

El manejo de errores se implementa a nivel de controller, priorizando respuestas HTTP claras y consistentes.

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

## Seguridad
No se implementa autenticación en esta versión.

---

## Diagramas

Se incluyen diagramas de referencia para facilitar la comprensión del diseño:

- **Diagrama de componentes**: muestra la relación entre el microservicio y sus dependencias.

Client
  → ConversationController
    → ConversationService
      → Repositories (Conversation / Message)
      → External API (yesno.wtf)

> **Nota sobre el alcance**
> La lógica del ConversationService se encuentra abstraída y validada mediante tests con mocks.
> La implementación concreta se dejó fuera del alcance para priorizar el diseño de contratos, persistencia y flujo conversacional, alineado con los objetivos del challenge.

- **Diagrama de secuencia**: describe el flujo de procesamiento de un mensaje de usuario.
### Caso de uso: Envío de mensaje con signo de pregunta (descripción textual)

1. El cliente envía un mensaje a: POST /api/v1/conversations/{id}/messages
2. El ConversationController recibe la request y delega el procesamiento al ConversationService.
3. El servicio:
* * Recupera la conversación desde ConversationRepository.
* * Persiste el mensaje del usuario.
4. Se evalúa el contenido del mensaje:
* - Si contiene un signo de pregunta (?), se considera una pregunta cerrada.
5. El servicio realiza una llamada HTTP a la API externa yesno.wtf para obtener una respuesta.
6. La respuesta externa es:
* - Persistida como mensaje del asistente.
* - Incluida en la respuesta al cliente.
7. El controller devuelve un 200 OK con el estado actualizado de la conversación.

### Caso alternativo: Mensaje sin signo de pregunta

1. El flujo inicial es el mismo hasta la evaluación del contenido.
2. Al no detectar una pregunta cerrada:
* * Se genera una respuesta de fallback.
* * Se persiste el mensaje de fallback.
3. Se devuelve la conversación actualizada sin llamar a servicios externos.
---

## Estructura del proyecto

El microservicio sigue una estructura simple por capas, priorizando claridad y facilidad de evolución.

- controller: expone la API REST y contiene la lógica de orquestación conversacional
- repository: capa de persistencia (JPA)
- model: entidades de dominio (Conversation, Message)
- dto: contratos de entrada y salida de la API
- client: integración HTTP con servicios externos (yesno.wtf)
- config: configuración de infraestructura básica
- exception: manejo de errores a nivel de controller

> Nota: para mantener el scope acotado del challenge, la lógica de orquestación se encuentra actualmente en la capa controller. La introducción de una capa service queda planificada como mejora evolutiva.

> Nota: todo el código fuente y los contratos de la API están escritos en inglés, siguiendo convenciones habituales mientras que la documentación se presenta en español para mas claridad.

---
## Scope y Decisiones tomadas y fuera de alcance”

- El core actúa como Conversation Orchestrator
- Utiliza 
  - Persistencia implementada con JPA (Conversation + Message)
  - API REST versionada
  - DTOs desacoplados del modelo

Los siguientes aspectos quedan explícitamente fuera del alcance de esta implementación:

- NLP o procesamiento semántico avanzado
- Integración con LLMs
- Autenticación y autorización
- Observabilidad avanzada
- Comunicación en tiempo real (WebSockets)
- Estrategias conversacionales complejas
- Resiliencia avanzada en integraciones externas (retry, circuit breaker)

Estos elementos podrían incorporarse en una evolución posterior del sistema sin requerir cambios estructurales significativos.

---
## Estado actual de la implementación

El microservicio implementa una versión funcional mínima del Conversation Orchestrator, cubriendo los aspectos obligatorios del desafío:

- Creación de conversaciones mediante API REST
- Envío y persistencia de mensajes asociados a una conversación
- Regla básica de decisión:
  - Preguntas cerradas (`?`) → integración con API externa
  - Otros mensajes → respuesta fallback
- Integración HTTP con yesno.wtf como simulación de un motor de decisión
- Pruebas automatizadas ejecutables con `mvn test`, que validan:
  - Creación de conversaciones
  - Envío de mensajes
  - Persistencia del historial
  - Integración externa mediante mocks

La implementación prioriza claridad de diseño, separación de responsabilidades y testabilidad, dejando explícitamente documentadas las extensiones futuras.

