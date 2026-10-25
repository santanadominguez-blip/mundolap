package mx.mundolap.tecnico;

import java.util.List;
import java.util.Set;
import mx.mundolap.compartido.ItemMenu;
import mx.mundolap.compartido.Modulo;
import mx.mundolap.compartido.Rol;
import mx.mundolap.compartido.web.Router;

/**
 * Módulo del técnico (P-11 Mis órdenes y P-12 Detalle de orden).
 * FASE ACTUAL: solo las vistas. Pendiente: órdenes asignadas al técnico, filtro y búsqueda (RF-18),
 * diagnóstico, cambio de estado, pruebas y piezas (RF-21, RF-22, RF-23, RF-25, RF-26, RF-34, RF-52)
 * y sus rutas POST.
 */
public final class TecnicoModulo implements Modulo {
    private static final Set<Rol> ROLES = Set.of(Rol.TECNICO);
    private final TecnicoControlador controlador = new TecnicoControlador();

    @Override public String nombre() { return "Técnico"; }

    @Override
    public void registrarRutas(Router r) {
        r.get("/mis-ordenes", ROLES, controlador::misOrdenes);
        r.get("/mis-ordenes/detalle", ROLES, controlador::detalle);
    }

    @Override
    public List<ItemMenu> menu() { return List.of(new ItemMenu("Mis órdenes", "/mis-ordenes", ROLES, 10)); }
}
