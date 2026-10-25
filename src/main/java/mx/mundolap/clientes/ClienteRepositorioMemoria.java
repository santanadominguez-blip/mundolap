package mx.mundolap.clientes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ClienteRepositorioMemoria implements ClienteRepositorio {
    private final Map<String, Cliente> datos = new LinkedHashMap<>();
    private int contador = 0;

    @Override public synchronized List<Cliente> listar() { return new ArrayList<>(datos.values()); }

    @Override public synchronized Optional<Cliente> buscarPorId(String id) { return Optional.ofNullable(datos.get(id)); }

    @Override public synchronized void guardar(Cliente c) { datos.put(c.idCliente(), c); }

    @Override public synchronized String nuevoId() { return String.format("CLI-%03d", ++contador); }
}
