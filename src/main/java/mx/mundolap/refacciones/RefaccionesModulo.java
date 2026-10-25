package mx.mundolap.refacciones;

import java.util.List;
import java.util.Set;
import mx.mundolap.compartido.ItemMenu;
import mx.mundolap.compartido.Modulo;
import mx.mundolap.compartido.Rol;
import mx.mundolap.compartido.web.Router;

/**
 * Módulo de refacciones (P-13 Solicitar refacción).
 * FASE ACTUAL: solo la vista. Pendiente: catálogo con stock, proveedores, solicitudes y alertas
 * (RF-28 a RF-33, RF-35 a RF-37, RF-53) y sus rutas POST.
 */
public final class RefaccionesModulo implements Modulo {
    private static final Set<Rol> ROLES = Set.of(Rol.TECNICO);
    private final RefaccionesControlador controlador = new RefaccionesControlador();

    @Override public String nombre() { return "Refacciones"; }

    @Override
    public void registrarRutas(Router r) {
        r.get("/refacciones", ROLES, controlador::pantalla);
    }

    @Override
    public List<ItemMenu> menu() { return List.of(new ItemMenu("Refacciones", "/refacciones", ROLES, 20)); }
}
