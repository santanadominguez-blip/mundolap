package mx.mundolap.ordenes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mx.mundolap.compartido.EstadoOrden;
import mx.mundolap.compartido.bd.ConexionBD;
import mx.mundolap.compartido.bd.ErrorBD;
import mx.mundolap.compartido.bd.Secuencias;

/** Órdenes de servicio guardadas en la tabla orden_servicio de PostgreSQL. */
public final class OrdenRepositorioJdbc implements OrdenRepositorio {
    private static final String COLUMNAS = "folio, id_cliente, id_equipo, problema_reportado, diagnostico_tecnico, "
            + "estado_actual, fecha_recepcion, fecha_entrega_estimada, prioridad, costo_estimado";

    @Override
    public List<OrdenServicio> listar() {
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT " + COLUMNAS + " FROM orden_servicio ORDER BY folio");
             ResultSet rs = ps.executeQuery()) {
            List<OrdenServicio> lista = new ArrayList<>();
            while (rs.next()) lista.add(leer(rs));
            return lista;
        } catch (SQLException e) {
            throw new ErrorBD("listar las órdenes", e);
        }
    }

    @Override
    public Optional<OrdenServicio> buscarPorFolio(String folio) {
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT " + COLUMNAS + " FROM orden_servicio WHERE folio = ?")) {
            ps.setString(1, folio);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(leer(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBD("buscar la orden", e);
        }
    }

    @Override
    public void guardar(OrdenServicio o) {
        String sql = "INSERT INTO orden_servicio (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT (folio) DO UPDATE SET id_cliente = EXCLUDED.id_cliente, id_equipo = EXCLUDED.id_equipo, "
                + "problema_reportado = EXCLUDED.problema_reportado, diagnostico_tecnico = EXCLUDED.diagnostico_tecnico, "
                + "estado_actual = EXCLUDED.estado_actual, fecha_recepcion = EXCLUDED.fecha_recepcion, "
                + "fecha_entrega_estimada = EXCLUDED.fecha_entrega_estimada, prioridad = EXCLUDED.prioridad, "
                + "costo_estimado = EXCLUDED.costo_estimado";
        try (Connection cn = ConexionBD.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, o.folio());
            ps.setString(2, o.idCliente());
            ps.setString(3, o.idEquipo());
            ps.setString(4, o.problemaReportado());
            ps.setString(5, o.diagnosticoTecnico());
            ps.setString(6, o.estadoActual().name());
            ps.setTimestamp(7, Timestamp.valueOf(o.fechaRecepcion()));
            ps.setTimestamp(8, Timestamp.valueOf(o.fechaEntregaEstimada()));
            ps.setString(9, o.prioridad().name());
            ps.setBigDecimal(10, o.costoEstimado());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorBD("guardar la orden", e);
        }
    }

    @Override
    public String nuevoFolio() { return String.format("OS-%04d", Secuencias.siguiente("orden_servicio_seq")); }

    private static OrdenServicio leer(ResultSet rs) throws SQLException {
        return new OrdenServicio(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                EstadoOrden.valueOf(rs.getString(6)), rs.getTimestamp(7).toLocalDateTime(),
                rs.getTimestamp(8).toLocalDateTime(), Prioridad.desde(rs.getString(9)), rs.getBigDecimal(10));
    }
}
