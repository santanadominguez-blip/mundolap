package mx.mundolap.equipos;

import java.util.List;
import java.util.Optional;

public interface EquipoRepositorio {
    List<Equipo> listarPorCliente(String idCliente);

    Optional<Equipo> buscarPorId(String idEquipo);

    Optional<Equipo> buscarPorSerie(String numeroSerie);

    void guardar(Equipo equipo);

    String nuevoId();
}
