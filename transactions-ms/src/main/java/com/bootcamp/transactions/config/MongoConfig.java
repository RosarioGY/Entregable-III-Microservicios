package com.bootcamp.transactions.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Configuración de MongoDB. Habilita el auditing para gestión automática de timestamps.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
    // El auditing se habilita automáticamente para las anotaciones @CreatedDate y @LastModifiedDate
}