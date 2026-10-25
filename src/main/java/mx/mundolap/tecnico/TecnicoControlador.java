package mx.mundolap.tecnico;

import mx.mundolap.compartido.Html;
import mx.mundolap.compartido.Vistas;
import mx.mundolap.compartido.web.Contexto;

/** P-11 Mis órdenes y P-12 Detalle de orden. FASE ACTUAL: solo muestran la pantalla, sin lógica de negocio. */
public final class TecnicoControlador {

    public void misOrdenes(Contexto c) throws Exception {
        var v = Vistas.datos("folio", c.param("folio"));
        c.html(c.pagina("Mis órdenes", Vistas.render("tecnico/mis-ordenes", v)));
    }

    public void detalle(Contexto c) throws Exception {
        String folio = c.param("folio");
        var v = Vistas.datos("folioTxt", folio.isEmpty() ? "—" : folio, "folioUrl", Html.url(folio));
        c.html(c.pagina("Detalle de orden", Vistas.render("tecnico/detalle", v)));
    }
}
