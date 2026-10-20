## 🌍 Entornos de ejecución

El backend utiliza Spring Profiles para separar la configuración de desarrollo, pruebas y producción.

| Perfil | Archivo | Finalidad |
|---|---|---|
| `dev` | `application-dev.yml` | Desarrollo local |
| `test` | `src/test/resources/application-test.yml` | Pruebas automatizadas |
| `prod` | `application-prod.yml` | Producción |

La configuración compartida se encuentra en `application.yml`.

### Desarrollo

Desde la raíz del repositorio:

```bash
docker compose up -d

set -a
source .env
set +a

cd apps/backend
./mvnw spring-boot:run
```

El archivo `.env` debe establecer `SPRING_PROFILES_ACTIVE=dev`.

Alternativamente:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Pruebas

Las pruebas que cargan el contexto de Spring utilizan el perfil `test` mediante `@ActiveProfiles("test")`.

```bash
./mvnw test
```

Las pruebas de integración requerirán PostgreSQL aislado. Se incorporará Testcontainers para automatizar su creación y eliminación.

### Producción

La aplicación se empaqueta en un JAR ejecutable:

```bash
./mvnw clean package
```

Una vez verificado el artefacto y configurado el entorno productivo:

```bash
java -jar ruta/al/artefacto.jar --spring.profiles.active=prod
```

La ejecución debe realizarse mediante un servicio administrado o infraestructura de contenedores. No deben reutilizarse las credenciales ni la base de datos de desarrollo.

### Variables de entorno

Las variables necesarias se documentan en el archivo `.env.example` de la raíz del monorepo.

**Reglas de seguridad:**

- Nunca versionar `.env` ni las contraseñas reales.
- Mantener bases de datos separadas para desarrollo, pruebas y producción.
- No mostrar credenciales ni información sensible en los logs.
- Ejecutar las migraciones con procedimientos controlados.
- Mantener la configuración productiva en el entorno de despliegue.