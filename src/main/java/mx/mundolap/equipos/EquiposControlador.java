package mx.mundolap.equipos;

import java.util.List;
import java.util.Optional;
import mx.mundolap.clientes.Cliente;
import mx.mundolap.clientes.ClienteServicio;
import mx.mundolap.compartido.Html;
import mx.mundolap.compartido.Vistas;
import mx.mundolap.compartido.web.Contexto;

/** P-07 Registrar equipo. */
public final class EquiposControlador {
    private final ClienteServicio clientes;
    private final EquipoServicio servicio;

    public EquiposControlador(ClienteServicio clientes, EquipoServicio servicio) {
        this.clientes = clientes;
        this.servicio = servicio;
    }

    public void pantalla(Contexto c) throws Exception {
        Optional<Cliente> cliente = clientes.buscarPorId(c.param("cliente"));
        if (cliente.isEmpty()) {
            c.html(c.pagina("Registrar equipo", sinCliente()));
            return;
        }
        String alerta = c.param("ok").equals("1") ? Html.alerta("exito", "Equipo guardado y vinculado al cliente.") : "";
        c.html(c.pagina("Registrar equipo", vista(cliente.get(), c.param("sel"),
                new Equipo("", cliente.get().idCliente(), TipoDispositivo.CELULAR, "", "", ""), alerta)));
    }

    public void guardar(Contexto c) throws Exception {
        Optional<Cliente> cliente = clientes.buscarPorId(c.param("idCliente"));
        if (cliente.isEmpty()) {
            c.html(c.pagina("Registrar equipo", sinCliente()));
            return;
        }
        Equipo e = servicio.normalizar(new Equipo("", cliente.get().idCliente(),
                TipoDispositivo.desde(c.param("tipoDispositivo")), c.param("marca"),
                c.param("modelo"), c.param("numeroSerie")));
        List<String> errores = servicio.validar(e);
        if (!errores.isEmpty()) {
            c.html(c.pagina("Registrar equipo", vista(cliente.get(), "", e, Html.alerta("error", String.join(" ", errores)))));
            return;
        }
        Equipo guardado = servicio.guardar(e);
        c.redirigir("/equipos?cliente=" + Html.url(guardado.idCliente()) + "&sel=" + Html.url(guardado.idEquipo()) + "&ok=1");
    }

    private String sinCliente() {
        return "<section class=\"tarjeta\"><p>Para registrar un equipo primero seleccione al cliente.</p>"
                + "<a class=\"btn btn-primario\" href=\"/clientes\">Buscar cliente</a></section>";
    }

    private String vista(Cliente cliente, String seleccionado, Equipo f, String alertaHtml) {
        StringBuilder tipos = new StringBuilder();
        for (TipoDispositivo t : TipoDispositivo.values()) {
            tipos.append(Html.opcion(t.name(), t.etiqueta(), t == f.tipoDispositivo()));
        }
        StringBuilder filas = new StringBuilder();
        for (Equipo e : servicio.deCliente(cliente.idCliente())) {
            filas.append("<tr").append(e.idEquipo().equals(seleccionado) ? " class=\"resaltada\"" : "").append("><td>")
                    .append(Html.escapar(e.idEquipo())).append("</td><td>")
                    .append(Html.escapar(e.tipoDispositivo().etiqueta())).append("</td><td>")
                    .append(Html.escapar(e.marca())).append("</td><td>")
                    .append(Html.escapar(e.modelo())).append("</td><td>")
                    .append(Html.escapar(e.numeroSerie())).append("</td><td class=\"num\">")
                    .append(servicio.totalOrdenes(e.idEquipo())).append("</td><td class=\"acciones\">")
                    .append("<a class=\"btn btn-chico\" href=\"/ordenes/nueva?cliente=").append(Html.url(cliente.idCliente()))
                    .append("&equipo=").append(Html.url(e.idEquipo())).append("\">Crear orden</a></td></tr>");
        }
        if (filas.length() == 0) {
            filas.append("<tr><td colspan=\"7\" class=\"vacio\">Este cliente aún no tiene equipos registrados.</td></tr>");
        }
        String crearOrden = seleccionado.isEmpty()
                ? "<span class=\"btn btn-deshabilitado\" aria-disabled=\"true\">Crear orden de servicio</span>"
                : "<a class=\"btn\" href=\"/ordenes/nueva?cliente=" + Html.url(cliente.idCliente())
                        + "&equipo=" + Html.url(seleccionado) + "\">Crear orden de servicio</a>";

        var v = Vistas.datos("idCliente", cliente.idCliente(),
                "clienteNombre", cliente.nombreCompleto() + " · " + cliente.telefonoContacto(),
                "marca", f.marca(), "modelo", f.modelo(), "numeroSerie", f.numeroSerie());
        v.put("alertaHtml", alertaHtml);
        v.put("tiposHtml", tipos.toString());
        v.put("filasHtml", filas.toString());
        v.put("crearOrdenHtml", crearOrden);
        return Vistas.render("equipos/pantalla", v);
    }
}
