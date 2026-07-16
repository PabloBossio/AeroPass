# Notas — Aprendiendo Spring Boot con el proyecto Aerolinea API

Este archivo es un registro vivo de dudas, preguntas y conceptos que fueron surgiendo durante el desarrollo del proyecto. La idea es que sirva como referencia rápida propia — no memorices el código, quedate con el "para qué sirve esto" de cada entrada, y buscá la sintaxis exacta cuando la necesites (documentación oficial, un proyecto anterior tuyo, o una IA).

Se va a ir actualizando a medida que surjan nuevas dudas durante el desarrollo.

---

## Fundamentos de Spring Boot

**¿Qué es Spring Boot?**
No es "otro framework": es Spring Framework (que resuelve Inversión de Control / Inyección de Dependencias) + autoconfiguración (Spring Boot decide sensatamente cómo configurar cosas según lo que detecta en el classpath) + servidor embebido (Tomcat) + "starters" (paquetes de dependencias pre-armadas y compatibles entre sí, ej: `spring-boot-starter-web`).

**Inversión de Control / Inyección de Dependencias (IoC/DI)**
En vez de que una clase cree sus propias dependencias con `new`, se las inyectan desde afuera. Spring arma un contenedor (`ApplicationContext`) que escanea las clases marcadas como "beans" y las conecta entre sí automáticamente.

**Anotaciones "estereotipo"**: `@Component` (genérica), y sus especializaciones `@Service` (lógica de negocio), `@Repository` (acceso a datos), `@RestController` (capa HTTP). Todas le dicen a Spring "gestioná vos el ciclo de vida de esta clase". Las escanea `@ComponentScan`, que viene incluido dentro de `@SpringBootApplication`.

**Constructor injection** (recomendado sobre inyectar con `@Autowired` en el atributo): las dependencias se piden como parámetros del constructor. Hace las dependencias explícitas y facilita testear con mocks más adelante.

**Estructura de paquetes usada en el proyecto**: `controller`, `service`, `repository`, `model`, `dto`, `mapper`, `exception` — cada uno con una única responsabilidad.

---

## Persistencia (JPA / Hibernate / MySQL)

- JPA es la especificación estándar de Java para mapear objetos a tablas; Hibernate es la implementación que usa Spring Boot.
- `spring.jpa.hibernate.ddl-auto=update`: Hibernate crea/actualiza tablas según las entidades. Cómodo para aprender/prototipar; en producción se reemplaza por migraciones versionadas (Flyway/Liquibase).
- `BigDecimal` para valores monetarios, nunca `double`/`float` (errores de redondeo binario).
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: delega la generación del ID en el auto-incremento nativo de MySQL.
- `@Enumerated(EnumType.STRING)`: guarda el enum como texto, no como número ordinal — si reordenás el enum en el futuro, los datos existentes no se corrompen.
- Diseño de estados como un enum pensado como ciclo de vida real (`PROGRAMADO → EN_VUELO → FINALIZADO`, con `DEMORADO`/`CANCELADO` como alternativas) — pensar el dominio antes de escribir código.

**Spring Data JPA — derived query methods**: interfaces que extienden `JpaRepository<Entidad, TipoId>` ya traen `save()`, `findById()`, `findAll()`, etc. sin implementación. Además, métodos con nombres como `findByOrigenAndDestino(...)` se traducen automáticamente a una consulta JPQL, parseando el nombre del método.

**Bug real: `ddl-auto=update` es solo aditivo.** Al sacar un campo de una entidad (ej. `aerolinea` de `Vuelo`), Hibernate **nunca borra la columna vieja** de la tabla real — solo agrega columnas nuevas, nunca quita ni renombra. Si esa columna vieja era `NOT NULL`, los futuros `INSERT` (que ya no la mandan) fallan con un error de MySQL tipo `Field 'x' doesn't have a default value`. Hay que borrar la columna a mano (`ALTER TABLE tabla DROP COLUMN columna;` en phpMyAdmin/SQL). Además, filas viejas no reciben valores para columnas nuevas (quedan en `NULL` aunque la entidad diga `nullable = false`), lo que puede romper después al mapear esas filas viejas a un DTO (`NullPointerException` si el código asume que una relación nunca es null). Esta es una de las razones reales por las que en producción se usan migraciones versionadas (Flyway/Liquibase) en vez de `ddl-auto`.

**Relaciones JPA (`@ManyToOne`)**: `@JoinColumn(name = "...")` define la columna FK en la tabla. `fetch = FetchType.LAZY` (recomendado explícito, ya que el default de `@ManyToOne` en la especificación JPA es EAGER) hace que Hibernate no traiga la entidad relacionada hasta que se accede a ella con el getter. Esto funciona sin problema dentro de una misma request gracias a `spring.jpa.open-in-view=true` (default), que mantiene la sesión de Hibernate abierta durante toda la duración de la request — si se desactiva (recomendado en proyectos grandes, para forzar fetch explícito), acceder a una relación LAZY fuera de la transacción tira `LazyInitializationException`.

