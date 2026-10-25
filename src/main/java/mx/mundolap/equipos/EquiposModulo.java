package mx.mundolap.equipos;

import java.util.List;
import java.util.Set;
import mx.mundolap.clientes.ClienteServicio;
import mx.mundolap.compartido.ItemMenu;
import mx.mundolap.compartido.Modulo;
import mx.mundolap.compartido.Rol;
import mx.mundolap.compartido.web.Router;

/** Registro de equipos de los clientes (parte de Gestión de clientes en el PRY03). */
public final class EquiposModulo implements Modulo {
    private static final Set<Rol> ROLES = Set.of(Rol.RECEPCIONISTA);
    private final EquiposControlador controlador;

    public EquiposModulo(ClienteServicio clientes, EquipoServicio servicio) {
        this.controlador = new EquiposControlador(clientes, servicio);
    }

    @Override public String nombre() { return "Equipos"; }

    @Override
    public void registrarRutas(Router r) {
        r.get("/equipos", ROLES, controlador::pantalla);
        r.post("/equipos/guardar", ROLES, controlador::guardar);
    }

    @Override
    public List<ItemMenu> menu() { return List.of(new ItemMenu("Equipos", "/equipos", ROLES, 20)); }
}
