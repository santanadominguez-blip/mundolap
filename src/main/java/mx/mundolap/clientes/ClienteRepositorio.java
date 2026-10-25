package mx.mundolap.clientes;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositorio {
    List<Cliente> listar();

    Optional<Cliente> buscarPorId(String idCliente);

    void guardar(Cliente cliente);

    String nuevoId();
}
