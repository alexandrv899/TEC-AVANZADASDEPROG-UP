package com.up.spa.application.repository.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.InsertOneResult;
import com.up.spa.application.component.MongoLookupBuilder;
import com.up.spa.application.enums.EstadoTurno;
import com.up.spa.application.model.BSONFields;
import com.up.spa.application.model.Profesional;
import com.up.spa.application.model.Servicio;
import com.up.spa.application.model.Turno;
import com.up.spa.application.model.mappers.ServiceDocMongoMapper;
import com.up.spa.application.repository.TurnoRepository;
import com.up.spa.application.services.IMongoService;
import com.up.spa.application.services.IServicio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Repository
@Slf4j
public class MongoTurnoRepositoryImpl implements TurnoRepository {

  private final IMongoService mongoService;
  private final IServicio servicioService;
  private final MongoLookupBuilder lookupBuilder;
  private final ServiceDocMongoMapper mapper;

  public MongoTurnoRepositoryImpl(IMongoService mongoService, IServicio servicioService, MongoLookupBuilder lookupBuilder,
                                  ServiceDocMongoMapper mapper) {
    this.mongoService = mongoService;
    this.servicioService = servicioService;
    this.lookupBuilder = lookupBuilder;
    this.mapper = mapper;
  }

  private Date convertStringToDate(String fechaStr) {
    try {
      LocalDate localDate = LocalDate.parse(fechaStr);
      return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    } catch (Exception e) {
      log.error("Error converting date string: {} to Date", fechaStr, e);
      return null;
    }
  }

  private boolean isTurnoOcupado(String fieldId, String entityId, String fecha, String horaInicio, String horaFin) {
    MongoCollection<Document> turnoCollection = mongoService.getCollection(BSONFields.TURNO_COLLECTION);
    Date fechaDate = convertStringToDate(fecha);
    if (fechaDate == null) {
      log.error("No se pudo convertir la fecha: {}", fecha);
      return false;
    }

    LocalTime inicioActual = LocalTime.parse(horaInicio);
    LocalTime finActual = LocalTime.parse(horaFin);

    Document filter = new Document()
        .append(fieldId, new ObjectId(entityId))
        .append(BSONFields.TURN_FECHA, fechaDate)
        .append(BSONFields.TURN_ESTADO, EstadoTurno.ASIGNADO.toString());

    var turnos = turnoCollection.find(filter).iterator();
    while (turnos.hasNext()) {
      Document turno = turnos.next();
      String turnoServicioId = turno.getObjectId(BSONFields.TURN_SERVICIO_ID).toHexString();

      int duracionServicioPrevio = servicioService.obtenerDuracionServicio(turnoServicioId);
      LocalTime turnoHoraInicio = LocalTime.parse(turno.getString(BSONFields.TURN_HORA));
      LocalTime turnoHoraFin = turnoHoraInicio.plusMinutes(duracionServicioPrevio);

      if (!(finActual.isBefore(turnoHoraInicio) || inicioActual.isAfter(turnoHoraFin) || inicioActual.equals(turnoHoraFin) || finActual.equals(turnoHoraInicio))) {
        log.info("Solapamiento encontrado con turno - Hora inicio: {}, Hora fin calculada: {}", turnoHoraInicio, turnoHoraFin);
        return true;
      }
    }

    log.info("No se encontraron solapamientos");
    return false;
  }

  @Override
  public String save(Turno turno) {
    MongoCollection<Document> turnoCollection = mongoService.getCollection(BSONFields.TURNO_COLLECTION);

    if (turno.getId() != null && !turno.getId().isEmpty()) {
      // Actualizar los campos TURN_ESTADO y TURN_PROFESIONAL_ID si el turno tiene un ID
      ObjectId objectId = new ObjectId(turno.getId());

      Document updateFields = new Document()
          .append(BSONFields.TURN_ESTADO, turno.getEstado())
          .append(BSONFields.TURN_PROFESIONAL_ID, turno.getProfesional() != null ? new ObjectId(turno.getProfesional().getId()) : null);

      turnoCollection.updateOne(
          new Document("_id", objectId),
          new Document("$set", updateFields)
      );

      return objectId.toHexString(); // Retorna el ID del documento actualizado
    } else {
      // Crear el documento completo solo si no existe un ID (es un nuevo turno)
      Document turnoDocument = new Document()
          .append(BSONFields.TURN_FECHA, turno.getFecha())
          .append(BSONFields.TURN_HORA, turno.getHora())
          .append(BSONFields.TURN_ESTADO, turno.getEstado())
          .append(BSONFields.TURN_CLIENTE_ID, turno.getCliente() != null ? new ObjectId(turno.getCliente().getId()) : null)
          .append(BSONFields.TURN_SERVICIO_ID, turno.getServicio() != null ? new ObjectId(turno.getServicio().getId()) : null)
          .append(BSONFields.TURN_PROFESIONAL_ID, turno.getProfesional() != null ? new ObjectId(turno.getProfesional().getId()) : null)
          .append(BSONFields.TURN_SUCURSAL_ID, turno.getSucursal() != null ? new ObjectId(turno.getSucursal().getId()) : null)
          .append(BSONFields.TURN_PROF_SELECTED_FOR_CLI, turno.isProfesionalSeleccionadoPorCliente());

      // Insertar el nuevo documento
      InsertOneResult result = turnoCollection.insertOne(turnoDocument);
      return result.getInsertedId().asObjectId().getValue().toHexString(); // Retorna el nuevo ID generado
    }
  }




