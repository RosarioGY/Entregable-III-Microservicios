# 📊 Diagramas de Secuencia - Microservicio Client Management

Este archivo contiene los diagramas de secuencia detallados del microservicio de gestión de clientes, mostrando el flujo completo de cada operación CRUD.

## 🎯 Operaciones Documentadas

1. **Crear Cliente (POST /clientes)** - Flujo exitoso y con errores
2. **Listar Clientes (GET /clientes)** - Obtener todos los clientes
3. **Obtener Cliente por ID (GET /clientes/{id})** - Búsqueda individual
4. **Actualizar Cliente Completo (PUT /clientes/{id})** - Actualización total
5. **Actualizar Cliente Parcial (PATCH /clientes/{id})** - Actualización parcial
6. **Eliminar Cliente (DELETE /clientes/{id})** - Eliminación
7. **Flujos de Error** - Manejo de excepciones y validaciones

---

## 1. 📝 Crear Cliente - Flujo Exitoso

```mermaid
sequenceDiagram
    participant Client as 🌐 Cliente (Postman)
    participant Controller as 📡 ClientController
    participant Validator as ✅ Bean Validator
    participant Service as ⚙️ ClientService
    participant Repository as 🗃️ ClientRepository
    participant MongoDB as 🗄️ MongoDB Atlas
    participant Auditing as 📝 MongoAuditing

    Client->>+Controller: POST /clientes
    Note over Client,Controller: CreateClientRequest JSON<br/>{firstName, lastName, dni, email}
    
    Controller->>+Validator: @Valid validation
    Validator->>Validator: Check @NotBlank, @Email, @Size
    Validator-->>-Controller: ✅ Validation passed
    
    Controller->>+Service: createClient(request)
    Note over Service: log.info("Creating new client with email: {}")
    
    Service->>+Repository: existsByEmail(email)
    Repository->>+MongoDB: db.client.findOne({email: "..."})
    MongoDB-->>-Repository: null (email available)
    Repository-->>-Service: false
    
    Service->>+Repository: existsByDni(dni)
    Repository->>+MongoDB: db.client.findOne({dni: "..."})
    MongoDB-->>-Repository: null (dni available)
    Repository-->>-Service: false
    
    Note over Service: Client.builder()<br/>.firstName().lastName()<br/>.email().dni().build()
    
    Service->>+Repository: save(client)
    Repository->>+MongoDB: db.client.insertOne(document)
    
    MongoDB->>+Auditing: Trigger @EnableMongoAuditing
    Auditing->>Auditing: Set @CreatedDate = now()
    Auditing->>Auditing: Set @LastModifiedDate = now()
    Auditing-->>-MongoDB: Timestamps added
    
    Note over MongoDB: Auto-generate ObjectId<br/>Apply unique indexes<br/>Store in 'client' collection
    MongoDB-->>-Repository: Saved Client with generated ID
    Repository-->>-Service: Client entity
    
    Note over Service: log.info("Client created successfully with id: {}")
    Service->>Service: ClientResponse.from(savedClient)
    Service-->>-Controller: ClientResponse DTO
    
    Controller-->>-Client: HTTP 200 OK + ClientResponse
    Note over Client,Controller: {id: "ObjectId", firstName: "...", lastName: "...", dni: "...", email: "..."}
```

---

## 2. 📋 Listar Todos los Clientes

```mermaid
sequenceDiagram
    participant Client as 🌐 Cliente (Postman)
    participant Controller as 📡 ClientController
    participant Service as ⚙️ ClientService
    participant Repository as 🗃️ ClientRepository
    participant MongoDB as 🗄️ MongoDB Atlas

    Client->>+Controller: GET /clientes
    
    Controller->>+Service: getAllClients()
    Note over Service: log.info("Retrieving all clients")
    
    Service->>+Repository: findAll()
    Repository->>+MongoDB: db.client.find({})
    Note over MongoDB: Query all documents<br/>in 'client' collection
    MongoDB-->>-Repository: List<Client> entities
    Repository-->>-Service: List<Client>
    
    Service->>Service: clients.stream()<br/>.map(ClientResponse::from)<br/>.toList()
    Note over Service: Transform entities to DTOs<br/>Hide internal fields
    Service-->>-Controller: List<ClientResponse>
    
    Controller-->>-Client: HTTP 200 OK + List<ClientResponse>
    Note over Client,Controller: [<br/>{id: "...", firstName: "...", ...},<br/>{id: "...", firstName: "...", ...}<br/>]
```

