package mx.mundolap.acceso;

import java.util.List;
import java.util.Set;
import mx.mundolap.compartido.ItemMenu;
import mx.mundolap.compartido.Modulo;
import mx.mundolap.compartido.RelacionOrdenes;
import mx.mundolap.compartido.Rol;
import mx.mundolap.compartido.web.Router;
import mx.mundolap.compartido.web.Sesiones;

/** Módulo A del PRY03: Acceso y control de usuarios (login, cierre de sesión y panel). */
public final class AccesoModulo implements Modulo {
    private final AccesoControlador controlador;

    public AccesoModulo(AutenticacionServicio autenticacion, Sesiones sesiones, RelacionOrdenes ordenes) {
        this.controlador = new AccesoControlador(autenticacion, sesiones, ordenes);
    }

    @Override public String nombre() { return "Acceso"; }

    @Override
    public void registrarRutas(Router r) {
        r.get("/", controlador::inicio);
        r.get("/login", controlador::formularioLogin);
        r.post("/login", controlador::iniciarSesion);
        r.post("/logout", controlador::cerrarSesion);
        r.get("/panel", Set.of(Rol.RECEPCIONISTA, Rol.TECNICO, Rol.GERENTE), controlador::panel);
    }

    @Override
    public List<ItemMenu> menu() {
        return List.of(new ItemMenu("Inicio", "/panel", Set.of(Rol.RECEPCIONISTA, Rol.TECNICO, Rol.GERENTE), 0));
    }
}
