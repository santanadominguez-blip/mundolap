package mx.mundolap.pagos;

import java.util.List;
import java.util.Set;
import mx.mundolap.compartido.ItemMenu;
import mx.mundolap.compartido.Modulo;
import mx.mundolap.compartido.Rol;
import mx.mundolap.compartido.web.Router;

/**
 * Módulo de pagos (P-09 Registrar pago o abono).
 * FASE ACTUAL: solo la vista. Pendiente: entidad Pago, PagoRepositorio (+ versión en memoria),
 * PagoServicio (RF-40, RF-41, RF-42) y la ruta POST /pagos/registrar.
 */
public final class PagosModulo implements Modulo {
    private static final Set<Rol> ROLES = Set.of(Rol.RECEPCIONISTA);
    private final PagosControlador controlador = new PagosControlador();

    @Override public String nombre() { return "Pagos"; }

    @Override
    public void registrarRutas(Router r) {
        r.get("/pagos", ROLES, controlador::pantalla);
    }

    @Override
    public List<ItemMenu> menu() { return List.of(new ItemMenu("Pagos", "/pagos", ROLES, 40)); }
}
