package com.up.spa.application.repository.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.up.spa.application.model.BSONFields;
import com.up.spa.application.model.Cliente;
import com.up.spa.application.model.Turno;
import com.up.spa.application.repository.ClienteRepository;
import com.up.spa.application.services.IMongoService;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MongoClienteRepositoryImpl implements ClienteRepository {

  private final IMongoService mongoService;

  public MongoClienteRepositoryImpl(IMongoService mongoService) {
    this.mongoService = mongoService;
  }

  @Override
  public Optional<Cliente> findByEmail(String email) {
    MongoCollection<Document> clienteCollection = mongoService.getCollection(BSONFields.CLIENTE_COLLECTION);

    // Construir el pipeline de agregación
    List<Document> pipeline = new ArrayList<>();
    pipeline.add(new Document("$match", new Document(BSONFields.CLIENTE_EMAIL, email)));

    // Agregar el $lookup para los turnos
    pipeline.add(new Document("$lookup", new Document("from", BSONFields.TURNO_COLLECTION)
        .append("localField", BSONFields.CLIENTE_TURNOS)
        .append("foreignField", BSONFields.TURN_ID)
        .append("as", BSONFields.CLIENTE_TURNOS)));

    // Ejecutar la agregación con el pipeline
    List<Document> result = clienteCollection.aggregate(pipeline).into(new ArrayList<>());

    if (result.isEmpty()) {
      return Optional.empty();
    }

    // Convertir el primer documento encontrado a un Cliente
    Document clienteDocument = result.get(0);
    Cliente cliente = new Cliente();
    cliente.setId(clienteDocument.getObjectId(BSONFields.CLIENTE_ID).toHexString());
    cliente.setNombre(clienteDocument.getString(BSONFields.CLIENTE_NOMBRE));
    cliente.setEmail(clienteDocument.getString(BSONFields.CLIENTE_EMAIL));

    // Convertir la lista de turnos obtenida del lookup a objetos Turno
    List<Document> turnoDocuments = (List<Document>) clienteDocument.get(BSONFields.CLIENTE_TURNOS);
    if (turnoDocuments != null) {
      List<Turno> turnos = turnoDocuments.stream()
          .map(this::convertToTurno)
          .collect(Collectors.toList());
      cliente.setTurnos(turnos);
    }

    return Optional.of(cliente);
  }

  private Turno convertToTurno(Document document) {
    Turno turno = new Turno();
    turno.setId(document.getObjectId(BSONFields.TURN_ID).toHexString());
    turno.setFecha(document.getDate(BSONFields.TURN_FECHA));
    turno.setHora(document.getString(BSONFields.TURN_HORA));
    turno.setEstado(document.getString(BSONFields.TURN_ESTADO));
    return turno;
  }

  @Override
  public void addTurnoToCliente(String clienteId, String turnoId) {
    MongoCollection<Document> clienteCollection = mongoService.getCollection(BSONFields.CLIENTE_COLLECTION);
    clienteCollection.updateOne(
        Filters.eq(BSONFields.CLIENTE_ID, new ObjectId(clienteId)),
        Updates.addToSet(BSONFields.CLIENTE_TURNOS, new ObjectId(turnoId))
    );
  }
}
