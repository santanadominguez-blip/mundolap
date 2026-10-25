package mx.mundolap.reportes;

import java.util.HashMap;
import mx.mundolap.compartido.Vistas;
import mx.mundolap.compartido.web.Contexto;

/** P-17 Configurar metas de productividad. FASE ACTUAL: solo muestra la pantalla, sin lógica de negocio. */
public final class MetasControlador {
    public void pantalla(Contexto c) throws Exception {
        c.html(c.pagina("Metas de productividad", Vistas.render("reportes/metas", new HashMap<>())));
    }
}