---

## 3. 🔍 Obtener Cliente por ID

```mermaid
sequenceDiagram
    participant Client as 🌐 Cliente (Postman)
    participant Controller as 📡 ClientController
    participant Service as ⚙️ ClientService
    participant Repository as 🗃️ ClientRepository
    participant MongoDB as 🗄️ MongoDB Atlas
    participant Exception as ⚠️ GlobalExceptionHandler

    Client->>+Controller: GET /clientes/{id}
    Note over Client,Controller: PathVariable: String id (ObjectId)
    
    Controller->>+Service: getClientById(id)
    Note over Service: log.info("Retrieving client with id: {}")
    
    Service->>+Repository: findById(id)
    Repository->>+MongoDB: db.client.findOne({_id: ObjectId(id)})
    MongoDB-->>-Repository: Optional<Client>
    Repository-->>-Service: Optional<Client>
    
    alt Cliente encontrado
        Service->>Service: ClientResponse.from(client)
        Service-->>-Controller: ClientResponse
        Controller-->>-Client: HTTP 200 OK + ClientResponse
        Note over Client,Controller: {id: "...", firstName: "...", lastName: "...", dni: "...", email: "..."}
    else Cliente no encontrado
        Service->>Service: throw ClientNotFoundException(id)
        Service-->>-Controller: ClientNotFoundException
        Controller->>+Exception: GlobalExceptionHandler.handleClientNotFound()
        Exception->>Exception: Build error response
        Exception-->>-Client: HTTP 404 Not Found
        Note over Client,Exception: {<br/>"error": "Client not found with id: ...",<br/>"timestamp": "...",<br/>"path": "/clientes/..."<br/>}
    end
```

---

## 4. ✏️ Actualizar Cliente Completo (PUT)

```mermaid
sequenceDiagram
    participant Client as 🌐 Cliente (Postman)
    participant Controller as 📡 ClientController
    participant Validator as ✅ Bean Validator
    participant Service as ⚙️ ClientService
    participant Repository as 🗃️ ClientRepository
    participant MongoDB as 🗄️ MongoDB Atlas
    participant Auditing as 📝 MongoAuditing

    Client->>+Controller: PUT /clientes/{id}
    Note over Client,Controller: UpdateClientRequest JSON<br/>{firstName, lastName, email}
    
    Controller->>+Validator: @Valid validation
    Validator-->>-Controller: ✅ Validation passed
    
    Controller->>+Service: updateClient(id, request)
    Note over Service: log.info("Updating client with id: {}")
    
    Service->>+Repository: findById(id)
    Repository->>+MongoDB: db.client.findOne({_id: ObjectId(id)})
    MongoDB-->>-Repository: Optional<Client>
    Repository-->>-Service: Optional<Client>
    
    alt Cliente existe
        Service->>+Repository: existsByEmailAndIdNot(email, id)
        Repository->>+MongoDB: db.client.findOne({email: "...", _id: {$ne: ObjectId(id)}})
        MongoDB-->>-Repository: null (email available for this client)
        Repository-->>-Service: false
        
        Note over Service: existingClient.setFirstName(request.getFirstName())<br/>existingClient.setLastName(request.getLastName())<br/>existingClient.setEmail(request.getEmail())
        
        Service->>+Repository: save(existingClient)
        Repository->>+MongoDB: db.client.updateOne({_id: ObjectId(id)}, {$set: {...}})
        
        MongoDB->>+Auditing: Trigger @EnableMongoAuditing
        Auditing->>Auditing: Update @LastModifiedDate = now()
        Note over Auditing: @CreatedDate remains unchanged
        Auditing-->>-MongoDB: Timestamp updated
        
        MongoDB-->>-Repository: Updated Client
        Repository-->>-Service: Updated Client entity
        
        Note over Service: log.info("Client updated successfully with id: {}")
        Service->>Service: ClientResponse.from(updatedClient)
        Service-->>-Controller: ClientResponse
        Controller-->>-Client: HTTP 200 OK + ClientResponse
    else Cliente no existe
        Service->>Service: throw ClientNotFoundException(id)
        Service-->>Controller: ClientNotFoundException
        Controller->>Exception: GlobalExceptionHandler
        Exception-->>Client: HTTP 404 Not Found
    end
```

