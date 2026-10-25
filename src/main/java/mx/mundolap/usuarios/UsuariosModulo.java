package mx.mundolap.usuarios;

import java.util.List;
import java.util.Set;
import mx.mundolap.compartido.ItemMenu;
import mx.mundolap.compartido.Modulo;
import mx.mundolap.compartido.Rol;
import mx.mundolap.compartido.web.Router;

/**
 * Módulo de administración de usuarios (P-14).
 * FASE ACTUAL: solo la vista. Pendiente: lista de cuentas, alta, validaciones y asignación de rol
 * (RF-01, RF-03, RF-07) y sus rutas POST. Reutilizará acceso.UsuarioRepositorio.
 */
public final class UsuariosModulo implements Modulo {
    private static final Set<Rol> ROLES = Set.of(Rol.GERENTE);
    private final UsuariosControlador controlador = new UsuariosControlador();

    @Override public String nombre() { return "Usuarios"; }

    @Override
    public void registrarRutas(Router r) {
        r.get("/usuarios", ROLES, controlador::pantalla);
    }

    @Override
    public List<ItemMenu> menu() { return List.of(new ItemMenu("Usuarios", "/usuarios", ROLES, 40)); }
}
