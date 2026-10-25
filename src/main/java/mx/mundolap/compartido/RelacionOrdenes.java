package mx.mundolap.compartido;

import java.util.Optional;

/**
 * Contrato por el que los demás módulos consultan información de órdenes sin depender
 * del módulo de órdenes. Si ese módulo no existe se usa VACIA y el resto sigue funcionando.
 */
public interface RelacionOrdenes {

    int activasDeCliente(String idCliente);

    int totalDeEquipo(String idEquipo);

    Optional<String> clienteDeFolio(String folio);

    int cantidadEnEstado(EstadoOrden estado);

    RelacionOrdenes VACIA = new RelacionOrdenes() {
        public int activasDeCliente(String idCliente) { return 0; }
        public int totalDeEquipo(String idEquipo) { return 0; }
        public Optional<String> clienteDeFolio(String folio) { return Optional.empty(); }
        public int cantidadEnEstado(EstadoOrden estado) { return 0; }
    };
}
