package mx.mundolap.ordenes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class OrdenRepositorioMemoria implements OrdenRepositorio {
    private final Map<String, OrdenServicio> datos = new LinkedHashMap<>();
    private int contador = 0;

    @Override public synchronized List<OrdenServicio> listar() { return new ArrayList<>(datos.values()); }

    @Override public synchronized Optional<OrdenServicio> buscarPorFolio(String f) { return Optional.ofNullable(datos.get(f)); }

    @Override public synchronized void guardar(OrdenServicio o) { datos.put(o.folio(), o); }

    @Override public synchronized String nuevoFolio() { return String.format("OS-%04d", ++contador); }
}
