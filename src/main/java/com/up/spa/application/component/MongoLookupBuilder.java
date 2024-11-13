package com.up.spa.application.component;

import com.up.spa.application.model.BSONFields;
import com.up.spa.application.utils.Lookups;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class MongoLookupBuilder {
  private final List<Document> pipeline = new ArrayList<>();

  public MongoLookupBuilder withProfesionales() {
    pipeline.add(Lookups.lookup(
        "Profesional",
        "profesionalIds",
        "_id",
        BSONFields.PROFESIONALES
    ));
    return this;
  }

  public MongoLookupBuilder withCliente() {
    pipeline.add(Lookups.lookup(
        "Cliente",
        BSONFields.TURN_CLIENTE_ID,
        "_id",
        BSONFields.CLIENTE
    ));
    return this;
  }

  public MongoLookupBuilder withProfesional() {
    pipeline.add(Lookups.lookup(
        "Profesional",
        "profesionalId",
        "_id",
        BSONFields.PROFESIONALES
    ));
    return this;
  }

  public MongoLookupBuilder withServicios() {
    pipeline.add(Lookups.lookup(
        "Servicio",
        "servicioIds",
        "_id",
        BSONFields.SERVICIOS
    ));
    return this;
  }

  public MongoLookupBuilder withServicio() {
    pipeline.add(Lookups.lookup(
        "Servicio",
        "servicioId",
        "_id",
        BSONFields.SERVICIOS
    ));
    return this;
  }

  public MongoLookupBuilder withSucursal() {
    pipeline.add(Lookups.lookup(
        "Sucursal",
        "sucursalId",
        "_id",
        BSONFields.SUCURSAL
    ));
    return this;
  }


  public MongoLookupBuilder withEspecialidades() {
    pipeline.add(Document.parse(
        "{ $lookup: { " +
            "from: 'Especialidad', " +
            "localField: 'profesionales.especialidadIds', " +
            "foreignField: '_id', " +
            "as: 'especialidades' " +
            "} }"
    ));
    return this;
  }

  public List<Document> build() {
    List<Document> result = new ArrayList<>(pipeline);
    pipeline.clear();
    return result;
  }
}