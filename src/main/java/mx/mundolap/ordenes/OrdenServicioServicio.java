package mx.mundolap.ordenes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import mx.mundolap.clientes.ClienteServicio;
import mx.mundolap.compartido.EstadoOrden;
import mx.mundolap.compartido.RelacionOrdenes;
import mx.mundolap.equipos.Equipo;
import mx.mundolap.equipos.EquipoServicio;

/** Reglas de negocio de órdenes de servicio (RF-16, RF-17, RF-19, RF-20, RF-27). */
public final class OrdenServicioServicio implements RelacionOrdenes {
    private final OrdenRepositorio repositorio;
    private final ClienteServicio clientes;
    private final EquipoServicio equipos;
    private final CalculadoraCosto calculadora;

    public OrdenServicioServicio(OrdenRepositorio repositorio, ClienteServicio clientes,
                                 EquipoServicio equipos, CalculadoraCosto calculadora) {
        this.repositorio = repositorio;
        this.clientes = clientes;
        this.equipos = equipos;
        this.calculadora = calculadora;
    }

    public BigDecimal costoPara(Equipo equipo) { return calculadora.costoEstimado(equipo.tipoDispositivo()); }

    public List<String> validar(String idCliente, String idEquipo, String problema, LocalDate fechaEntrega) {
        List<String> errores = new ArrayList<>();
        if (clientes.buscarPorId(idCliente).isEmpty()) errores.add("El cliente seleccionado no existe.");
        Optional<Equipo> equipo = equipos.buscarPorId(idEquipo);
        if (equipo.isEmpty() || !equipo.get().idCliente().equals(idCliente)) {
            errores.add("Seleccione un equipo registrado de este cliente.");
        }
        if (problema.isBlank()) errores.add("Describa el problema reportado.");
        if (fechaEntrega == null) errores.add("Indique la fecha de entrega estimada.");
        else if (fechaEntrega.isBefore(LocalDate.now())) errores.add("La fecha de entrega estimada no puede ser anterior a hoy.");
        return errores;
    }

    /**
     * RF-16 estado inicial "Recibido"; RF-17 costo estimado; RF-19 folio único;
     * RF-20 la orden queda asociada al cliente, al equipo y al problema reportado.
     */
    public OrdenServicio crear(String idCliente, String idEquipo, String problema,
                               Prioridad prioridad, LocalDate fechaEntrega) {
        List<String> errores = validar(idCliente, idEquipo, problema, fechaEntrega);
        if (!errores.isEmpty()) throw new IllegalArgumentException(String.join(" ", errores));
        Equipo equipo = equipos.buscarPorId(idEquipo).orElseThrow();
        OrdenServicio orden = new OrdenServicio(repositorio.nuevoFolio(), idCliente, idEquipo, problema.trim(),
                "", EstadoOrden.RECIBIDO, LocalDateTime.now(), fechaEntrega.atStartOfDay(),
                prioridad, costoPara(equipo));
        repositorio.guardar(orden);
        return orden;
    }

    /** Se usa para cargar datos de ejemplo con otro estado/fecha, sin pasar por las validaciones de recepción. */
    public void registrarExistente(OrdenServicio orden) { repositorio.guardar(orden); }

    public String siguienteFolio() { return repositorio.nuevoFolio(); }

    public Optional<OrdenServicio> buscarPorFolio(String folio) {
        return repositorio.buscarPorFolio(folio == null ? "" : folio.trim().toUpperCase(Locale.ROOT));
    }

    // ---------- RelacionOrdenes ----------
    @Override
    public int activasDeCliente(String idCliente) {
        return (int) repositorio.listar().stream()
                .filter(o -> o.idCliente().equals(idCliente) && o.estadoActual().activo()).count();
    }

    @Override
    public int totalDeEquipo(String idEquipo) {
        return (int) repositorio.listar().stream().filter(o -> o.idEquipo().equals(idEquipo)).count();
    }

    @Override
    public Optional<String> clienteDeFolio(String folio) {
        return repositorio.buscarPorFolio(folio).map(OrdenServicio::idCliente);
    }

    @Override
    public int cantidadEnEstado(EstadoOrden estado) {
        return (int) repositorio.listar().stream().filter(o -> o.estadoActual() == estado).count();
    }
}
