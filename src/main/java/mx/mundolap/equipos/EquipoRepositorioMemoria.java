package mx.mundolap.equipos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class EquipoRepositorioMemoria implements EquipoRepositorio {
    private final Map<String, Equipo> datos = new LinkedHashMap<>();
    private int contador = 0;

    @Override
    public synchronized List<Equipo> listarPorCliente(String idCliente) {
        List<Equipo> r = new ArrayList<>();
        for (Equipo e : datos.values()) if (e.idCliente().equals(idCliente)) r.add(e);
        return r;
    }

    @Override public synchronized Optional<Equipo> buscarPorId(String id) { return Optional.ofNullable(datos.get(id)); }

    @Override
    public synchronized Optional<Equipo> buscarPorSerie(String serie) {
        return datos.values().stream().filter(e -> e.numeroSerie().equalsIgnoreCase(serie)).findFirst();
    }

    @Override public synchronized void guardar(Equipo e) { datos.put(e.idEquipo(), e); }

    @Override public synchronized String nuevoId() { return String.format("EQ-%03d", ++contador); }
}