**Un service puede inyectar más de un repositorio.** Es normal cuando una regla de negocio involucra más de una entidad (ej. `VueloService` necesita `AvionRepository` para validar que `asientosDisponibles` no supere la `capacidad` del avión asignado).

---

## Seguridad (contraseñas, antes de llegar al módulo de JWT)

- **`spring-security-crypto`** es un artefacto separado del starter completo de Spring Security (`spring-boot-starter-security`). Da acceso a `BCryptPasswordEncoder` **sin** disparar la autoconfiguración que bloquea todos los endpoints con login automático (esa autoconfiguración depende de `spring-security-web`/`spring-security-config`, no de `spring-security-crypto`). Útil para encriptar contraseñas desde ya, antes de meterse con JWT.
- `passwordEncoder.encode(texto)` genera un hash irreversible (formato `$2a$10$...`); no se "desencripta", solo se compara con `passwordEncoder.matches(textoPlano, hash)` (esto se usa en el login, más adelante).
- **Nunca** devolver el password (ni el hash) en un DTO de respuesta — es exactamente el tipo de fuga que el patrón DTO existe para evitar.
- Igual que se fuerza `estado = PROGRAMADO` al crear un `Vuelo`, se fuerza `rol = USUARIO` al registrar un `Usuario` — nunca confiar en un rol que venga del cliente en el registro público. Crear un `ADMIN` es una operación aparte (a definir más adelante, probablemente restringida a otro admin ya autenticado).

**`@Configuration` + `@Bean`**: para registrar como bean de Spring una clase que **no es tuya** (no la podés anotar con `@Component`/`@Service`, como `BCryptPasswordEncoder` de una librería externa). Se escribe una clase `@Configuration` con métodos `@Bean` — cada método devuelve un objeto que Spring gestiona y que después se puede inyectar por constructor en cualquier otra clase, igual que un bean propio.

**Nuevas validaciones de Bean Validation**: `@Email` (formato de email válido) y `@Size(min = ..., max = ...)` (longitud de un String) — mismo mecanismo ya conocido, reglas distintas.

---

## Lombok

- `@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder`: para **entidades** (`@Entity`).
- `@Data`: solo para **DTOs**, nunca (o con mucho cuidado) para entidades JPA. Motivo: `@Data` genera `equals`/`hashCode`/`toString` con todos los campos, lo cual es riesgoso en entidades por los proxies de Hibernate (lazy loading) y por relaciones bidireccionales (`toString()` recursivo → `StackOverflowError`).
- `@Builder`: genera el patrón Builder para construir objetos de forma legible y nombrada (`Vuelo.builder().origen("BUE")...build()`), evitando constructores con muchos parámetros posicionales ambiguos.
- `@NoArgsConstructor` es obligatorio en toda entidad JPA porque Hibernate lo usa por reflection para instanciarlas.
- Recordar habilitar "Annotation Processing" en IntelliJ (Settings → Build, Execution, Deployment → Compiler → Annotation Processors) para que Lombok funcione bien en el IDE.

---

## Capa de servicio

- Ahí vive la lógica de negocio (validaciones que dependen del dominio, no solo del formato). La entidad describe la forma de los datos; el controller traduce HTTP; el servicio decide "esto tiene sentido de negocio o no".
- `@Transactional`: marca que un método corre dentro de una transacción de base de datos — si algo falla a mitad de camino (relevante cuando una operación toca más de una tabla, ej. reservas), Hibernate hace rollback de todo.
- Mantener validaciones de negocio en el servicio aunque "ya estén" validadas en el DTO de entrada: es defensa en profundidad — el servicio puede ser invocado desde otros lugares además del controller (tests, batch jobs, otros servicios) que no pasan por la validación del DTO.

---

## DTOs y API REST

- **Nunca exponer la entidad JPA directamente en la API.** Motivos: control de qué puede setear el cliente (ej. no debería poder mandar `estado` al crear un vuelo), evitar problemas de serialización con relaciones/lazy loading, y desacoplar el contrato público de la API del esquema interno de base de datos.
- Patrón DTO + Mapper: clases de request/response separadas de la entidad, con una clase `Mapper` (métodos estáticos, sin anotaciones de Spring — no todo necesita ser un bean) que traduce entre ambas.
- `@Valid` en el parámetro del controller dispara las validaciones de Bean Validation del DTO.
- `@PathVariable` (parte de la URL, identifica un recurso específico) vs `@RequestParam` (query param, para filtros/búsquedas opcionales).
- `ResponseEntity<T>` permite controlar el código de estado HTTP explícitamente: `201 Created` al crear un recurso (no `200 OK`, que es para lecturas), `404 Not Found` cuando se busca por id y no existe.
- Encadenar `Optional.map(...).map(...).orElse(...)` es el estilo funcional para manejar ausencia de valor sin `if/else` ni riesgo de `NullPointerException`.

