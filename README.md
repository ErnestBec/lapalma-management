# 🌴 La Palma | Sistema de Gestión Comercial

![Estado](https://img.shields.io/badge/Estado-En%20desarrollo-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/Frontend-React-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

**Plataforma de gestión comercial diseñada para optimizar las operaciones de La Palma, un negocio dedicado a la comercialización de productos de limpieza y jarciería.**

El sistema busca centralizar el control de inventario, las ventas, las compras y las operaciones de caja en una solución modular, segura y escalable.

---

## 📖 Descripción

**La Palma Management** es un sistema de gestión comercial con funcionalidades de **ERP (Enterprise Resource Planning)** y **POS (Point of Sale)**.

Su objetivo es digitalizar los procesos operativos del negocio, mejorar la trazabilidad de las transacciones y facilitar la toma de decisiones mediante información confiable y actualizada.

El proyecto se desarrolla inicialmente como una solución para un negocio físico, contemplando futuras ampliaciones como la gestión de entregas a domicilio.

## 🎯 Objetivos

- Centralizar la administración de productos, existencias y operaciones comerciales.
- Registrar ventas y mantener actualizado el inventario.
- Controlar ingresos, retiros y cierres de caja.
- Facilitar la administración de compras y proveedores.
- Gestionar los permisos de acceso según el rol del usuario.
- Proporcionar información útil para el análisis del negocio.
- Construir una arquitectura preparada para nuevas funcionalidades.

---

## 🧩 Módulos del sistema

La siguiente tabla representa el alcance funcional previsto. Los módulos listados no implican que ya estén implementados.

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

### Avances técnicos

- ✅ Repositorio Git inicializado.
- ✅ Estructura inicial del monorepo.
- ✅ Backend Spring Boot ejecutándose localmente.
- ✅ PostgreSQL mediante Docker Compose.
- ✅ Endpoint de información del sistema.
- 🚧 Configuración de entornos y pruebas automatizadas.

---

## 🛠️ Stack tecnológico

| Área | Tecnología |
|---|---|
| Backend | Java 21 + Spring Boot 4 |
| Frontend | React (pendiente) |
| Base de datos | PostgreSQL 17 |
| Persistencia | Spring Data JPA / Hibernate |
| Migraciones | Flyway |
| API | REST |
| Contenedores | Docker / Docker Compose |
| Construcción backend | Maven Wrapper |
| CI/CD | GitHub Actions (previsto) |
| Gestión | Scrum adaptado + Notion |

---

## 🏗️ Arquitectura

El proyecto utiliza un **monorepo con un backend de arquitectura monolítica modular**, organizado por dominios de negocio.

```text
lapalma-management/
├── apps/
│   ├── backend/
│   │   ├── src/
│   │   ├── pom.xml
│   │   └── README.md
│   └── frontend/           # Próxima etapa
├── infrastructure/
├── docs/
│   └── adr/
├── .github/
│   └── workflows/
├── .env.example
├── .gitignore
├── compose.yaml
└── README.md
```

La estructura seguirá evolucionando conforme se desarrollen los módulos.

---

## 🚀 Inicio rápido — Desarrollo local

### Requisitos

- Java 21.
- Docker y Docker Compose.
- Git.
- Bash (para los comandos de ejemplo).

### 1. Configurar variables

Desde la raíz del proyecto:

```bash
cp .env.example .env
```

Edita `.env` con tus credenciales y el puerto local configurado para PostgreSQL.

### 2. Iniciar PostgreSQL

```bash
docker compose up -d
```

### 3. Cargar variables de entorno

```bash
set -a
source .env
set +a
```

### 4. Ejecutar el backend

```bash
cd apps/backend
./mvnw spring-boot:run
```

### 5. Verificar funcionamiento

Consultar:

`http://localhost:8080/api/v1/system`

Para información detallada, configuración y ejecución del backend, consultar:

**[📘 Documentación del backend](apps/backend/README.md)**

> Los comandos se ejecutan desde la raíz del repositorio, salvo que se indique lo contrario.

---

## 📚 Documentación

| Documento | Descripción |
|---|---|
| [Backend](apps/backend/README.md) | Instalación, configuración, ejecución y pruebas |
| Frontend | Se agregará al inicializar React |
| `docs/adr/` | Decisiones de arquitectura |
| Notion | Requerimientos, historias de usuario y tablero Scrum |

La documentación funcional oficial se mantiene en Notion. La documentación técnica directamente relacionada con el código se versiona en este repositorio.

---

## 🗺️ Roadmap

- [ ] **Sprint 0:** completar configuración técnica y pruebas iniciales.
- [ ] Seguridad: usuarios, autenticación y permisos.
- [ ] Catálogo de productos y categorías.
- [ ] Gestión de inventarios.
- [ ] Registro de ventas y punto de venta.
- [ ] Operaciones de caja.
- [ ] Compras y proveedores.
- [ ] Devoluciones y ajustes.
- [ ] Reportes comerciales.
- [ ] Preparación para producción.

El orden de implementación se ajustará al backlog aprobado en Notion.

---

## 🔄 Versionado y desarrollo

El proyecto se encuentra en desarrollo inicial y todavía no tiene una versión productiva publicada.

La estrategia de versionado y el historial de cambios se formalizarán antes de la primera entrega.

**Metodología:** Scrum adaptado al desarrollo individual.

---

<p align="center">
  <strong>🌴 La Palma</strong><br>
  <em>Tecnología para una gestión comercial más eficiente.</em>
</p>