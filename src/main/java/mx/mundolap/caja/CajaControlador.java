package mx.mundolap.caja;

import java.util.HashMap;
import mx.mundolap.compartido.Vistas;
import mx.mundolap.compartido.web.Contexto;

/** P-15 Cierre de caja. FASE ACTUAL: solo muestra la pantalla, sin lógica de negocio. */
public final class CajaControlador {
    public void pantalla(Contexto c) throws Exception {
        c.html(c.pagina("Cierre de caja", Vistas.render("caja/pantalla", new HashMap<>())));
    }
}
