package com.up.spa.application.model;


public class BSONFields {

  // Constantes para el documento Turno
  public static final String TURN_ID = "_id";
  public static final String TURN_FECHA = "fecha";
  public static final String TURN_HORA = "hora";
  public static final String TURN_ESTADO = "estado";
  public static final String TURN_CLIENTE_ID = "clienteId";
  public static final String TURN_SERVICIO_ID = "servicioId";
  public static final String TURN_PROFESIONAL_ID = "profesionalId";
  public static final String TURN_SUCURSAL_ID = "sucursalId";
  public static final String TURN_PROF_SELECTED_FOR_CLI = "profesionalSeleccionadoPorCliente";

  // Constantes para el documento Cliente
  public static final String CLIENTE_ID = "_id";
  public static final String CLIENTE_NOMBRE = "nombre";
  public static final String CLIENTE_EMAIL = "email";
  public static final String CLIENTE_TURNOS = "turnos";

  // Constantes para el documento Servicio
  public static final String SERVICIO_ID = "_id";
  public static final String SERVICIO_NOMBRE = "nombre";
  public static final String SERVICIO_DESCRIPCION = "descripcion";
  public static final String SERVICIO_DURACION = "duracion";
  public static final String SERVICIO_ESPECIALIDAD_ID = "especialidadId";

  // Constantes para el documento Sucursal
  public static final String SUCURSAL_ID = "_id";
  public static final String SUCURSAL_NOMBRE = "nombre";
  public static final String SUCURSAL_DIRECCION = "direccion";
  public static final String SUCURSAL_PROFESIONALES = "profesionalIds";
  public static final String SUCURSAL_SERVICIOS = "servicioIds";
  public static final String SUCURSAL_HORARIOS = "horarios";

  // Constantes para el documento Profesional
  public static final String PROFESIONAL_ID = "_id";
  public static final String PROFESIONAL_NOMBRE = "nombre";
  public static final String PROFESIONAL_EMAIL = "email";
  public static final String PROFESIONAL_ESPECIALIDADES = "especialidadIds";
  public static final String PROFESIONAL_HORARIOS = "horarios";
  public static final String PROFESIONAL_SUCURSALES = "sucursalIds";

  // Constantes para el documento Horario
  public static final String HORARIO_DIA_SEMANA = "diaSemana";
  public static final String HORARIO_HORA_INICIO = "horaInicio";
  public static final String HORARIO_HORA_FIN = "horaFin";

  public static final String TURNO_COLLECTION = "Turno";
  public static final String SERVICIO_COLLECTION = "Servicio";
  public static final String SUCURSAL_COLLECTION = "Sucursal";
  public static final String HORARIO_COLLECTION = "Horario";
  public static final String CLIENTE_COLLECTION = "Cliente";
  public static final String PROFESIONALES = "profesionales";
  public static final String SERVICIOS = "servicios";
  public static final String CLIENTE = "cliente";
  public static final String SUCURSAL = "sucuarsal";


}
