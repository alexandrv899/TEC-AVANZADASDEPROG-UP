package com.up.spa.application.repository.impl;

import com.mongodb.client.MongoCollection;
import com.up.spa.application.model.BSONFields;
import com.up.spa.application.model.*;
import com.up.spa.application.model.mappers.ServiceDocMongoMapper;
import com.up.spa.application.repository.SucursalRepository;
import com.up.spa.application.services.IMongoService;
import com.up.spa.application.component.MongoLookupBuilder;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MongoSucursalRepositoryImpl implements SucursalRepository {

  private static final String PROFESIONALES = "profesionales";
  private static final String SERVICIOS = "servicios";
  private static final String ESPECIALIDADES = "especialidades";

  private final IMongoService mongoService;
  private final MongoLookupBuilder lookupBuilder;
  private final ServiceDocMongoMapper mapper;

  public MongoSucursalRepositoryImpl(IMongoService mongoService, MongoLookupBuilder lookupBuilder, ServiceDocMongoMapper mapper) {
    this.mongoService = mongoService;
    this.lookupBuilder = lookupBuilder;
    this.mapper = mapper;
  }

  @Override
  public List<Sucursal> findAllSucursales() {
    MongoCollection<Document> sucursalCollection = mongoService.getCollection(BSONFields.SUCURSAL_COLLECTION);

    List<Document> pipeline = lookupBuilder
        .withProfesionales()
        .withServicios()
        .withEspecialidades()
        .build();

    return sucursalCollection.aggregate(pipeline)
        .into(new ArrayList<>())
        .stream()
        .map(this::mapToSucursales)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Sucursal> findById(String sucursalId) {
    MongoCollection<Document> sucursalCollection = mongoService.getCollection(BSONFields.SUCURSAL_COLLECTION);
    Document filter = new Document(BSONFields.SUCURSAL_ID, new ObjectId(sucursalId));

    List<Document> pipeline = lookupBuilder
        .withProfesionales()
        .withServicios()
        .withEspecialidades()
        .build();
    pipeline.add(0, new Document("$match", filter));

    return sucursalCollection.aggregate(pipeline)
        .into(new ArrayList<>())
        .stream()
        .findFirst()
        .map(this::mapToSucursales);
  }

  private List<Horario> mapHorarios(List<Document> horariosDocs) {
    return horariosDocs.stream()
        .map(horarioDoc -> Horario.builder()
            .diaSemana(horarioDoc.getString(BSONFields.HORARIO_DIA_SEMANA))
            .horaInicio(horarioDoc.getString(BSONFields.HORARIO_HORA_INICIO))
            .horaFin(horarioDoc.getString(BSONFields.HORARIO_HORA_FIN))
            .build())
        .collect(Collectors.toList());
  }


  private List<Especialidad> mapEspecialidades(List<Document> especialidadesDocs) {
    return especialidadesDocs.stream()
        .map(espDoc -> Especialidad.builder()
            .id(espDoc.getObjectId(BSONFields.SERVICIO_ID).toHexString())
            .nombre(espDoc.getString(BSONFields.SERVICIO_NOMBRE))
            .descripcion(espDoc.getString(BSONFields.SERVICIO_DESCRIPCION))
            .build())
        .collect(Collectors.toList());
  }

  private Sucursal mapToSucursales(Document doc) {
    return Sucursal.builder()
        .id(doc.getObjectId(BSONFields.SUCURSAL_ID).toHexString())
        .nombre(doc.getString(BSONFields.SUCURSAL_NOMBRE))
        .direccion(doc.getString(BSONFields.SUCURSAL_DIRECCION))
        .profesionales(mapper.mapProfesionales((List<Document>) doc.get(PROFESIONALES)))
        .servicios(mapper.mapServicios((List<Document>) doc.get(SERVICIOS)))
        .especialidades(mapEspecialidades((List<Document>) doc.get(ESPECIALIDADES)))
        .horarios(mapHorarios((List<Document>) doc.get(BSONFields.SUCURSAL_HORARIOS)))
        .build();
  }
}
