package mx.mundolap.ordenes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import mx.mundolap.clientes.Cliente;
import mx.mundolap.clientes.ClienteServicio;
import mx.mundolap.compartido.EstadoOrden;
import mx.mundolap.compartido.Html;
import mx.mundolap.compartido.Rol;
import mx.mundolap.compartido.Vistas;
import mx.mundolap.compartido.web.Contexto;
import mx.mundolap.equipos.Equipo;
import mx.mundolap.equipos.EquipoServicio;

/** P-08 Nueva orden de servicio (y su vista de confirmación con el folio generado). */
public final class OrdenesControlador {
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ClienteServicio clientes;
    private final EquipoServicio equipos;
    private final OrdenServicioServicio servicio;

    public OrdenesControlador(ClienteServicio clientes, EquipoServicio equipos, OrdenServicioServicio servicio) {
        this.clientes = clientes;
        this.equipos = equipos;
        this.servicio = servicio;
    }

    public void nueva(Contexto c) throws Exception {
        Optional<Cliente> cliente = clientes.buscarPorId(c.param("cliente"));
        if (cliente.isEmpty()) {
            c.html(c.pagina("Nueva orden de servicio",
                    "<section class=\"tarjeta\"><p>Para crear una orden primero seleccione al cliente y su equipo.</p>"
                            + "<a class=\"btn btn-primario\" href=\"/clientes\">Ir a clientes</a></section>"));
            return;
        }
        c.html(c.pagina("Nueva orden de servicio",
                formulario(cliente.get(), c.param("equipo"), "", Prioridad.NORMAL, "", "")));
    }

    public void generar(Contexto c) throws Exception {
        String idCliente = c.param("idCliente");
        String idEquipo = c.param("idEquipo");
        String problema = c.param("problemaReportado");
        Prioridad prioridad = Prioridad.desde(c.param("prioridad"));
        LocalDate fecha = parseFecha(c.param("fechaEntregaEstimada"));

        List<String> errores = servicio.validar(idCliente, idEquipo, problema, fecha);
        if (!errores.isEmpty()) {
            Optional<Cliente> cliente = clientes.buscarPorId(idCliente);
            if (cliente.isEmpty()) {
                c.redirigir("/ordenes/nueva");
                return;
            }
            c.html(c.pagina("Nueva orden de servicio", formulario(cliente.get(), idEquipo, problema, prioridad,
                    c.param("fechaEntregaEstimada"), Html.alerta("error", String.join(" ", errores)))));
            return;
        }
        OrdenServicio orden = servicio.crear(idCliente, idEquipo, problema, prioridad, fecha);
        c.redirigir("/ordenes/ver?folio=" + Html.url(orden.folio()) + "&nueva=1");
    }

    public void ver(Contexto c) throws Exception {
        String folio = c.param("folio");
        Optional<OrdenServicio> orden = servicio.buscarPorFolio(folio);
        if (orden.isEmpty()) {
            c.responder(404, c.pagina("Orden de servicio", Html.alerta("advertencia",
                    "No se encontró ninguna orden con el folio \"" + folio + "\".")
                    + "<a class=\"btn btn-secundario\" href=\"/panel\">Volver al panel</a>"));
            return;
        }
        OrdenServicio o = orden.get();
        Cliente cliente = clientes.buscarPorId(o.idCliente()).orElse(Cliente.vacio());
        Equipo equipo = equipos.buscarPorId(o.idEquipo()).orElse(null);
        String equipoTxt = equipo == null ? "" : equipo.tipoDispositivo().etiqueta() + " " + equipo.marca() + " " + equipo.modelo();

        boolean recepcion = c.usuario().rol() == Rol.RECEPCIONISTA;
        var v = Vistas.datos("folio", o.folio(), "estado", o.estadoActual().etiqueta(),
                "cliente", cliente.nombreCompleto() + " · " + cliente.telefonoContacto(), "equipo", equipoTxt,
                "problema", o.problemaReportado(), "prioridad", o.prioridad().etiqueta(),
                "recepcion", o.fechaRecepcion().format(FECHA_HORA),
                "entrega", o.fechaEntregaEstimada().format(FECHA), "costo", dinero(o.costoEstimado()));
        v.put("alertaHtml", c.param("nueva").equals("1")
                ? Html.alerta("exito", "Orden generada correctamente. Entregue la ficha con el folio al cliente.") : "");
        v.put("claseEstado", "estado-" + o.estadoActual().name().toLowerCase(Locale.ROOT));
        v.put("accionesHtml", "<button class=\"btn btn-primario\" type=\"button\" onclick=\"window.print()\">Imprimir ficha</button> "
                + (recepcion ? "<a class=\"btn\" href=\"/ordenes/nueva\">Nueva orden</a> " : "")
                + (recepcion && o.estadoActual() == EstadoOrden.TERMINADO
                        ? "<a class=\"btn\" href=\"/ordenes/entrega?folio=" + Html.url(o.folio()) + "\">Entregar equipo</a> " : "")
                + "<a class=\"btn btn-secundario\" href=\"/panel\">Ir al panel</a>");
        c.html(c.pagina("Orden " + o.folio(), Vistas.render("ordenes/detalle", v)));
    }

    private String formulario(Cliente cliente, String equipoSel, String problema, Prioridad prioridad,
                              String fecha, String alertaHtml) {
        List<Equipo> lista = equipos.deCliente(cliente.idCliente());
        if (lista.isEmpty()) {
            return "<section class=\"tarjeta\"><p>Este cliente aún no tiene equipos registrados.</p>"
                    + "<a class=\"btn btn-primario\" href=\"/equipos?cliente=" + Html.url(cliente.idCliente())
                    + "\">Registrar equipo</a></section>";
        }
        StringBuilder opciones = new StringBuilder();
        String costoInicial = "";
        for (int i = 0; i < lista.size(); i++) {
            Equipo e = lista.get(i);
            boolean elegido = e.idEquipo().equals(equipoSel) || (equipoSel.isEmpty() && i == 0);
            String costo = dinero(servicio.costoPara(e));
            if (elegido) costoInicial = costo;
            opciones.append("<option value=\"").append(Html.escapar(e.idEquipo())).append("\" data-costo=\"")
                    .append(Html.escapar(costo)).append("\"").append(elegido ? " selected" : "").append(">")
                    .append(Html.escapar(e.tipoDispositivo().etiqueta() + " · " + e.marca() + " " + e.modelo()))
                    .append("</option>");
        }
        StringBuilder prioridades = new StringBuilder();
        for (Prioridad p : Prioridad.values()) prioridades.append(Html.opcion(p.name(), p.etiqueta(), p == prioridad));

        var v = Vistas.datos("idCliente", cliente.idCliente(),
                "clienteNombre", cliente.nombreCompleto() + " · " + cliente.telefonoContacto(),
                "problema", problema, "fechaEntrega", fecha, "hoy", LocalDate.now().toString(),
                "fechaRecepcion", java.time.LocalDateTime.now().format(FECHA_HORA), "costo", costoInicial);
        v.put("alertaHtml", alertaHtml);
        v.put("equiposHtml", opciones.toString());
        v.put("prioridadesHtml", prioridades.toString());
        return Vistas.render("ordenes/nueva", v);
    }

    private static LocalDate parseFecha(String texto) {
        try {
            return texto.isEmpty() ? null : LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static String dinero(BigDecimal valor) {
        return String.format(Locale.forLanguageTag("es-MX"), "$%,.2f", valor);
    }
}
