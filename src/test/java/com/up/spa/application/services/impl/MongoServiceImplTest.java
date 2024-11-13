package com.up.spa.application.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.github.stefanbirkner.systemlambda.SystemLambda;

@ExtendWith(MockitoExtension.class)
public class MongoServiceImplTest {

  @Mock
  private MongoClient mongoClient;

  @Mock
  private MongoDatabase mongoDatabase;

  @Mock
  private MongoCollection<Document> mongoCollection;


  /**
   * Test para el método getCollection cuando la colección existe.
   * Verifica que la colección sea retornada correctamente.
   */
  @Test
  void getCollection_CollectionExists_ReturnsCollection() throws Exception {
    // Arrange
    String collectionName = "clientes";

    // Establecer la variable de entorno MONGO_DB a "test_db" temporalmente
    SystemLambda.withEnvironmentVariable("MONGO_DB", "test_db")
        .execute(() -> {
          // Configurar el comportamiento de los mocks
          when(mongoClient.getDatabase("test_db")).thenReturn(mongoDatabase);
          when(mongoDatabase.getCollection(collectionName)).thenReturn(mongoCollection);

          // Instanciar la clase bajo prueba después de establecer la variable de entorno
          MongoServiceImpl service = new MongoServiceImpl(mongoClient);

          // Act
          MongoCollection<Document> result = service.getCollection(collectionName);

          // Assert
          assertNotNull(result, "La colección debería ser retornada.");
          assertEquals(mongoCollection, result, "La colección retornada debería ser la esperada.");

          // Verificaciones
          verify(mongoClient, times(1)).getDatabase("test_db");
          verify(mongoDatabase, times(1)).getCollection(collectionName);
        });
  }

  /**
   * Test para el método getCollection cuando la colección no existe.
   * Verifica que el método retorne null.
   */
  @Test
  void getCollection_CollectionDoesNotExist_ReturnsNull() throws Exception {
    // Arrange
    String collectionName = "no_existe";

    // Establecer la variable de entorno MONGO_DB a "test_db" temporalmente
    SystemLambda.withEnvironmentVariable("MONGO_DB", "test_db")
        .execute(() -> {
          // Configurar el comportamiento de los mocks
          when(mongoClient.getDatabase("test_db")).thenReturn(mongoDatabase);
          when(mongoDatabase.getCollection(collectionName)).thenReturn(null); // Simular colección inexistente

          // Instanciar la clase bajo prueba después de establecer la variable de entorno
          MongoServiceImpl service = new MongoServiceImpl(mongoClient);

          // Act
          MongoCollection<Document> result = service.getCollection(collectionName);

          // Assert
          assertNull(result, "La colección no debería ser encontrada y debería retornar null.");

          // Verificaciones
          verify(mongoClient, times(1)).getDatabase("test_db");
          verify(mongoDatabase, times(1)).getCollection(collectionName);
        });
  }
}
