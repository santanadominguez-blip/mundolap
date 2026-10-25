package mx.mundolap.ordenes;

import mx.mundolap.compartido.Vistas;
import mx.mundolap.compartido.web.Contexto;

/** P-10 Entrega de equipo y comprobante. FASE ACTUAL: solo muestra la pantalla, sin lógica de negocio. */
public final class EntregaControlador {
    public void pantalla(Contexto c) throws Exception {
        var v = Vistas.datos("folio", c.param("folio"));
        c.html(c.pagina("Entrega de equipo", Vistas.render("ordenes/entrega", v)));
    }
}
