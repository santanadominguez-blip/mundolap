package mx.mundolap.ordenes;

import java.util.List;
import java.util.Set;
import mx.mundolap.clientes.ClienteServicio;
import mx.mundolap.compartido.ItemMenu;
import mx.mundolap.compartido.Modulo;
import mx.mundolap.compartido.Rol;
import mx.mundolap.compartido.web.Router;
import mx.mundolap.equipos.EquipoServicio;

/** Módulo C del PRY03: Órdenes de servicio (aquí: recepción y creación de la orden con folio). */
public final class OrdenesModulo implements Modulo {
    private static final Set<Rol> RECEPCION = Set.of(Rol.RECEPCIONISTA);
    private static final Set<Rol> TODOS = Set.of(Rol.RECEPCIONISTA, Rol.TECNICO, Rol.GERENTE);
    private final OrdenesControlador controlador;
    private final EntregaControlador entrega = new EntregaControlador();

    public OrdenesModulo(ClienteServicio clientes, EquipoServicio equipos, OrdenServicioServicio servicio) {
        this.controlador = new OrdenesControlador(clientes, equipos, servicio);
    }

    @Override public String nombre() { return "Órdenes de servicio"; }

    @Override
    public void registrarRutas(Router r) {
        r.get("/ordenes/nueva", RECEPCION, controlador::nueva);
        r.post("/ordenes/generar", RECEPCION, controlador::generar);
        r.get("/ordenes/ver", TODOS, controlador::ver);
        r.get("/ordenes/entrega", RECEPCION, entrega::pantalla);
    }

    @Override
    public List<ItemMenu> menu() { return List.of(new ItemMenu("Órdenes de servicio", "/ordenes/nueva", RECEPCION, 30)); }
}
