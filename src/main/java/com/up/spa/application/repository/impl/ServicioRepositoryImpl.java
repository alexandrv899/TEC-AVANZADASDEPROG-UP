package com.up.spa.application.repository.impl;

import com.mongodb.client.MongoCollection;
import com.up.spa.application.model.BSONFields;
import com.up.spa.application.model.Servicio;
import com.up.spa.application.repository.ServicioRepository;
import com.up.spa.application.services.IMongoService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ServicioRepositoryImpl implements ServicioRepository {

  private final IMongoService mongoService;

  public ServicioRepositoryImpl(IMongoService mongoService) {
    this.mongoService = mongoService;
  }

  @Override
  public Servicio findServicioById(String servicioId) {
    MongoCollection<Document> servicioCollection = mongoService.getCollection(BSONFields.SERVICIO_COLLECTION);
    Document servicioDoc = servicioCollection.find(new Document(BSONFields.SERVICIO_ID, new ObjectId(servicioId))).first();

    if (servicioDoc != null) {
      String nombre = servicioDoc.getString(BSONFields.SERVICIO_NOMBRE);
      Integer duracion = servicioDoc.getInteger(BSONFields.SERVICIO_DURACION);
      String especialidadId = servicioDoc.getObjectId(BSONFields.SERVICIO_ESPECIALIDAD_ID).toHexString();

      return Servicio.builder()
          .id(servicioId)
          .nombre(nombre)
          .duracion(duracion)
          .especialidadId(especialidadId)
          .build();
    } else {
      log.warn("Servicio no encontrado para ID: {}", servicioId);
      return null;
    }
  }
}
