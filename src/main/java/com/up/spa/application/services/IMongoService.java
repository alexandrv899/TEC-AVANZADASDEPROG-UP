package com.up.spa.application.services;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public interface IMongoService {
  MongoCollection<Document> getCollection(String collectionName);
}
