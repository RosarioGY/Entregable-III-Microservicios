package com.bootcamp.clientms.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Document(collection = "client")
@CompoundIndexes({@CompoundIndex(name = "idx_client_name", def = "{first_name:1, last_name:1}")})
public class Client {
  @Id
  private String id; // ObjectId como String

  @Field("first_name")
  private String firstName;

  @Field("last_name")
  private String lastName;

  @Indexed(unique = true)
  private String email;

  @Indexed(unique = true)
  private String dni;

  @CreatedDate
  @Field("created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Field("updated_at")
  private LocalDateTime updatedAt;
}
