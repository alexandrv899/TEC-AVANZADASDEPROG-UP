package com.up.spa.application.model.mappers;

import static org.junit.jupiter.api.Assertions.*;

import com.up.spa.application.model.BSONFields;
import com.up.spa.application.model.Cliente;
import com.up.spa.application.model.Horario;
import com.up.spa.application.model.Profesional;
import com.up.spa.application.model.Servicio;
import com.up.spa.application.model.Sucursal;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class ServiceDocMongoMapperTest {

  private ServiceDocMongoMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ServiceDocMongoMapper();
  }

  @Test
  void testMapServicio() {
    Document doc = new Document()
        .append(BSONFields.SERVICIO_ID, new ObjectId("64df18b9c44e9c2c4fd1a111"))
        .append(BSONFields.SERVICIO_NOMBRE, "Servicio Test")
        .append(BSONFields.SERVICIO_DURACION, 60)
        .append(BSONFields.SERVICIO_ESPECIALIDAD_ID, new ObjectId("64df18b9c44e9c2c4fd1a222"));

    Servicio servicio = mapper.mapServicio(doc);

    assertNotNull(servicio);
    assertEquals("64df18b9c44e9c2c4fd1a111", servicio.getId());
    assertEquals("Servicio Test", servicio.getNombre());
    assertEquals(60, servicio.getDuracion());
    assertEquals("64df18b9c44e9c2c4fd1a222", servicio.getEspecialidadId());
  }

  @Test
  void testMapServicios() {
    Document doc1 = new Document()
        .append(BSONFields.SERVICIO_ID, new ObjectId("64df18b9c44e9c2c4fd1a111"))
        .append(BSONFields.SERVICIO_NOMBRE, "Servicio 1")
        .append(BSONFields.SERVICIO_DURACION, 45)
        .append(BSONFields.SERVICIO_ESPECIALIDAD_ID, new ObjectId("64df18b9c44e9c2c4fd1a222"));

    Document doc2 = new Document()
        .append(BSONFields.SERVICIO_ID, new ObjectId("64df18b9c44e9c2c4fd1a333"))
        .append(BSONFields.SERVICIO_NOMBRE, "Servicio 2")
        .append(BSONFields.SERVICIO_DURACION, 30)
        .append(BSONFields.SERVICIO_ESPECIALIDAD_ID, new ObjectId("64df18b9c44e9c2c4fd1a444"));

    List<Servicio> servicios = mapper.mapServicios(Arrays.asList(doc1, doc2));

    assertNotNull(servicios);
    assertEquals(2, servicios.size());
    assertEquals("Servicio 1", servicios.get(0).getNombre());
    assertEquals("Servicio 2", servicios.get(1).getNombre());
  }

  @Test
  void testMapProfesional() {
    Document doc = new Document()
        .append(BSONFields.PROFESIONAL_ID, new ObjectId("64df18b9c44e9c2c4fd1a555"))
        .append(BSONFields.PROFESIONAL_NOMBRE, "Profesional Test")
        .append(BSONFields.PROFESIONAL_EMAIL, "profesional@test.com")
        .append(BSONFields.PROFESIONAL_ESPECIALIDADES, Arrays.asList(
            new ObjectId("64df18b9c44e9c2c4fd1a666"),
            new ObjectId("64df18b9c44e9c2c4fd1a777")))
        .append(BSONFields.PROFESIONAL_HORARIOS, Arrays.asList(
            new Document(BSONFields.HORARIO_DIA_SEMANA, "Lunes")
                .append(BSONFields.HORARIO_HORA_INICIO, "09:00")
                .append(BSONFields.HORARIO_HORA_FIN, "17:00")));

    Profesional profesional = mapper.mapProfesional(doc);

    assertNotNull(profesional);
    assertEquals("64df18b9c44e9c2c4fd1a555", profesional.getId());
    assertEquals("Profesional Test", profesional.getNombre());
    assertEquals("profesional@test.com", profesional.getEmail());
    assertEquals(2, profesional.getEspecialidadIds().size());
    assertEquals("64df18b9c44e9c2c4fd1a666", profesional.getEspecialidadIds().get(0));
    assertEquals("64df18b9c44e9c2c4fd1a777", profesional.getEspecialidadIds().get(1));
    assertEquals(1, profesional.getHorarios().size());
    assertEquals("Lunes", profesional.getHorarios().get(0).getDiaSemana());
    assertEquals("09:00", profesional.getHorarios().get(0).getHoraInicio());
    assertEquals("17:00", profesional.getHorarios().get(0).getHoraFin());
  }

  @Test
  void testMapProfesionales() {
    Document doc1 = new Document()
        .append(BSONFields.PROFESIONAL_ID, new ObjectId("64df18b9c44e9c2c4fd1a888"))
        .append(BSONFields.PROFESIONAL_NOMBRE, "Profesional 1")
        .append(BSONFields.PROFESIONAL_EMAIL, "pro1@test.com")
        .append(BSONFields.PROFESIONAL_ESPECIALIDADES, Arrays.asList(
            new ObjectId("64df18b9c44e9c2c4fd1a666"),
            new ObjectId("64df18b9c44e9c2c4fd1a777")))
        .append(BSONFields.PROFESIONAL_HORARIOS, Arrays.asList(
            new Document(BSONFields.HORARIO_DIA_SEMANA, "Martes")
                .append(BSONFields.HORARIO_HORA_INICIO, "10:00")
                .append(BSONFields.HORARIO_HORA_FIN, "18:00")));

    Document doc2 = new Document()
        .append(BSONFields.PROFESIONAL_ID, new ObjectId("64df18b9c44e9c2c4fd1a999"))
        .append(BSONFields.PROFESIONAL_NOMBRE, "Profesional 2")
        .append(BSONFields.PROFESIONAL_EMAIL, "pro2@test.com")
        .append(BSONFields.PROFESIONAL_ESPECIALIDADES, Arrays.asList(
            new ObjectId("64df18b9c44e9c2c4fd1a888")))
        .append(BSONFields.PROFESIONAL_HORARIOS, Arrays.asList(
            new Document(BSONFields.HORARIO_DIA_SEMANA, "Miércoles")
                .append(BSONFields.HORARIO_HORA_INICIO, "11:00")
                .append(BSONFields.HORARIO_HORA_FIN, "19:00")));

    List<Profesional> profesionales = mapper.mapProfesionales(Arrays.asList(doc1, doc2));

    assertNotNull(profesionales);
    assertEquals(2, profesionales.size());

    // Verificar el primer profesional
    Profesional prof1 = profesionales.get(0);
    assertEquals("64df18b9c44e9c2c4fd1a888", prof1.getId());
    assertEquals("Profesional 1", prof1.getNombre());
    assertEquals("pro1@test.com", prof1.getEmail());
    assertEquals(2, prof1.getEspecialidadIds().size());
    assertEquals("64df18b9c44e9c2c4fd1a666", prof1.getEspecialidadIds().get(0));
    assertEquals("64df18b9c44e9c2c4fd1a777", prof1.getEspecialidadIds().get(1));
    assertEquals(1, prof1.getHorarios().size());
    assertEquals("Martes", prof1.getHorarios().get(0).getDiaSemana());
    assertEquals("10:00", prof1.getHorarios().get(0).getHoraInicio());
    assertEquals("18:00", prof1.getHorarios().get(0).getHoraFin());

    // Verificar el segundo profesional
    Profesional prof2 = profesionales.get(1);
    assertEquals("64df18b9c44e9c2c4fd1a999", prof2.getId());
    assertEquals("Profesional 2", prof2.getNombre());
    assertEquals("pro2@test.com", prof2.getEmail());
    assertEquals(1, prof2.getEspecialidadIds().size());
    assertEquals("64df18b9c44e9c2c4fd1a888", prof2.getEspecialidadIds().get(0));
    assertEquals(1, prof2.getHorarios().size());
    assertEquals("Miércoles", prof2.getHorarios().get(0).getDiaSemana());
    assertEquals("11:00", prof2.getHorarios().get(0).getHoraInicio());
    assertEquals("19:00", prof2.getHorarios().get(0).getHoraFin());
  }

  @Test
  void testMapHorarios() {
    List<Document> horariosDocs = Arrays.asList(
        new Document(BSONFields.HORARIO_DIA_SEMANA, "Lunes")
            .append(BSONFields.HORARIO_HORA_INICIO, "08:00")
            .append(BSONFields.HORARIO_HORA_FIN, "12:00"),
        new Document(BSONFields.HORARIO_DIA_SEMANA, "Martes")
            .append(BSONFields.HORARIO_HORA_INICIO, "13:00")
            .append(BSONFields.HORARIO_HORA_FIN, "17:00")
    );

    List<Horario> horarios = mapper.mapHorarios(horariosDocs);

    assertNotNull(horarios);
    assertEquals(2, horarios.size());

    // Verificar el primer horario
    Horario horario1 = horarios.get(0);
    assertEquals("Lunes", horario1.getDiaSemana());
    assertEquals("08:00", horario1.getHoraInicio());
    assertEquals("12:00", horario1.getHoraFin());

    // Verificar el segundo horario
    Horario horario2 = horarios.get(1);
    assertEquals("Martes", horario2.getDiaSemana());
    assertEquals("13:00", horario2.getHoraInicio());
    assertEquals("17:00", horario2.getHoraFin());
  }

  @Test
  void testMapCliente() {
    Document doc = new Document()
        .append(BSONFields.CLIENTE_ID, new ObjectId("64df18b9c44e9c2c4fd1b000"))
        .append(BSONFields.CLIENTE_NOMBRE, "Cliente Test")
        .append(BSONFields.CLIENTE_EMAIL, "cliente@test.com");

    Cliente cliente = mapper.mapCliente(doc);

    assertNotNull(cliente);
    assertEquals("64df18b9c44e9c2c4fd1b000", cliente.getId());
    assertEquals("Cliente Test", cliente.getNombre());
    assertEquals("cliente@test.com", cliente.getEmail());
  }

  @Test
  void testMapSucursal() {
    Document doc = new Document()
        .append(BSONFields.SUCURSAL_ID, new ObjectId("64df18b9c44e9c2c4fd1b111"))
        .append(BSONFields.SUCURSAL_NOMBRE, "Sucursal Test");

    Sucursal sucursal = mapper.mapSucursal(doc);

    assertNotNull(sucursal);
    assertEquals("64df18b9c44e9c2c4fd1b111", sucursal.getId());
    assertEquals("Sucursal Test", sucursal.getNombre());
  }
}
