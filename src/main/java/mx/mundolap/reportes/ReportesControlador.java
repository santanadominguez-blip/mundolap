package mx.mundolap.reportes;

import java.util.HashMap;
import mx.mundolap.compartido.Vistas;
import mx.mundolap.compartido.web.Contexto;

/** P-16 Reportes de ventas, inventario y productividad. FASE ACTUAL: solo muestra la pantalla, sin lógica de negocio. */
public final class ReportesControlador {
    public void pantalla(Contexto c) throws Exception {
        c.html(c.pagina("Reportes", Vistas.render("reportes/pantalla", new HashMap<>())));
    }
}