  @Override
  public boolean isClienteOcupado(String clienteId, String fecha, String horaInicio, String horaFin) {
    return isTurnoOcupado(BSONFields.TURN_CLIENTE_ID, clienteId, fecha, horaInicio, horaFin);
  }

  @Override
  public boolean isProfesionalOcupado(String profesionalId, String fecha, String horaInicio, String horaFin) {
    return isTurnoOcupado(BSONFields.TURN_PROFESIONAL_ID, profesionalId, fecha, horaInicio, horaFin);
  }

  @Override
  public List<LocalTime[]> getIntervalosOcupados(String profesionalId, String fecha) {
    MongoCollection<Document> turnoCollection = mongoService.getCollection(BSONFields.TURNO_COLLECTION);
    Date fechaDate = convertStringToDate(fecha);

    if (fechaDate == null) {
      log.error("No se pudo convertir la fecha: {}", fecha);
      return new ArrayList<>();
    }

    Document filter = new Document()
        .append(BSONFields.TURN_PROFESIONAL_ID, new ObjectId(profesionalId))
        .append(BSONFields.TURN_FECHA, fechaDate)
        .append(BSONFields.TURN_ESTADO, EstadoTurno.ASIGNADO.toString());

    List<LocalTime[]> intervalosOcupados = new ArrayList<>();
    var turnos = turnoCollection.find(filter).iterator();

    while (turnos.hasNext()) {
      Document turno = turnos.next();
      String turnoServicioId = turno.getObjectId(BSONFields.TURN_SERVICIO_ID).toHexString();

      int duracionServicio = servicioService.obtenerDuracionServicio(turnoServicioId);
      LocalTime turnoHoraInicio = LocalTime.parse(turno.getString(BSONFields.TURN_HORA));
      LocalTime turnoHoraFin = turnoHoraInicio.plusMinutes(duracionServicio);

      intervalosOcupados.add(new LocalTime[]{turnoHoraInicio, turnoHoraFin});
    }

    log.info("Intervalos ocupados para el profesional {} en la fecha {}: {}", profesionalId, fecha, intervalosOcupados);
    return intervalosOcupados;
  }



  private List<Turno> executeTurnoQuery(List<Document> pipeline) {
    MongoCollection<Document> turnoCollection = mongoService.getCollection(BSONFields.TURNO_COLLECTION);
    List<Turno> turnos = new ArrayList<>();
    try (MongoCursor<Document> cursor = turnoCollection.aggregate(pipeline).iterator()) {
      while (cursor.hasNext()) {
        Document doc = cursor.next();
        turnos.add(mapToTurno(doc));
      }
    }
    return turnos;
  }


  private Turno mapToTurno(Document doc) {
    // Mapear y limpiar datos del profesional
    Profesional prof = mapper.mapProfesional((Document) ((List<?>) doc.get(BSONFields.PROFESIONALES)).get(0));
    limpiarDatosProfesional(prof);

    // Mapear y limpiar datos del servicio
    Servicio serv = mapper.mapServicio((Document) ((List<?>) doc.get(BSONFields.SERVICIOS)).get(0));
    limpiarDatosServicio(serv);

    Object cliente = doc.get(BSONFields.CLIENTE);
    Object sucursal = doc.get(BSONFields.SUCURSAL);

    return Turno.builder()
        .id(doc.getObjectId(BSONFields.TURN_ID).toHexString())
        .fecha(doc.getDate(BSONFields.TURN_FECHA))
        .hora(doc.getString(BSONFields.TURN_HORA))
        .estado(doc.getString(BSONFields.TURN_ESTADO))
        .profesionalSeleccionadoPorCliente(doc.getBoolean(BSONFields.TURN_PROF_SELECTED_FOR_CLI))
        .cliente(mapper.mapCliente(cliente == null ? null : (Document) ((List<?>) doc.get(BSONFields.CLIENTE)).get(0)))
        .sucursal(mapper.mapSucursal(sucursal == null ? null : (Document) ((List<?>) doc.get(BSONFields.SUCURSAL)).get(0)))
        .profesional(prof)
        .servicio(serv)
        .build();
  }

  private void limpiarDatosProfesional(Profesional prof) {
    if (prof != null) {
      prof.setHorarios(null);
      prof.setEspecialidadIds(null);
    }
  }

  private void limpiarDatosServicio(Servicio serv) {
    if (serv != null) {
      serv.setEspecialidadId(null);
    }
  }



  @Override
  public List<Turno> findAllGroupedByCliente() {
    List<Document> pipeline = lookupBuilder.withCliente().withProfesional().withServicio().build();
    return executeTurnoQuery(pipeline);
  }

  @Override
  public Optional<Turno> findById(String turnoId) {
    List<Document> pipeline = lookupBuilder.withProfesional().withServicio().withSucursal().build();
    pipeline.add(0, new Document("$match", new Document(BSONFields.TURN_ID, new ObjectId(turnoId))));
    List<Turno> turnos = executeTurnoQuery(pipeline);
    return turnos.isEmpty() ? Optional.empty() : Optional.of(turnos.get(0));
  }

  @Override
  public List<Turno> findAllByClienteId(String clienteId) {
    List<Document> pipeline = lookupBuilder.withProfesional().withServicio().build();
    pipeline.add(0, new Document("$match", new Document(BSONFields.TURN_CLIENTE_ID, new ObjectId(clienteId))));
    return executeTurnoQuery(pipeline);
  }


}
