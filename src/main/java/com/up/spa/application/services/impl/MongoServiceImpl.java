package com.up.spa.application.services.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.up.spa.application.services.IMongoService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MongoServiceImpl implements IMongoService {

  private final MongoDatabase database;
  String mongoDb = System.getenv("MONGO_DB");

  @Autowired
  public MongoServiceImpl(MongoClient mongoClient) {
    this.database = mongoClient.getDatabase(mongoDb);
  }

  @Override
  public MongoCollection<Document> getCollection(String collectionName) {
    MongoCollection<Document> collection = database.getCollection(collectionName);
    if (collection != null) {
      log.info("Colección {} encontrada en la base de datos.", collectionName);
    } else {
      log.warn("Colección {} no encontrada en la base de datos.", collectionName);
    }
    return collection;
  }
}