# Facturación Multi-tenant

## Descripción

Este es un sistema de facturación multitenant, diseñado para manejar múltiples clientes con bases de datos independientes. Cada cliente se encuentra aislado en su propio esquema dentro de la base de datos, permitiendo una gestión eficiente y segura de la información.

El sistema incluye funcionalidades básicas como la gestión de clientes y direcciones, con la capacidad de agregar, editar, listar y eliminar clientes. También se contempla la creación dinámica de esquemas y la configuración de conexiones a bases de datos específicas por tenant.

## Características

- **Multitenancy:** Cada cliente (tenant) tiene su propio esquema en la base de datos.
- **Gestión de Clientes:** CRUD completo para clientes.
- **Gestión de Direcciones:** Los clientes pueden tener múltiples direcciones.
- **Escalabilidad:** El sistema está diseñado para escalar añadiendo nuevos tenants de forma sencilla.
- **Configuración de Esquemas Dinámicos:** Se crean esquemas y tablas para cada nuevo cliente.

## Estructura del Proyecto

### Backend

El backend está basado en **Spring Boot** y está estructurado de la siguiente manera:

1. **CustomerService:** Maneja la lógica para crear, actualizar, listar y eliminar clientes.
2. **TenantService:** Administra la creación de nuevos tenants y sus esquemas asociados.
3. **Database Configurations:** Configuración para manejo dinámico de esquemas usando `TenantContextHolder`.

### Docker

El proyecto utiliza Docker para facilitar la ejecución en entornos aislados:

- **`docker-compose.yml`**: Define los servicios necesarios (base de datos y backend).
- **`Dockerfile`**: Utiliza Maven para la compilación y Amazon Corretto para la ejecución del backend.

## Requisitos

- **Java 21**
- **Docker**
- **PostgreSQL** como base de datos.
- **Maven** para gestión de dependencias y construcción del proyecto.

## Configuración

### Variables de Entorno

Asegúrate de configurar las variables necesarias en el archivo `src/main/resources/application.properties`:

```properties
spring.datasource.username=usuario
spring.datasource.password=contrasena
```

Tienen que ser las mismas credenciales que en el archivo `docker-compose.yml`

## Docker
Para ejecutar el proyecto con Docker, sigue estos pasos:

1. Construir y levantar los contenedores:

```bash
docker-compose up --build
```
2. Acceder al servicio:

El servicio backend estará disponible en http://localhost:3000.

La base de datos estará disponible en el puerto 5431 de la máquina local por si se necesita acceder a la misma.

## Crear un Tenant
Para crear un nuevo tenant:

1. Realiza una petición POST a /api/tenants con los siguientes datos:

```json
{
  "name": "Empresa A",
  "schema": "company_a"
}
```
2. Se creará automáticamente un nuevo esquema en la base de datos para el tenant.

## Endpoints

Se incluye un archivo .json para importar en Postman y realizar pruebas a los endpoints.

[Test-Multitenant](/Test-Multitenant.postman_collection.json)


### Clientes
- GET /api/customers: Lista de clientes.
- POST /api/customers: Crear un nuevo cliente.
- PUT /api/customers/{identificationNumber}: Actualizar un cliente.
- DELETE /api/customers/{identificationNumber}: Eliminar un cliente.

### Direcciones
- POST /api/customers/{identificationNumber}/addresses: Añadir una dirección a un cliente.
- GET /api/customers/{identificationNumber}/addresses: Listar las direcciones de un cliente.

### Tenants
- GET /api/tenants: Obtener todos los tenants.
- POST /api/tenants: Crear un nuevo tenant.

## Arquitectura
Este sistema sigue una arquitectura hexagonal, lo que significa que está organizado en capas donde cada capa es independiente de la infraestructura y del resto del sistema. La interacción con la base de datos se maneja mediante repositorios y entidades, mientras que la lógica de negocio se gestiona a través de servicios.

