// Script para ejecutar en MongoDB Atlas via mongosh online o MongoDB Compass
// URI: mongodb+srv://rosario:1KsQK8dZgcUtiHr7@cluster0.bgvqsrr.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0

// Conectar a la base de datos banking
use('banking');

// Eliminar colecciones si existen (opcional para reiniciar)
// db.client.drop();
// db.customer.drop();

print('Creando colección client con validaciones...');

// Crear colección client con validación de esquema
try {
  db.createCollection('client', {
    validator: {
      $jsonSchema: {
        bsonType: 'object',
        required: ['first_name', 'last_name', 'dni', 'email'],
        properties: {
          first_name: { bsonType: 'string', maxLength: 80 },
          last_name: { bsonType: 'string', maxLength: 80 },
          dni: { bsonType: 'string', maxLength: 16 },
          email: { bsonType: 'string', maxLength: 120, pattern: '^.+@.+\\..+$' },
          created_at: { bsonType: ['date', 'null'] },
          updated_at: { bsonType: ['date', 'null'] }
        }
      }
    },
    validationLevel: 'moderate',
    validationAction: 'error'
  });
  print('✅ Colección client creada exitosamente');
} catch (e) {
  if (e.codeName === 'NamespaceExists') {
    print('⚠️  Colección client ya existe, continuando...');
  } else {
    print('❌ Error creando colección client:', e.message);
    throw e;
  }
}

print('Creando índices para colecci��n client...');

// Crear índices únicos para client
try {
  db.client.createIndex({ dni: 1 }, { unique: true, name: 'uk_client_dni' });
  print('✅ Índice único uk_client_dni creado');
} catch (e) {
  print('⚠️  Índice uk_client_dni ya existe o error:', e.message);
}

try {
  db.client.createIndex({ email: 1 }, { unique: true, name: 'uk_client_email' });
  print('✅ Índice único uk_client_email creado');
} catch (e) {
  print('⚠️  Índice uk_client_email ya existe o error:', e.message);
}

try {
  db.client.createIndex({ first_name: 1, last_name: 1 }, { name: 'idx_client_name' });
  print('✅ Índice idx_client_name creado');
} catch (e) {
  print('⚠️  Índice idx_client_name ya existe o error:', e.message);
}

print('Creando colección customer con validaciones...');

// Crear colección customer (compatible con la estructura)
try {
  db.createCollection('customer', {
    validator: {
      $jsonSchema: {
        bsonType: 'object',
        required: ['first_name', 'last_name', 'dni', 'email'],
        properties: {
          first_name: { bsonType: 'string', maxLength: 80 },
          last_name: { bsonType: 'string', maxLength: 80 },
          dni: { bsonType: 'string', maxLength: 16 },
          email: { bsonType: 'string', maxLength: 120, pattern: '^.+@.+\\..+$' },
          created_at: { bsonType: ['date', 'null'] },
          updated_at: { bsonType: ['date', 'null'] }
        }
      }
    },
    validationLevel: 'moderate',
    validationAction: 'error'
  });
  print('✅ Colección customer creada exitosamente');
} catch (e) {
  if (e.codeName === 'NamespaceExists') {
    print('⚠️  Colección customer ya existe, continuando...');
  } else {
    print('❌ Error creando colección customer:', e.message);
  }
}

print('Creando índices para colección customer...');

// Crear índices únicos para customer
try {
  db.customer.createIndex({ dni: 1 }, { unique: true, name: 'uk_customer_dni' });
  print('✅ Índice único uk_customer_dni creado');
} catch (e) {
  print('⚠️  Índice uk_customer_dni ya existe o error:', e.message);
}

try {
  db.customer.createIndex({ email: 1 }, { unique: true, name: 'uk_customer_email' });
  print('✅ Índice único uk_customer_email creado');
} catch (e) {
  print('⚠️  Índice uk_customer_email ya existe o error:', e.message);
}

try {
  db.customer.createIndex({ first_name: 1, last_name: 1 }, { name: 'idx_customer_name' });
  print('✅ Índice idx_customer_name creado');
} catch (e) {
  print('⚠️  Índice idx_customer_name ya existe o error:', e.message);
}

// Insertar datos de prueba
print('Insertando datos de prueba...');

try {
  const now = new Date();

  // Insertar clientes de prueba
  const clientsData = [
    {
      first_name: 'Juan',
      last_name: 'Pérez',
      dni: '12345678A',
      email: 'juan.perez@example.com',
      created_at: now,
      updated_at: now
    },
    {
      first_name: 'María',
      last_name: 'González',
      dni: '87654321B',
      email: 'maria.gonzalez@example.com',
      created_at: now,
      updated_at: now
    },
    {
      first_name: 'Carlos',
      last_name: 'López',
      dni: '11223344C',
      email: 'carlos.lopez@example.com',
      created_at: now,
      updated_at: now
    }
  ];

  const result = db.client.insertMany(clientsData);
  print('✅ Insertados', result.insertedIds.length, 'clientes de prueba');

} catch (e) {
  print('⚠️  Error insertando datos de prueba (pueden ya existir):', e.message);
}

// Mostrar estadísticas finales
print('\n📊 RESUMEN DE LA BASE DE DATOS:');
print('Base de datos actual:', db.getName());

const clientCount = db.client.countDocuments();
const customerCount = db.customer.countDocuments();

print('Documentos en colección client:', clientCount);
print('Documentos en colección customer:', customerCount);

print('\n📋 ÍNDICES CREADOS:');
print('Índices en client:');
db.client.getIndexes().forEach(index => {
  print(' -', index.name, ':', JSON.stringify(index.key));
});

print('Índices en customer:');
db.customer.getIndexes().forEach(index => {
  print(' -', index.name, ':', JSON.stringify(index.key));
});

print('\n🎉 SCRIPT COMPLETADO EXITOSAMENTE!');
print('Base de datos banking lista para usar con la aplicación Spring Boot.');
print('\n💡 Para probar la aplicación, configura DB_PASSWORD=1KsQK8dZgcUtiHr7');