**Validación cruzada entre campos (cross-field)**: las anotaciones estándar (`@NotNull`, `@Positive`, etc.) validan un campo aislado. Para reglas que comparan dos campos entre sí (ej: fechaLlegada posterior a fechaSalida), se define una anotación propia a **nivel de clase** (`@Target(ElementType.TYPE)`) con su propio `ConstraintValidator`.

**El patrón DTO/Mapper no es rígido.** `Reserva` no tiene un `toEntity` en su mapper porque el service ya recibe los ids (`usuarioId`, `vueloId`) directo y arma la entidad buscando las relaciones reales — no hace falta forzar el mismo mapeo 1:1 que usan `Vuelo`/`Avion` si no aporta nada en ese caso.

---

## Reservas: transacciones multi-entidad y snapshots

- **Snapshot histórico**: `Reserva.precioPagado` copia el precio del vuelo *al momento de reservar*, en vez de leer `vuelo.getPrecio()` dinámicamente. El precio de un vuelo puede cambiar después; lo que alguien pagó en el pasado es un hecho histórico que no debe cambiar con él. A veces duplicar un dato a propósito (en vez de todo "normalizado") es el modelado correcto — es una fotografía de un momento, no una referencia viva.
- **Acá `@Transactional` importa de verdad**: `crearReserva` escribe en dos tablas (descuenta `asientosDisponibles` del vuelo Y crea la reserva). Sin la transacción, un fallo a mitad de camino dejaría un asiento "perdido" sin ninguna reserva que lo explique.
- **Race condition detectada y resuelta con bloqueo pesimista**: si dos requests reservan el último asiento de un vuelo *al mismo tiempo*, ambas pueden leer "queda 1" antes de que cualquiera escriba el descuento, y las dos tendrían éxito — sobreventa. Solución aplicada: un `SELECT ... FOR UPDATE` que bloquea la fila del vuelo hasta que termina la transacción; cualquier otra transacción que quiera tocar esa misma fila queda esperando en la cola (se ve clarísimo probándolo: la segunda request en Postman directamente no responde hasta que la primera libera el lock). Alternativa que no se implementó acá pero existe: bloqueo optimista (`@Version`), que en vez de bloquear detecta el conflicto recién al momento de escribir y falla la segunda transacción para que se reintente — mejor para baja contención, pero requiere manejar el reintento.

**Bug real: `@Lock` sobre un método heredado no funciona.** No se puede poner `@Lock` directamente sobre `findById` (viene ya implementado por `JpaRepository`) — hace falta declarar un método propio con `@Query` y recién ahí colgarle `@Lock`.

**Bug real: MariaDB (XAMPP) no es 100% compatible con la sintaxis de bloqueo que genera Hibernate para "MySQL".** Con `@Lock(LockModeType.PESSIMISTIC_WRITE)` sobre una consulta JPQL, Hibernate generó `SELECT ... FOR UPDATE OF v1_0` — sintaxis que MySQL 8+ soporta pero que MariaDB (a pesar de ser de la misma familia) nunca implementó, y tira un error de sintaxis SQL (1064). Intentar arreglarlo fijando un dialecto específico de MariaDB (`hibernate-community-dialects` + `spring.jpa.properties.hibernate.dialect=...`) llevó a un `ClassNotFoundException` (la clase no estaba disponible en esa versión/configuración). La solución que sí funcionó: escribir la consulta como **SQL nativo** en vez de JPQL, con `nativeQuery = true` — así Hibernate no traduce nada y ejecuta exactamente el SQL que escribiste (`FOR UPDATE`, sin el `OF`, que MariaDB sí entiende):
```java
@Query(value = "SELECT * FROM vuelos WHERE id = :id FOR UPDATE", nativeQuery = true)
Optional<Vuelo> buscarPorIdConBloqueo(@Param("id") Long id);
```
Ojo de sintaxis: con más de un atributo en `@Query` (`value` y `nativeQuery`), Java exige nombrar **todos** los atributos explícitamente — no alcanza con poner la cadena SQL sola entre paréntesis.
- Derived query methods pueden "atravesar" relaciones: `findByUsuarioId(Long usuarioId)` en `ReservaRepository` filtra por el `id` del `Usuario` relacionado (vía `@ManyToOne`), generando el JOIN automáticamente.
- `PUT` para acciones de cambio de estado sobre un recurso existente (ej. `PUT /api/reservas/{id}/cancelar`), a diferencia de `POST` que crea un recurso nuevo. (Un purista de REST diría que `PATCH` es más preciso para modificaciones parciales — matiz fino, `PUT` es aceptado en la práctica.)

