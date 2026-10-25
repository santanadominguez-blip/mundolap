package mx.mundolap.refacciones;

import mx.mundolap.compartido.Html;
import mx.mundolap.compartido.Vistas;
import mx.mundolap.compartido.web.Contexto;

/** P-13 Solicitar refacción. FASE ACTUAL: solo muestra la pantalla, sin lógica de negocio. */
public final class RefaccionesControlador {
    public void pantalla(Contexto c) throws Exception {
        String folio = c.param("folio");
        var v = Vistas.datos("folioTxt", folio.isEmpty() ? "—" : folio, "folioUrl", Html.url(folio));
        c.html(c.pagina("Solicitar refacción", Vistas.render("refacciones/pantalla", v)));
    }
}
