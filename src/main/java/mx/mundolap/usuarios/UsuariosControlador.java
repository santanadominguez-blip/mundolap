package mx.mundolap.usuarios;

import java.util.HashMap;
import mx.mundolap.compartido.Vistas;
import mx.mundolap.compartido.web.Contexto;

/** P-14 Administración de usuarios. FASE ACTUAL: solo muestra la pantalla, sin lógica de negocio. */
public final class UsuariosControlador {
    public void pantalla(Contexto c) throws Exception {
        c.html(c.pagina("Administración de usuarios", Vistas.render("usuarios/pantalla", new HashMap<>())));
    }
}
