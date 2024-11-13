package com.up.spa.application.model.mappers;


import com.up.spa.application.model.BSONFields;
import com.up.spa.application.model.Cliente;
import com.up.spa.application.model.Horario;
import com.up.spa.application.model.Profesional;
import com.up.spa.application.model.Servicio;
import com.up.spa.application.model.Sucursal;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceDocMongoMapper {

  public Servicio mapServicio(Document doc) {
    return  doc == null ? null : Servicio.builder()
        .id(doc.getObjectId(BSONFields.SERVICIO_ID).toHexString())
        .nombre(doc.getString(BSONFields.SERVICIO_NOMBRE))
        .duracion(doc.getInteger(BSONFields.SERVICIO_DURACION))
        .especialidadId(doc.getObjectId(BSONFields.SERVICIO_ESPECIALIDAD_ID).toHexString())
        .build();
  }

  public List<Servicio> mapServicios(List<Document> serviciosDocs) {
    return serviciosDocs == null ? null : serviciosDocs.stream()
        .map(this::mapServicio)
        .collect(Collectors.toList());
  }

  public Profesional mapProfesional(Document doc) {
    return doc == null ? null :  Profesional.builder()
        .id(doc.getObjectId(BSONFields.PROFESIONAL_ID).toHexString())
        .nombre(doc.getString(BSONFields.PROFESIONAL_NOMBRE))
        .email(doc.getString(BSONFields.PROFESIONAL_EMAIL))
        .especialidadIds(((List<ObjectId>) doc.get(BSONFields.PROFESIONAL_ESPECIALIDADES))
            .stream()
            .map(ObjectId::toHexString)
            .collect(Collectors.toList()))
        .horarios(mapHorarios((List<Document>) doc.get(BSONFields.PROFESIONAL_HORARIOS)))
        .build();
  }

  public List<Profesional> mapProfesionales(List<Document> profesionalesDocs) {
    return profesionalesDocs == null ? null : profesionalesDocs.stream()
        .map(this::mapProfesional)
        .collect(Collectors.toList());
  }

  public List<Horario> mapHorarios(List<Document> horariosDocs) {
    return  horariosDocs == null ? null : horariosDocs.stream()
        .map(horarioDoc -> Horario.builder()
            .diaSemana(horarioDoc.getString(BSONFields.HORARIO_DIA_SEMANA))
            .horaInicio(horarioDoc.getString(BSONFields.HORARIO_HORA_INICIO))
            .horaFin(horarioDoc.getString(BSONFields.HORARIO_HORA_FIN))
            .build())
        .collect(Collectors.toList());
  }

  public Cliente mapCliente(Document doc) {
    return doc == null ? null: Cliente.builder()
        .nombre(doc.getString(BSONFields.CLIENTE_NOMBRE))
        .email(doc.getString(BSONFields.CLIENTE_EMAIL))
        .id(doc.getObjectId(BSONFields.CLIENTE_ID).toHexString())
        .build();
  }

  public Sucursal mapSucursal(Document doc) {
    return doc == null ? null: Sucursal.builder()
        .nombre(doc.getString(BSONFields.SUCURSAL_NOMBRE))
        .direccion(doc.getString(BSONFields.CLIENTE_EMAIL))
        .id(doc.getObjectId(BSONFields.SUCURSAL_ID).toHexString())
        .build();
  }




}