---

## 5. 🔧 Actualizar Cliente Parcial (PATCH)

```mermaid
sequenceDiagram
    participant Client as 🌐 Cliente (Postman)
    participant Controller as 📡 ClientController
    participant Service as ⚙️ ClientService
    participant Repository as 🗃️ ClientRepository
    participant MongoDB as 🗄️ MongoDB Atlas

    Client->>+Controller: PATCH /clientes/{id}
    Note over Client,Controller: PatchClientRequest JSON<br/>(only modified fields)<br/>{email: "new@email.com"}
    
    Controller->>+Service: patchClient(id, request)
    Note over Service: log.info("Patching client with id: {}")
    
    Service->>+Repository: findById(id)
    Repository->>+MongoDB: db.client.findOne({_id: ObjectId(id)})
    MongoDB-->>-Repository: Optional<Client>
    Repository-->>-Service: Optional<Client>
    
    alt Cliente existe
        alt firstName != null
            Note over Service: existingClient.setFirstName(request.getFirstName())
        end
        
        alt lastName != null
            Note over Service: existingClient.setLastName(request.getLastName())
        end
        
        alt email != null
            Service->>+Repository: existsByEmailAndIdNot(email, id)
            Repository->>+MongoDB: Check email uniqueness
            MongoDB-->>-Repository: false (email available)
            Repository-->>-Service: false
            Note over Service: existingClient.setEmail(request.getEmail())
        end
        
        alt dni != null
            Service->>+Repository: existsByDniAndIdNot(dni, id)
            Repository->>+MongoDB: Check DNI uniqueness
            MongoDB-->>-Repository: false (DNI available)
            Repository-->>-Service: false
            Note over Service: existingClient.setDni(request.getDni())
        end
        
        Service->>+Repository: save(existingClient)
        Repository->>+MongoDB: db.client.updateOne({_id: ObjectId(id)}, {$set: {...}})
        Note over MongoDB: Update only modified fields<br/>Trigger @LastModifiedDate update
        MongoDB-->>-Repository: Updated Client
        Repository-->>-Service: Updated Client entity
        
        Note over Service: log.info("Client patched successfully with id: {}")
        Service-->>-Controller: ClientResponse
        Controller-->>-Client: HTTP 200 OK + ClientResponse
    else Cliente no existe
        Service-->>Controller: ClientNotFoundException
        Controller->>Exception: GlobalExceptionHandler
        Exception-->>Client: HTTP 404 Not Found
    end
```

---

## 6. 🗑️ Eliminar Cliente

```mermaid
sequenceDiagram
    participant Client as 🌐 Cliente (Postman)
    participant Controller as 📡 ClientController
    participant Service as ⚙️ ClientService
    participant Repository as 🗃️ ClientRepository
    participant MongoDB as 🗄️ MongoDB Atlas

    Client->>+Controller: DELETE /clientes/{id}
    
    Controller->>+Service: deleteClient(id)
    Note over Service: log.info("Deleting client with id: {}")
    
    Service->>+Repository: findById(id)
    Repository->>+MongoDB: db.client.findOne({_id: ObjectId(id)})
    MongoDB-->>-Repository: Optional<Client>
    Repository-->>-Service: Optional<Client>
    
    alt Cliente existe
        Service->>+Repository: delete(client)
        Repository->>+MongoDB: db.client.deleteOne({_id: ObjectId(id)})
        Note over MongoDB: Remove document from<br/>'client' collection
        MongoDB-->>-Repository: DeleteResult (acknowledged: true)
        Repository-->>-Service: void
        
        Note over Service: log.info("Client deleted successfully with id: {}")
        Service-->>-Controller: void
        Controller-->>-Client: HTTP 204 No Content
    else Cliente no existe
        Service->>Service: throw ClientNotFoundException(id)
        Service-->>Controller: ClientNotFoundException
        Controller->>Exception: GlobalExceptionHandler
        Exception-->>Client: HTTP 404 Not Found
    end
```