---

## Manejo global de excepciones

- `@RestControllerAdvice` + `@ExceptionHandler`: intercepta cualquier excepción que se escape de los controllers, en un solo lugar centralizado, en vez de try/catch repetido en cada método.
- Spring elige el `@ExceptionHandler` más específico automáticamente (no importa el orden de los métodos en la clase).
- Excepciones propias (ej. `ReglaDeNegocioException`) en vez de usar `IllegalArgumentException` genérica, para poder distinguir tipos de error y mapearlos a códigos HTTP con sentido.
- **Seguridad**: nunca devolver el stack trace ni el mensaje real de una excepción inesperada al cliente. Dos líneas de defensa:
  1. Tu propio `@RestControllerAdvice` construye la respuesta de error a mano (nunca incluye trace porque nunca lo escribís ahí).
  2. `server.error.include-stacktrace=never` en `application.properties`, como red de seguridad para casos que ni tu `@RestControllerAdvice` llega a interceptar (ej. errores en filtros de bajo nivel, antes de llegar al controller).
- Para errores realmente inesperados (`Exception` genérica): loguear el detalle real del lado del servidor (`log.error(...)`) pero devolver al cliente un mensaje genérico.

**Bug real encontrado: `FieldError` vs `ObjectError`.** Al validar, Spring separa los errores en dos tipos: `FieldError` (atado a un campo puntual, ej. `@Positive` en `precio`) y `ObjectError` (atado a la clase entera, ej. una validación cruzada como `@FechasValidas` con `@Target(TYPE)`). `getFieldErrors()` en el `BindingResult` **solo** trae los `FieldError` — si armás la lista de detalles con eso, los errores de validaciones a nivel de clase quedan invisibles (lista vacía) aunque el request sí se rechace. Solución: usar `getAllErrors()` (trae ambos tipos, ya que `FieldError` es subclase de `ObjectError`) y distinguir con `instanceof FieldError fieldError` para saber si mostrar el nombre del campo o no.

**Regla general: cada tipo de excepción tiene su código HTTP semánticamente correcto, no todo es `400` o `500`.** Cualquier excepción sin un `@ExceptionHandler` específico cae en el genérico (`Exception` → `500`), aunque la causa real sea un error del cliente. Handlers agregados a medida que se fueron encontrando estos casos, todos con el mismo esqueleto (armar `ErrorResponseDto`, elegir el status correcto):
- `RecursoNoEncontradoException` (propia) → `404 Not Found` — ej. buscar un `Avion`/`Usuario`/`Vuelo` por un id que no existe.
- `HttpMessageNotReadableException` → `400 Bad Request` — JSON mal formado (ej. mandar un array `[ ]` en vez de un objeto `{ }`).
- `MethodArgumentTypeMismatchException` → `400 Bad Request` — un `@PathVariable`/`@RequestParam` con un tipo incorrecto (ej. mandar texto donde se espera un `Long`). Trae `ex.getValue()` y `ex.getName()` para armar un mensaje útil.
- `HttpRequestMethodNotSupportedException` → `405 Method Not Allowed` — pedir un verbo HTTP (GET/POST/PUT/DELETE) que esa ruta puntual no soporta. Trae `ex.getMethod()`.

---

## Herramientas / flujo de trabajo

- **Postman**: cuidado con el dropdown de método (GET por defecto) — si no lo cambiás a POST/PUT/DELETE explícitamente, tu request pega contra el endpoint equivocado y puede darte una respuesta "válida" (200 OK) que no es la que esperabas.
- HikariCP: pool de conexiones a la base de datos que usa Spring Boot por defecto (reutiliza conexiones en vez de abrir una nueva por request).

---

## Próximos temas pendientes (roadmap)

1. Entidad `Avion` (relación `@ManyToOne` desde `Vuelo`) — decisión de diseño pendiente: ¿entidad propia o campos sueltos en `Vuelo`?
2. Entidad `Usuario` con roles (`ADMIN` / `USUARIO`)
3. Entidad `Reserva` (relaciona `Usuario` + `Vuelo`, lógica de negocio con transacciones reales)
4. Seguridad con Spring Security + JWT sobre `Usuario`/roles
5. Testing con JUnit + Mockito
6. Documentación con Swagger/OpenAPI
7. Repaso final y buenas prácticas
