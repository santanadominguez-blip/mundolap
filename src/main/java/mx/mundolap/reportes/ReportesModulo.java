package mx.mundolap.reportes;

import java.util.List;
import java.util.Set;
import mx.mundolap.compartido.ItemMenu;
import mx.mundolap.compartido.Modulo;
import mx.mundolap.compartido.Rol;
import mx.mundolap.compartido.web.Router;

/**
 * Módulo de reportes y metas (P-16 Reportes y P-17 Metas de productividad).
 * FASE ACTUAL: solo las vistas. Pendiente: consultas de ventas, inventario y productividad, exportación a PDF
 * y guardado de metas (RF-57, RF-58, RF-59, RF-62, RF-63) y sus rutas POST.
 */
public final class ReportesModulo implements Modulo {
    private static final Set<Rol> ROLES = Set.of(Rol.GERENTE);
    private final ReportesControlador reportes = new ReportesControlador();
    private final MetasControlador metas = new MetasControlador();

    @Override public String nombre() { return "Reportes"; }

    @Override
    public void registrarRutas(Router r) {
        r.get("/reportes", ROLES, reportes::pantalla);
        r.get("/metas", ROLES, metas::pantalla);
    }

    @Override
    public List<ItemMenu> menu() {
        return List.of(new ItemMenu("Reportes", "/reportes", ROLES, 10),
                new ItemMenu("Metas", "/metas", ROLES, 30));
    }
}