---

## 7. ⚠️ Flujos de Error - Validaciones de Negocio

### 7.1 Error: Email Duplicado

```mermaid
sequenceDiagram
    participant Client as 🌐 Cliente (Postman)
    participant Controller as 📡 ClientController
    participant Service as ⚙️ ClientService
    participant Repository as 🗃️ ClientRepository
    participant MongoDB as 🗄️ MongoDB Atlas
    participant Exception as ⚠️ GlobalExceptionHandler

    Client->>+Controller: POST /clientes
    Note over Client,Controller: Email already exists in database
    
    Controller->>+Service: createClient(request)
    
    Service->>+Repository: existsByEmail(email)
    Repository->>+MongoDB: db.client.findOne({email: "existing@test.com"})
    MongoDB-->>-Repository: Document found (email exists)
    Repository-->>-Service: true
    
    Note over Service: log.warn("Attempt to create client with existing email: {}")
    Service->>Service: throw EmailAlreadyExistsException(email)
    Service-->>-Controller: EmailAlreadyExistsException
    
    Controller->>+Exception: GlobalExceptionHandler.handleEmailAlreadyExists()
    Exception->>Exception: Build conflict response
    Exception-->>-Client: HTTP 409 Conflict
    Note over Client,Exception: {<br/>"error": "Email already exists",<br/>"email": "existing@test.com",<br/>"timestamp": "...",<br/>"path": "/clientes"<br/>}
```

### 7.2 Error: DNI Duplicado

```mermaid
sequenceDiagram
    participant Client as 🌐 Cliente (Postman)
    participant Controller as 📡 ClientController
    participant Service as ⚙️ ClientService
    participant Repository as 🗃️ ClientRepository
    participant Exception as ⚠️ GlobalExceptionHandler

    Client->>+Controller: PATCH /clientes/{id}
    Note over Client,Controller: DNI already exists for another client
    
    Controller->>+Service: patchClient(id, request)
    
    Note over Service: Cliente found, DNI field != null
    Service->>+Repository: existsByDniAndIdNot(dni, id)
    Repository-->>-Service: true (DNI exists for another client)
    
    Note over Service: log.warn("Attempt to update client with existing DNI: {}")
    Service->>Service: throw DniAlreadyExistsException(dni)
    Service-->>-Controller: DniAlreadyExistsException
    
    Controller->>+Exception: GlobalExceptionHandler.handleDniAlreadyExists()
    Exception-->>-Client: HTTP 409 Conflict
    Note over Client,Exception: {<br/>"error": "DNI already exists",<br/>"dni": "12345678A",<br/>"timestamp": "...",<br/>"path": "/clientes/..."<br/>}
```

### 7.3 Error: Validación de Entrada

```mermaid
sequenceDiagram
    participant Client as 🌐 Cliente (Postman)
    participant Controller as 📡 ClientController
    participant Validator as ✅ Bean Validator
    participant Exception as ⚠️ GlobalExceptionHandler

    Client->>+Controller: POST /clientes
    Note over Client,Controller: Invalid data<br/>{firstName: "", email: "invalid-email"}
    
    Controller->>+Validator: @Valid CreateClientRequest
    Validator->>Validator: Check @NotBlank on firstName
    Validator->>Validator: Check @Email on email field
    Validator->>Validator: Validation fails
    Validator-->>-Controller: MethodArgumentNotValidException
    
    Controller->>+Exception: GlobalExceptionHandler.handleValidationExceptions()
    Exception->>Exception: Extract field errors<br/>Build validation response
    Exception-->>-Client: HTTP 400 Bad Request
    Note over Client,Exception: {<br/>"error": "Validation failed",<br/>"fields": {<br/>"firstName": "must not be blank",<br/>"email": "must be a well-formed email"<br/>},<br/>"timestamp": "...",<br/>"path": "/clientes"<br/>}
```

