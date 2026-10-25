package mx.mundolap.pagos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import mx.mundolap.compartido.Vistas;
import mx.mundolap.compartido.web.Contexto;

/** P-09 Registrar pago o abono. FASE ACTUAL: solo muestra la pantalla, sin lógica de negocio. */
public final class PagosControlador {
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void pantalla(Contexto c) throws Exception {
        var v = Vistas.datos("folio", c.param("folio"),
                "fechaPago", LocalDateTime.now().format(FECHA_HORA));
        c.html(c.pagina("Pagos", Vistas.render("pagos/pantalla", v)));
    }
}
