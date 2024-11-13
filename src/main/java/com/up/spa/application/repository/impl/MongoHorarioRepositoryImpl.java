package com.up.spa.application.repository.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.up.spa.application.model.BSONFields;
import com.up.spa.application.model.Horario;
import com.up.spa.application.repository.HorarioRepository;
import com.up.spa.application.services.IMongoService;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MongoHorarioRepositoryImpl implements HorarioRepository {


  private final IMongoService mongoService;

  public MongoHorarioRepositoryImpl(IMongoService mongoService) {
    this.mongoService = mongoService;
  }

  @Override
  public List<Horario> findHorariosByProfesionalId(String profesionalId) {
    MongoCollection<Document> horarioCollection = mongoService.getCollection(BSONFields.HORARIO_COLLECTION);
    List<Document> horariosDocuments = horarioCollection.find(Filters.eq(BSONFields.TURN_PROFESIONAL_ID, new ObjectId(profesionalId))).into(new ArrayList<>());

    return horariosDocuments.stream().map(this::convertToHorario).collect(Collectors.toList());
  }

  private Horario convertToHorario(Document document) {
    Horario horario = new Horario();
    horario.setDiaSemana(document.getString(BSONFields.HORARIO_DIA_SEMANA));
    horario.setHoraInicio(document.getString(BSONFields.HORARIO_HORA_INICIO));
    horario.setHoraFin(document.getString(BSONFields.HORARIO_HORA_FIN));

    // Convertir ObjectId de sucursalId a String
    ObjectId sucursalId = document.getObjectId(BSONFields.SUCURSAL_ID);
    if (sucursalId != null) {
      horario.setSucursalId(sucursalId.toHexString());
    }

    return horario;
  }
}