---

## 8. 🔄 Flujo de Auditoría MongoDB

```mermaid
sequenceDiagram
    participant Service as ⚙️ ClientService
    participant Repository as 🗃️ ClientRepository
    participant MongoDB as 🗄️ MongoDB Atlas
    participant Auditing as 📝 @EnableMongoAuditing
    participant Entity as 🎯 Client Entity

    Note over Service,Entity: Any write operation (save/update)

    Service->>+Repository: save(client)
    Repository->>+MongoDB: Database operation (insert/update)
    
    MongoDB->>+Auditing: Trigger MongoAuditing interceptor
    
    alt INSERT operation
        Auditing->>+Entity: Set @CreatedDate field
        Entity->>Entity: createdAt = LocalDateTime.now()
        Entity-->>-Auditing: Field updated
        
        Auditing->>+Entity: Set @LastModifiedDate field
        Entity->>Entity: updatedAt = LocalDateTime.now()
        Entity-->>-Auditing: Field updated
    else UPDATE operation
        Auditing->>+Entity: Update @LastModifiedDate field
        Entity->>Entity: updatedAt = LocalDateTime.now()
        Note over Entity: @CreatedDate remains unchanged
        Entity-->>-Auditing: Field updated
    end
    
    Auditing-->>-MongoDB: Document with audit timestamps
    MongoDB-->>-Repository: Saved/Updated document
    Repository-->>-Service: Client entity with audit fields
```

---

## 📊 Componentes del Microservicio

### **Participantes en los Diagramas:**

| **Participante** | **Tipo** | **Responsabilidad** |
|------------------|----------|---------------------|
| 🌐 **Cliente (Postman)** | API Consumer | Consumidor de la API REST |
| 📡 **ClientController** | REST Controller | Manejo de requests HTTP, validación |
| ✅ **Bean Validator** | Validation Framework | Validaciones automáticas (@Valid) |
| ⚙️ **ClientService** | Business Service | Lógica de negocio, validaciones de dominio |
| 🗃️ **ClientRepository** | Data Repository | Abstracción de acceso a datos |
| 🗄️ **MongoDB Atlas** | Database | Persistencia en la nube |
| 📝 **MongoAuditing** | Auditing System | Timestamps automáticos |
| ⚠️ **GlobalExceptionHandler** | Exception Handler | Manejo centralizado de errores |

### **Flujos de Datos Implementados:**

✅ **Request Flow**: Cliente → Controller → Service → Repository → MongoDB
✅ **Response Flow**: MongoDB → Repository → Service → Controller → Cliente  
✅ **Error Flow**: Exception → GlobalExceptionHandler → Error Response → Cliente
✅ **Auditing Flow**: MongoDB Operation → MongoAuditing → Timestamp Update
✅ **Validation Flow**: Controller → Bean Validator → Validation Result

### **Patrones de Diseño Documentados:**

- **Repository Pattern**: Separación de acceso a datos
- **DTO Pattern**: Transferencia segura de datos
- **Exception Handling Pattern**: Manejo centralizado de errores
- **Auditing Pattern**: Tracking automático de cambios
- **Builder Pattern**: Construcción fluida de objetos
- **Service Layer Pattern**: Lógica de negocio encapsulada

---

## 🎯 Casos de Uso Cubiertos

✅ **Operaciones CRUD Completas** (Create, Read, Update, Delete)
✅ **Validaciones Multi-nivel** (Entrada + Negocio + Base de datos)
✅ **Manejo Robusto de Errores** (400, 404, 409, 500)
✅ **Auditoría Automática** (Created/Modified timestamps)
✅ **Logging Detallado** (Info, Warn levels)
✅ **Transformación DTO** (Entity ↔ DTO mapping)
✅ **Integridad de Datos** (Unique constraints, validations)

---

**Para visualizar estos diagramas:**
1. Copiar el código Mermaid correspondiente
2. Pegar en [Mermaid Live Editor](https://mermaid.live/)
3. O usar extensiones de Mermaid en VS Code, IntelliJ, GitHub, etc.
