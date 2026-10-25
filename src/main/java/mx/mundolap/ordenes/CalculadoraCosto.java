package mx.mundolap.ordenes;

import java.math.BigDecimal;
import mx.mundolap.equipos.TipoDispositivo;

/** Estrategia para calcular el costo estimado al recibir un equipo (RF-17). */
public interface CalculadoraCosto {
    BigDecimal costoEstimado(TipoDispositivo tipo);
}
