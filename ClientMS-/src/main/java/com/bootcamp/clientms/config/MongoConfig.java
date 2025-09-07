package com.bootcamp.clientms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB configuration class. Enables MongoDB auditing for automatic timestamp management.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
  // Auditing is enabled automatically for @CreatedDate and @LastModifiedDate annotations
}
