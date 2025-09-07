// Script de inicialización para MongoDB equivalente al init-client-db.sql
// Ejecutar en mongosh: load('init-client-db-mongo.js')

// Seleccionar / crear base de datos
use('banking');

// Eliminación opcional de las colecciones si se quiere un inicio limpio
// (Descomentar si se requiere comportamiento tipo DROP TABLE)
// db.client?.drop();
// db.customer?.drop();

// Colección client con validación de esquema aproximando restricciones SQL
try {
  db.createCollection('client', {
    validator: {
      $jsonSchema: {
        bsonType: 'object',
        required: ['first_name', 'last_name', 'dni', 'email', 'created_at', 'updated_at'],
        properties: {
          first_name: { bsonType: 'string', maxLength: 80 },
            last_name: { bsonType: 'string', maxLength: 80 },
            dni: { bsonType: 'string', maxLength: 16 },
            email: { bsonType: 'string', maxLength: 120, pattern: '^.+@.+\\..+$' },
            created_at: { bsonType: ['date'] },
            updated_at: { bsonType: ['date'] }
        }
      }
    },
    validationLevel: 'moderate',
    validationAction: 'error'
  });
} catch (e) {
  if (e.codeName === 'NamespaceExists') {
    // ya existe, continuar
  } else throw e;
}

// Índices equivalentes a los del SQL
// UNIQUE KEY uk_client_dni (dni)
db.client.createIndex({ dni: 1 }, { unique: true, name: 'uk_client_dni' });
// UNIQUE KEY uk_client_email (email)
db.client.createIndex({ email: 1 }, { unique: true, name: 'uk_client_email' });
// INDEX idx_client_name (first_name, last_name)
db.client.createIndex({ first_name: 1, last_name: 1 }, { name: 'idx_client_name' });

// Trigger lógico para timestamps: función helper para inserts/updates manuales
function insertClient(doc) {
  const now = new Date();
  doc.created_at = doc.created_at || now;
  doc.updated_at = doc.updated_at || now;
  return db.client.insertOne(doc);
}

function updateClient(filter, update) {
  const setUpdate = update.$set || (update.$set = {});
  setUpdate.updated_at = new Date();
  return db.client.updateOne(filter, update);
}

// Colección customer para compatibilidad (misma estructura). Puede mantenerse sincronizada por aplicación.
try {
  db.createCollection('customer', {
    validator: {
      $jsonSchema: {
        bsonType: 'object',
        required: ['first_name', 'last_name', 'dni', 'email', 'created_at', 'updated_at'],
        properties: {
          first_name: { bsonType: 'string', maxLength: 80 },
          last_name: { bsonType: 'string', maxLength: 80 },
          dni: { bsonType: 'string', maxLength: 16 },
          email: { bsonType: 'string', maxLength: 120, pattern: '^.+@.+\\..+$' },
          created_at: { bsonType: ['date'] },
          updated_at: { bsonType: ['date'] }
        }
      }
    },
    validationLevel: 'moderate',
    validationAction: 'error'
  });
} catch (e) {
  if (e.codeName === 'NamespaceExists') {
  } else throw e;
}

// Índices en customer (clonados) - si no se necesitan duplicados, se pueden omitir o mantener sólo uno de los conjuntos.
db.customer.createIndex({ dni: 1 }, { unique: true, name: 'uk_customer_dni' });
db.customer.createIndex({ email: 1 }, { unique: true, name: 'uk_customer_email' });
db.customer.createIndex({ first_name: 1, last_name: 1 }, { name: 'idx_customer_name' });

// Ejemplo de uso:
// insertClient({ first_name: 'Juan', last_name: 'Pérez', dni: '12345678A', email: 'juan.perez@test.com' });
// updateClient({ dni: '12345678A' }, { $set: { email: 'nuevo@test.com' } });

print('\nScript MongoDB ejecutado. Colecciones e índices listos.');

