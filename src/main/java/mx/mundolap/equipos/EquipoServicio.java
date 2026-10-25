package mx.mundolap.equipos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mx.mundolap.compartido.RelacionOrdenes;

/** Reglas de negocio de equipos (RF-11). */
public final class EquipoServicio {
    private final EquipoRepositorio repositorio;
    private RelacionOrdenes ordenes = RelacionOrdenes.VACIA;

    public EquipoServicio(EquipoRepositorio repositorio) { this.repositorio = repositorio; }

    public void usarRelacionOrdenes(RelacionOrdenes ordenes) { this.ordenes = ordenes; }

    public List<Equipo> deCliente(String idCliente) { return repositorio.listarPorCliente(idCliente); }

    public Optional<Equipo> buscarPorId(String id) { return repositorio.buscarPorId(id); }

    public int totalOrdenes(String idEquipo) { return ordenes.totalDeEquipo(idEquipo); }

    public Equipo normalizar(Equipo e) {
        return new Equipo(e.idEquipo().trim(), e.idCliente().trim(), e.tipoDispositivo(),
                e.marca().trim(), e.modelo().trim(), e.numeroSerie().trim());
    }

    public List<String> validar(Equipo e) {
        List<String> errores = new ArrayList<>();
        if (e.tipoDispositivo() == null) errores.add("Seleccione el tipo de dispositivo.");
        if (e.marca().isBlank()) errores.add("La marca es obligatoria.");
        if (e.modelo().isBlank()) errores.add("El modelo es obligatorio.");
        if (!e.numeroSerie().isBlank()) {
            repositorio.buscarPorSerie(e.numeroSerie()).ifPresent(existente ->
                    errores.add("Ya existe un equipo con ese número de serie (" + existente.idEquipo()
                            + "). Selecciónelo de la tabla para conservar su historial."));
        }
        return errores;
    }

    /** RF-11: el equipo queda vinculado al historial de su cliente. */
    public Equipo guardar(Equipo e) {
        Equipo nuevo = new Equipo(repositorio.nuevoId(), e.idCliente(), e.tipoDispositivo(),
                e.marca(), e.modelo(), e.numeroSerie());
        repositorio.guardar(nuevo);
        return nuevo;
    }
}
