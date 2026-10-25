package mx.mundolap.ordenes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import mx.mundolap.compartido.EstadoOrden;

/**
 * Entidad ORDEN-SERVICIO del diccionario de datos (PRY03) más idCliente e idEquipo (relaciones del
 * modelo de dominio) y costoEstimado (RF-17), que el diccionario aún no incluye.
 */
public record OrdenServicio(String folio, String idCliente, String idEquipo, String problemaReportado,
                            String diagnosticoTecnico, EstadoOrden estadoActual, LocalDateTime fechaRecepcion,
                            LocalDateTime fechaEntregaEstimada, Prioridad prioridad, BigDecimal costoEstimado) { }
