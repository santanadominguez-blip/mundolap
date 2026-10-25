package mx.mundolap.ordenes;

import java.util.List;
import java.util.Optional;

public interface OrdenRepositorio {
    List<OrdenServicio> listar();

    Optional<OrdenServicio> buscarPorFolio(String folio);

    void guardar(OrdenServicio orden);

    String nuevoFolio();
}
