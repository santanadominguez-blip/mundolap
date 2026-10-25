package mx.mundolap.ordenes;

import java.math.BigDecimal;
import mx.mundolap.equipos.TipoDispositivo;

/**
 * TEMPORAL: el PRY03 pide calcular el costo estimado pero no define tarifas.
 * Estos importes son de ejemplo; se reemplaza esta clase cuando el taller entregue su tarifario.
 */
public final class CostoDiagnosticoEjemplo implements CalculadoraCosto {
    @Override
    public BigDecimal costoEstimado(TipoDispositivo tipo) {
        return switch (tipo) {
            case CELULAR, TABLET -> new BigDecimal("250.00");
            case LAPTOP -> new BigDecimal("350.00");
            case PC_ESCRITORIO, OTRO -> new BigDecimal("300.00");
        };
    }
}
