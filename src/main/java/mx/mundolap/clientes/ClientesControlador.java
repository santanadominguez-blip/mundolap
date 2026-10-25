package mx.mundolap.clientes;

import java.util.List;
import mx.mundolap.compartido.Html;
import mx.mundolap.compartido.Vistas;
import mx.mundolap.compartido.web.Contexto;

/** P-06 Clientes: buscar y registrar. */
public final class ClientesControlador {
    private final ClienteServicio servicio;

    public ClientesControlador(ClienteServicio servicio) { this.servicio = servicio; }

    public void pantalla(Contexto c) throws Exception {
        String sel = c.param("sel");
        Cliente formulario = sel.isEmpty() ? Cliente.vacio() : servicio.buscarPorId(sel).orElse(Cliente.vacio());
        String alerta = c.param("ok").equals("1") ? Html.alerta("exito", "Cliente guardado correctamente.") : "";
        c.html(c.pagina("Clientes", vista(c.param("q"), formulario, alerta, false)));
    }

    public void guardar(Contexto c) throws Exception {
        Cliente f = servicio.normalizar(new Cliente(c.param("idCliente"), c.param("nombreCompleto"),
                c.param("telefonoContacto"), c.param("correoElectronico"),
                TipoCliente.desde(c.param("tipoCliente")), c.param("razonSocial"), c.param("rfc")));

        List<String> errores = servicio.validar(f);
        if (!errores.isEmpty()) {
            c.html(c.pagina("Clientes", vista("", f, Html.alerta("error", String.join(" ", errores)), false)));
            return;
        }
        List<Cliente> repetidos = servicio.duplicados(f);
        if (!repetidos.isEmpty() && !c.param("confirmarDuplicado").equals("1")) {
            Cliente r = repetidos.get(0);
            String msg = "Ya existe un cliente con el mismo teléfono o correo: " + r.nombreCompleto()
                    + " (" + r.idCliente() + "). Revise antes de guardar.";
            c.html(c.pagina("Clientes", vista("", f, Html.alerta("advertencia", msg), true)));
            return;
        }
        Cliente guardado = servicio.guardar(f);
        c.redirigir("/clientes?sel=" + Html.url(guardado.idCliente()) + "&ok=1");
    }

    private String vista(String q, Cliente f, String alertaHtml, boolean pedirConfirmacion) {
        StringBuilder filas = new StringBuilder();
        for (Cliente x : servicio.buscar(q)) {
            String id = Html.url(x.idCliente());
            filas.append("<tr><td>").append(Html.escapar(x.idCliente())).append("</td><td>")
                    .append(Html.escapar(x.nombreCompleto())).append("</td><td>")
                    .append(Html.escapar(x.telefonoContacto())).append("</td><td>")
                    .append(Html.escapar(x.tipoCliente().etiqueta())).append("</td><td class=\"num\">")
                    .append(servicio.ordenesActivas(x.idCliente())).append("</td><td class=\"acciones\">")
                    .append("<a class=\"btn btn-chico\" href=\"/clientes?sel=").append(id)
                    .append("&q=").append(Html.url(q)).append("\">Seleccionar</a> ")
                    // Ajuste acordado con el cliente (PRY03 2.2.3): atajo para vincular un equipo.
                    .append("<a class=\"btn btn-chico btn-secundario\" href=\"/equipos?cliente=").append(id)
                    .append("\">Nuevo equipo</a></td></tr>");
        }
        if (filas.length() == 0) {
            filas.append("<tr><td colspan=\"6\" class=\"vacio\">No se encontraron clientes.</td></tr>");
        }

        boolean existente = !f.idCliente().isEmpty();
        StringBuilder tipos = new StringBuilder();
        for (TipoCliente t : TipoCliente.values()) {
            tipos.append(Html.opcion(t.name(), t.etiqueta(), t == f.tipoCliente()));
        }
        StringBuilder botones = new StringBuilder("<button class=\"btn btn-primario\" type=\"submit\">Guardar cliente</button> ");
        if (pedirConfirmacion) {
            botones.append("<button class=\"btn btn-advertencia\" type=\"submit\" name=\"confirmarDuplicado\" "
                    + "value=\"1\">Guardar de todos modos</button> ");
        }
        botones.append("<a class=\"btn btn-secundario\" href=\"/clientes\">Limpiar</a> ");
        botones.append(existente
                ? "<a class=\"btn\" href=\"/equipos?cliente=" + Html.url(f.idCliente()) + "\">Continuar a equipo</a>"
                : "<span class=\"btn btn-deshabilitado\" aria-disabled=\"true\">Continuar a equipo</span>");

        var v = Vistas.datos("q", q, "idCliente", f.idCliente(), "nombreCompleto", f.nombreCompleto(),
                "telefonoContacto", f.telefonoContacto(), "correoElectronico", f.correoElectronico(),
                "razonSocial", f.razonSocial(), "rfc", f.rfc(),
                "tituloFormulario", existente ? "Datos del cliente " + f.idCliente() : "Registrar cliente nuevo");
        v.put("filasHtml", filas.toString());
        v.put("alertaHtml", alertaHtml);
        v.put("tiposHtml", tipos.toString());
        v.put("fiscalAttr", f.tipoCliente() == TipoCliente.CORPORATIVO ? "" : "disabled");
        v.put("botonesHtml", botones.toString());
        return Vistas.render("clientes/pantalla", v);
    }
}
