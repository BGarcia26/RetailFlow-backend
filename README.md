# RetailFlow Backend

API REST en **Spring Boot 3 (Java 21)** que guarda los turnos cerrados de la app Android RetailFlow.
La app la consume con **Retrofit**.

## Arquitectura (Controller → Service → Repository)

```text
cl.rinconsur.retailflow.api/
├── controller/
│   ├── TurnoController.java      # Rutas HTTP del CRUD, valida el JSON con @Valid
│   └── HealthController.java     # GET /api/health (Render y para "despertar" el servicio)
├── service/
│   └── TurnoService.java         # Reglas de negocio: calcula el % de tránsito, valida compradores ≤ flujo
├── repository/
│   └── TurnoRepository.java      # JpaRepository: acceso a la tabla "turnos"
├── model/
│   └── Turno.java                # Entidad JPA: Hibernate crea la tabla a partir de ella
├── dto/
│   ├── TurnoRequest.java         # JSON que envía la app (sin id ni porcentaje)
│   └── TurnoResponse.java        # JSON que devuelve la API
└── exception/                    # 404 y 400 con un JSON { "error": "..." }
```

La base de datos y la tabla `turnos` **no se crean a mano**: Render crea la base PostgreSQL y
Hibernate crea la tabla al arrancar (`spring.jpa.hibernate.ddl-auto=update`).

## Endpoints

| Método | Ruta | Respuesta |
|---|---|---|
| GET | `/api/turnos` | 200, lista (más nuevo primero) |
| GET | `/api/turnos/{id}` | 200, o 404 si no existe |
| POST | `/api/turnos` | 201 con el turno creado, o 400 si el JSON es inválido |
| PUT | `/api/turnos/{id}` | 200 con el turno actualizado, 400 o 404 |
| DELETE | `/api/turnos/{id}` | 204, o 404 si no existe |
| GET | `/api/health` | 200 `{"status":"ok"}` |

Ejemplo de JSON para POST y PUT:

```json
{
  "dateLabel": "19/11/2026 (Jueves)",
  "shiftTime": "Tarde (16:30 hrs)",
  "totalFlow": 6, "buyersCount": 2,
  "childCount": 1, "youngCount": 2, "adultCount": 3, "seniorCount": 0,
  "soloCount": 1, "coupleCount": 1, "groupCount": 1,
  "accessSummary": "Acceso 1: 6 pers.",
  "notes": "Llovió en la tarde"
}
```

## Correr en el PC

Requiere JDK 21. Usa H2, una base en memoria, así que no hay que instalar PostgreSQL.

```bash
./gradlew bootRun
```

La API queda en `http://localhost:8080`. Desde el emulador de Android esa dirección es `http://10.0.2.2:8080`.

## Tests (JUnit 5)

```bash
./gradlew test
```

- `TurnoServiceTest`: reglas de negocio con el Repository simulado (Mockito).
- `TurnoControllerTest`: rutas, códigos HTTP y JSON con MockMvc.
- `TurnoRepositoryTest`: consultas reales contra H2 (`@DataJpaTest`).

## Desplegar en Render

1. Sube este proyecto a un repositorio de GitHub.
2. En Render: **New → Blueprint** y elige el repositorio. `render.yaml` crea la base `retailflow-db`
   (PostgreSQL) y el servicio `retailflow-api` (Docker), y conecta ambos con variables de entorno.
3. Cuando termine, la URL queda como `https://retailflow-api-xxxx.onrender.com`. Pruébala abriendo
   `/api/health` en el navegador.

Ten en cuenta el plan gratis:
- El servicio se "duerme" tras unos 15 minutos sin uso y la primera petición tarda cerca de un minuto
  en despertarlo. Antes de una demostración, abre `/api/health` en el navegador.
- La base PostgreSQL gratis caduca a los 30 días de creada.
