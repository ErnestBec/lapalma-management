# 🌴 La Palma Management — Backend

Backend de La Palma Management construido con **Java 21**, **Spring Boot 4**, **PostgreSQL 17** y **Flyway**. Este documento describe la configuración local, los perfiles y las pruebas de integración.

## 📁 Estructura relevante

```text
apps/backend/
├── .env                 # Configuración local, ignorada por Git
├── .env.example         # Plantilla sin credenciales reales
├── compose.yaml         # PostgreSQL local mediante Docker Compose
├── README.md
└── management/
    ├── mvnw
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/
        │   └── resources/
        │       └── db/migration/
        │           ├── V1__initialize_database.sql
        │           ├── V2__create_business_tables.sql
        │           └── V3__create_indexes.sql
        └── test/
```

## 🌍 Entornos de ejecución

El backend usa **Spring Profiles** para separar la configuración.

| Perfil | Configuración | Finalidad |
|---|---|---|
| `dev` | `application-dev.yml` | Desarrollo local |
| `test` | `src/test/resources/application-test.yml` | Pruebas automatizadas |
| `prod` | `application-prod.yml` | Producción, pendiente de validar antes de despliegue |

La configuración compartida está en `application.yml`. Los archivos de configuración de la aplicación se encuentran dentro del módulo `management`.

## 🛠️ Requisitos

- Java 21.
- Docker y Docker Compose.
- Git y Bash.
- Maven Wrapper incluido en `management/mvnw` (no es obligatorio instalar Maven globalmente).

## 🚀 Desarrollo local

### 1. Preparar el entorno

Desde la **raíz del repositorio**:

```bash
cd apps/backend
```

Si todavía no existe `.env`:

```bash
cp .env.example .env
```

Edita los valores locales sin utilizar credenciales reales en `.env.example`. **No sobrescribas un `.env` existente.**

Variables documentadas en la plantilla:

| Variable | Uso previsto |
|---|---|
| `SERVER_PORT` | Puerto HTTP del backend |
| `POSTGRES_DB` | Nombre de la base PostgreSQL de desarrollo |
| `POSTGRES_USER` | Usuario PostgreSQL de desarrollo |
| `POSTGRES_PASSWORD` | Contraseña local de PostgreSQL |
| `DB_HOST` | Host al que se conecta Spring Boot |
| `DB_PORT` | Puerto publicado para PostgreSQL |
| `TEST_DB_HOST`, `TEST_DB_PORT`, `TEST_DB_NAME`, `TEST_DB_USER`, `TEST_DB_PASSWORD` | Variables existentes en la plantilla local para pruebas; la suite actual de integración usa Testcontainers y no presupone su utilización |

**Nota de compatibilidad:** contrastar estas variables con `compose.yaml` y los archivos `application-*.yml` antes de afirmar que una instalación recién clonada funciona sin ajustes. No se ha revisado el contenido efectivo de `compose.yaml` en esta documentación.

### 2. Iniciar PostgreSQL

Desde `apps/backend`:

```bash
docker compose up -d
```

### 3. Cargar variables

En la misma terminal Bash:

```bash
set -a
source .env
set +a
```

### 4. Ejecutar Spring Boot

```bash
cd management
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

El perfil `dev` se activa de forma explícita. Si usas VS Code o un IDE, configura el directorio de trabajo y las variables de entorno equivalentes en su ejecución.

### 5. Comprobar la aplicación

Si el endpoint de sistema sigue implementado, consulta:

<http://localhost:8080/api/v1/system>

Ajusta el puerto si `SERVER_PORT` utiliza otro valor.

## 🗃️ PostgreSQL y migraciones Flyway

Las migraciones de base de datos del módulo están en:

`management/src/main/resources/db/migration/`

| Migración | Propósito |
|---|---|
| `V1__initialize_database.sql` | Crear el esquema `lapalma` |
| `V2__create_business_tables.sql` | Crear las tablas de negocio |
| `V3__create_indexes.sql` | Crear los índices definidos en el diseño |

Las pruebas de integración han verificado:

- 39 tablas en el esquema `lapalma`.
- 21 índices con el prefijo `ix_`.
- Tres versiones aplicadas con éxito en `public.flyway_schema_history`.
- Segunda ejecución de Flyway sobre la misma base de pruebas sin migraciones adicionales.

Estas pruebas comprueban el **conteo y ejecución** de las migraciones; aún se requiere revisión específica de la semántica de todas las claves, restricciones y relaciones.

## 🧪 Pruebas automatizadas

La suite utiliza **JUnit**, **Mockito**, **MockMvc** y **Testcontainers**.

Desde `apps/backend/management`:

```bash
./mvnw clean test
```

Para ejecutar únicamente la prueba de integración del contexto y la base de datos:

```bash
./mvnw test -Dtest=LaPalmaApplicationTest
```

**Requisito:** Docker debe estar disponible para que Testcontainers pueda crear PostgreSQL temporal.

Las pruebas que requieren el contexto completo usan el perfil `test` con `@ActiveProfiles("test")`. Los ejemplos didácticos de MockMvc que emplean `ProductController` usan el perfil específico `mvc-example` para no interferir con el arranque completo de Spring Boot.

**Importante:** los controladores, DTO e interfaces didácticos almacenados bajo `src/test/java` no representan todavía funcionalidades productivas del catálogo.

## 📦 Empaquetado y producción

Para construir el artefacto ejecutable, desde `apps/backend/management`:

```bash
./mvnw clean package
```

El despliegue productivo **no está aprobado ni configurado como entrega lista para operar**. Antes de desplegar se deben verificar las variables del entorno `prod`, secretos, conectividad, respaldos, permisos, migraciones y controles de seguridad.

El archivo JAR generado se encuentra normalmente bajo `target/`, con nombre según la configuración de Maven. No se documenta un nombre fijo hasta verificar el artefacto generado.

## 🔐 Seguridad

- Nunca versionar `.env` ni credenciales reales.
- Mantener `.env.example` solo con marcadores y valores ficticios.
- Usar credenciales diferentes para desarrollo, pruebas y producción.
- No imprimir contraseñas ni tokens en logs o capturas.
- Revisar periódicamente `.gitignore` y archivos rastreados por Git.
- Ejecutar migraciones de producción con un procedimiento controlado.

## 📚 Documentación complementaria

- [README principal](../../README.md)
- Requerimientos, DER y tablero Scrum de La Palma: documentados en Notion.

## 📋 Estado actual

- **S0-01:** migraciones de PostgreSQL y Flyway — terminado.
- **S0-05:** infraestructura de pruebas e integración — terminado.
- **S0-03:** estructura modular — en revisión.
- **S0-04:** configuración local y documentación — en revisión.

Los módulos funcionales de ventas, inventario, compras, caja y usuarios todavía están pendientes de implementación completa.
