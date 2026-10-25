package mx.mundolap.equipos;

/** Entidad EQUIPO del diccionario de datos; idCliente es la relación Cliente 1—* Equipo del modelo de dominio. */
public record Equipo(String idEquipo, String idCliente, TipoDispositivo tipoDispositivo,
                     String marca, String modelo, String numeroSerie) { }
