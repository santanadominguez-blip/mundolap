package mx.mundolap.clientes;

import java.util.List;
import java.util.Set;
import mx.mundolap.compartido.ItemMenu;
import mx.mundolap.compartido.Modulo;
import mx.mundolap.compartido.Rol;
import mx.mundolap.compartido.web.Router;

/** Módulo B del PRY03: Gestión de clientes. */
public final class ClientesModulo implements Modulo {
    private static final Set<Rol> ROLES = Set.of(Rol.RECEPCIONISTA);
    private final ClientesControlador controlador;

    public ClientesModulo(ClienteServicio servicio) { this.controlador = new ClientesControlador(servicio); }

    @Override public String nombre() { return "Clientes"; }

    @Override
    public void registrarRutas(Router r) {
        r.get("/clientes", ROLES, controlador::pantalla);
        r.post("/clientes/guardar", ROLES, controlador::guardar);
    }

    @Override
    public List<ItemMenu> menu() { return List.of(new ItemMenu("Clientes", "/clientes", ROLES, 10)); }
}
