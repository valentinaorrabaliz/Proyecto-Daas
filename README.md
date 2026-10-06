# Sistema de Gestión de Transacciones Bancarias

**Institución:** Universidad Nacional de Jujuy (UNJu)
**Facultad:** Facultad de Ingeniería
**Materia:** Desarrollo y Arquitecturas Avanzadas de Software

### Integrantes del Proyecto

* **Lucia Fernández Zapatero**
* **Valentina Orrabaliz**

---

## 1. Descripción del Proyecto

El presente proyecto consiste en el diseño e implementación de una API RESTful robusta para la gestión de un sistema bancario.

Entre sus funcionalidades principales, el módulo transaccional permite la ejecución de depósitos y extracciones actualizando el saldo de las cuentas de forma segura y aplicando reglas avanzadas de control de límites acumulados. En paralelo, el módulo de consultas, auditoría y eventos desacoplados ofrece servicios esenciales para la operatividad bancaria:

* **Gestión de Transacciones:** Depósitos y extracciones con validación previa de saldo disponible y verificación de límites acumulados diarios según la condición del cliente (Cliente Individual vs. Grupo Familiar).
* **Consulta individual:** Obtención detallada de una transacción específica mediante su identificador único (UUID).
* **Historial por cuenta:** Recuperación del listado completo de movimientos asociados a una cuenta bancaria ordenados cronológicamente.
* **Auditoría por rango de fechas:** Consulta filtrada de transacciones dentro de una ventana temporal específica (`inicio` y `fin`).
* **Paginación por tipo de transacción:** Listado optimizado de movimientos (depósitos o extracciones) utilizando paginación en base de datos.
* **Notificaciones y Procesos Asíncronos:** Notificación automática de activación mediante eventos de dominio (`ClienteCreadoEvent`) en segundo plano sin demorar la respuesta de la API REST.

---

## 2. Arquitectura del Repositorio y Capas

La estructura del código fuente está organizada bajo una arquitectura en capas, asegurando una separación clara de responsabilidades:

```text
ar.edu.unju.fi.arquitecturas.tp2banco
│
├── config          # Configuraciones globales de Spring
├── controlador     # Controladores REST que exponen los endpoints
├── dto             # Objetos de Transferencia de Datos
├── enums            # Constantes del dominio
├── evento          # Eventos de dominio y listeners
├── excepcion       # Excepciones y manejador global de errores
├── modelo          # Entidades JPA
├── repositorio     # Interfaces Spring Data JPA
├── scheduler       # Tareas programadas
└── servicios       # Lógica de negocio y validaciones
    └── impl         # Implementaciones de los servicios
```

---

## 3. Tecnologías Utilizadas

**Lenguaje de Programación:**
Java 25 (OpenJDK / Eclipse Temurin)

**Framework Principal:**
Spring Boot 3.x

**Acceso a Datos y ORM:**
Spring Data JPA / Hibernate

**Base de Datos:**
H2 Database (en memoria para desarrollo y pruebas) / PostgreSQL

**Librerías de Soporte:**
Lombok (`@Getter`, `@Setter`, `@SuperBuilder`)

**Testing Unitario y Mocks:**
JUnit 5, Mockito

**Pruebas de API y Cliente HTTP:**
IntelliJ HTTP Client (`src/main/resources/http/`)

**Gestor de Dependencias y Construcción:**
Apache Maven
