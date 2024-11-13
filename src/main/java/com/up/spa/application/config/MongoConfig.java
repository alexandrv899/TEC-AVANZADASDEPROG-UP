package com.up.spa.application.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

  @Bean
  public MongoClient mongoClient() {

    String user = System.getenv("MONGO_USER");
    String password = System.getenv("MONGO_PASS");

    String connectionString = String.format(
        "mongodb+srv://%s:%s@upprogavanzada.khzhv.mongodb.net/?retryWrites=true&w=majority&appName=UpProgAVanzada",
        user, password
    );

    ServerApi serverApi = ServerApi.builder()
        .version(ServerApiVersion.V1)
        .build();

    MongoClientSettings settings = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(connectionString))
        .serverApi(serverApi)
        .build();

    return MongoClients.create(settings);
  }
}
