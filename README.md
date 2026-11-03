# 🌴 La Palma | Sistema de Gestión Comercial

![Estado](https://img.shields.io/badge/Estado-En%20desarrollo-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/Frontend-React%20(previsto)-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

**Plataforma de gestión comercial diseñada para optimizar las operaciones de La Palma, un negocio dedicado a la comercialización de productos de limpieza y jarciería.**

El sistema busca centralizar el control de inventario, las ventas, las compras y las operaciones de caja en una solución modular, segura y escalable.

---

## 📖 Descripción

**La Palma Management** es un sistema de gestión comercial con funcionalidades previstas de **ERP (Enterprise Resource Planning)** y **POS (Point of Sale)**.

Su objetivo es digitalizar los procesos operativos del negocio, mejorar la trazabilidad de las transacciones y facilitar la toma de decisiones mediante información confiable y actualizada.

El proyecto se desarrolla inicialmente para un negocio físico y contempla futuras ampliaciones, como la gestión de entregas a domicilio.

## 🎯 Objetivos

- Centralizar la administración de productos, existencias y operaciones comerciales.
- Registrar ventas y mantener actualizado el inventario.
- Controlar ingresos, retiros y cierres de caja.
- Facilitar la administración de compras y proveedores.
- Gestionar permisos de acceso según el rol del usuario.
- Proporcionar información útil para el análisis del negocio.
- Construir una arquitectura preparada para nuevas funcionalidades.

---

## 🧩 Módulos del sistema

La tabla representa el **alcance funcional previsto**. Los módulos listados no implican que ya estén implementados.

| Módulo | Descripción | Estado |
|---|---|---|
| 🔐 Usuarios y permisos | Autenticación y autorización por roles | 📋 Pendiente |
| 📦 Productos | Catálogo, categorías y unidades de medida | 📋 Pendiente |
| 🏷️ Inventario | Existencias, movimientos y alertas | 📋 Pendiente |
| 🛒 Punto de venta | Ventas, notas, descuentos y promociones | 📋 Pendiente |
| 💰 Caja | Aperturas, retiros y cierres | 📋 Pendiente |
| 🚚 Compras y proveedores | Abastecimiento y compras | 📋 Pendiente |
| 🔄 Devoluciones | Devoluciones y ajustes | 📋 Pendiente |
| 📊 Reportes | Indicadores y análisis comercial | 📋 Pendiente |

**Leyenda:** 📋 Pendiente · 🚧 En desarrollo · ✅ Implementado

### Avances técnicos comprobados

- ✅ Repositorio Git y estructura inicial del monorepo.
- ✅ Backend Spring Boot y PostgreSQL para desarrollo mediante Docker Compose.
- ✅ Configuración diferenciada de perfiles de Spring (`dev`, `test`, `prod`).
- ✅ Flyway: migraciones V1, V2 y V3 probadas con PostgreSQL temporal.
- ✅ Validación automatizada de 39 tablas, 21 índices y segunda ejecución de Flyway sin duplicación.
- ✅ Pruebas locales con JUnit, Mockito, MockMvc y Testcontainers.
- 🚧 Revisión final de configuración, documentación y estructura modular del Sprint 0.

---

## 🛠️ Stack tecnológico

| Área | Tecnología |
|---|---|
| Backend | Java 21 + Spring Boot 4 |
| Frontend | React (previsto) |
| Base de datos | PostgreSQL 17 |
| Persistencia | Spring Data JPA / Hibernate |
| Migraciones | Flyway |
| API | REST |
| Contenedores | Docker / Docker Compose |
| Construcción backend | Maven Wrapper |
| Pruebas | JUnit, Mockito, MockMvc, Testcontainers |
| CI/CD | GitHub Actions (previsto) |
| Gestión | Scrum adaptado + Notion |

---

## 🏗️ Arquitectura

El proyecto es un **monorepo** con un backend concebido como **monolito modular**, organizado progresivamente por dominios de negocio.

Estructura relevante para la configuración local:

```text
lapalma-management/
├── apps/
│   └── backend/
│       ├── management/
│       │   ├── src/
│       │   ├── mvnw
│       │   └── pom.xml
│       ├── .env                 # Local; no versionar
│       ├── .env.example         # Plantilla sin secretos
│       ├── compose.yaml
│       └── README.md
├── .gitignore
└── README.md
```

La organización interna por módulos seguirá evolucionando durante el desarrollo. Este árbol muestra los archivos relevantes confirmados para el arranque; no pretende ser un inventario completo del repositorio.

---

## 🚀 Inicio rápido — Desarrollo local

### Requisitos

- Java 21.
- Docker con Docker Compose.
- Git.
- Bash para los ejemplos.
- Conexión a internet para resolver dependencias iniciales de Maven, si aún no están descargadas.

### 1. Entrar al directorio de infraestructura del backend

Desde la raíz del repositorio:

```bash
cd apps/backend
```

### 2. Preparar variables de entorno

**Solo si aún no existe un archivo `.env`:**

```bash
cp .env.example .env
```

Edita `apps/backend/.env` con tus valores locales. **No reemplaces un `.env` existente ni subas contraseñas reales al repositorio.**

La plantilla incluye nombres de variables para PostgreSQL y el backend. Antes de una instalación nueva, comprueba que los nombres coincidan con los consumidos por `compose.yaml` y los archivos `application-*.yml`.

### 3. Iniciar PostgreSQL

Desde `apps/backend`:

```bash
docker compose up -d
```

### 4. Cargar variables de entorno

En la misma terminal Bash, desde `apps/backend`:

```bash
set -a
source .env
set +a
```

### 5. Ejecutar el backend

```bash
cd management
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

El comando activa explícitamente el perfil `dev` para esta ejecución.

### 6. Verificar funcionamiento

Cuando el servicio inicie correctamente, comprueba el endpoint de información del sistema, si continúa disponible en tu implementación:

<http://localhost:8080/api/v1/system>

El puerto efectivo depende de `SERVER_PORT` y de la configuración de Spring.

### Ejecutar pruebas

Desde `apps/backend/management` y con Docker disponible:

```bash
./mvnw clean test
```

Las pruebas de integración utilizan PostgreSQL temporal mediante Testcontainers, con el perfil `test`; no requieren reutilizar la base de datos de desarrollo.

Para más detalles consulta la **[documentación del backend](apps/backend/README.md)**.

---

## 🔐 Seguridad de configuración

- Mantén los secretos en `apps/backend/.env` o en variables de entorno administradas; no los incluyas en Git.
- Versiona únicamente `apps/backend/.env.example` con valores ficticios.
- La regla `.gitignore` ignora `.env` y permite explícitamente `.env.example`.
- Usa credenciales distintas para desarrollo, pruebas y producción.
- Evita incluir contraseñas en capturas de pantalla, registros o reportes de errores.

---

## 📚 Documentación

| Documento | Descripción |
|---|---|
| [Backend](apps/backend/README.md) | Entornos, configuración, arranque y pruebas |
| Frontend | Se incorporará cuando se inicialice React |
| Notion | Requerimientos, historias y tablero Scrum |

La documentación funcional oficial se mantiene en Notion. La documentación técnica directamente relacionada con el código se versiona en el repositorio.

---

## 🗺️ Roadmap

- [ ] **Sprint 0:** cerrar revisión de infraestructura y configuración (S0-01 y S0-05 terminadas).
- [ ] Seguridad: usuarios, autenticación y permisos.
- [ ] Catálogo de productos y categorías.
- [ ] Gestión de inventarios.
- [ ] Registro de ventas y punto de venta.
- [ ] Operaciones de caja.
- [ ] Compras y proveedores.
- [ ] Devoluciones y ajustes.
- [ ] Reportes comerciales.
- [ ] Preparación para producción.

El orden de implementación se ajusta al backlog aprobado en Notion.

---

## 🔄 Versionado y desarrollo

El proyecto se encuentra en desarrollo inicial y no tiene una versión productiva publicada.

La estrategia de versionado y el historial de cambios se formalizarán antes de la primera entrega.

**Metodología:** Scrum adaptado al desarrollo individual.

---

<p align="center">
  <strong>🌴 La Palma</strong><br>
  <em>Tecnología para una gestión comercial más eficiente.</em>
</p>
