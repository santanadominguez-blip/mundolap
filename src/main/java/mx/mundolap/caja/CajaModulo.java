package mx.mundolap.caja;

import java.util.List;
import java.util.Set;
import mx.mundolap.compartido.ItemMenu;
import mx.mundolap.compartido.Modulo;
import mx.mundolap.compartido.Rol;
import mx.mundolap.compartido.web.Router;

/**
 * Módulo de cierre de caja (P-15).
 * FASE ACTUAL: solo la vista. Pendiente: totales por método de pago, diferencia, confirmación y
 * exportación a PDF (RF-60, RF-61). Depende de la entidad Pago (modelo relacional, 3.3).
 */
public final class CajaModulo implements Modulo {
    private static final Set<Rol> ROLES = Set.of(Rol.GERENTE);
    private final CajaControlador controlador = new CajaControlador();

    @Override public String nombre() { return "Cierre de caja"; }

    @Override
    public void registrarRutas(Router r) {
        r.get("/caja", ROLES, controlador::pantalla);
    }

    @Override
    public List<ItemMenu> menu() { return List.of(new ItemMenu("Cierre de caja", "/caja", ROLES, 20)); }
}
