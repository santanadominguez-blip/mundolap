package mx.mundolap.compartido;

import java.util.List;
import mx.mundolap.compartido.web.Router;

/**
 * Contrato que cumple cada módulo del sistema (clientes, equipos, órdenes, pagos...).
 * Para agregar un módulo nuevo: implementar esta interfaz y añadirlo a la lista de App.java.
 */
public interface Modulo {
    String nombre();

    /** Registra las rutas (URL) que atiende el módulo. */
    void registrarRutas(Router router);

    /** Opciones que el módulo aporta al menú lateral. */
    List<ItemMenu> menu();
}
