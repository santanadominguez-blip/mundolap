package mx.mundolap.equipos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mx.mundolap.compartido.bd.ConexionBD;
import mx.mundolap.compartido.bd.ErrorBD;
import mx.mundolap.compartido.bd.Secuencias;

/** Equipos guardados en la tabla equipo de PostgreSQL. */
public final class EquipoRepositorioJdbc implements EquipoRepositorio {
    private static final String COLUMNAS = "id_equipo, id_cliente, tipo_dispositivo, marca, modelo, numero_serie";

    @Override
    public List<Equipo> listarPorCliente(String idCliente) {
        String sql = "SELECT " + COLUMNAS + " FROM equipo WHERE id_cliente = ? ORDER BY id_equipo";
        try (Connection cn = ConexionBD.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                List<Equipo> lista = new ArrayList<>();
                while (rs.next()) lista.add(leer(rs));
                return lista;
            }
        } catch (SQLException e) {
            throw new ErrorBD("listar los equipos", e);
        }
    }

    @Override
    public Optional<Equipo> buscarPorId(String idEquipo) {
        return buscarUno("SELECT " + COLUMNAS + " FROM equipo WHERE id_equipo = ?", idEquipo);
    }

    @Override
    public Optional<Equipo> buscarPorSerie(String numeroSerie) {
        return buscarUno("SELECT " + COLUMNAS + " FROM equipo WHERE numero_serie <> '' "
                + "AND lower(numero_serie) = lower(?) LIMIT 1", numeroSerie);
    }

    @Override
    public void guardar(Equipo e) {
        String sql = "INSERT INTO equipo (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT (id_equipo) DO UPDATE SET id_cliente = EXCLUDED.id_cliente, "
                + "tipo_dispositivo = EXCLUDED.tipo_dispositivo, marca = EXCLUDED.marca, "
                + "modelo = EXCLUDED.modelo, numero_serie = EXCLUDED.numero_serie";
        try (Connection cn = ConexionBD.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.idEquipo());
            ps.setString(2, e.idCliente());
            ps.setString(3, e.tipoDispositivo().name());
            ps.setString(4, e.marca());
            ps.setString(5, e.modelo());
            ps.setString(6, e.numeroSerie());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new ErrorBD("guardar el equipo", ex);
        }
    }

    @Override
    public String nuevoId() { return String.format("EQ-%03d", Secuencias.siguiente("equipo_seq")); }

    private Optional<Equipo> buscarUno(String sql, String valor) {
        try (Connection cn = ConexionBD.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(leer(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBD("buscar el equipo", e);
        }
    }

    private static Equipo leer(ResultSet rs) throws SQLException {
        return new Equipo(rs.getString(1), rs.getString(2), TipoDispositivo.desde(rs.getString(3)),
                rs.getString(4), rs.getString(5), rs.getString(6));
    }
}
