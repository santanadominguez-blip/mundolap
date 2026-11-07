package mx.mundolap.clientes;

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

/** Clientes guardados en la tabla cliente de PostgreSQL. */
public final class ClienteRepositorioJdbc implements ClienteRepositorio {
    private static final String COLUMNAS = "id_cliente, nombre_completo, telefono_contacto, correo_electronico, "
            + "tipo_cliente, razon_social, rfc";

    @Override
    public List<Cliente> listar() {
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT " + COLUMNAS + " FROM cliente ORDER BY id_cliente");
             ResultSet rs = ps.executeQuery()) {
            List<Cliente> lista = new ArrayList<>();
            while (rs.next()) lista.add(leer(rs));
            return lista;
        } catch (SQLException e) {
            throw new ErrorBD("listar los clientes", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(String idCliente) {
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT " + COLUMNAS + " FROM cliente WHERE id_cliente = ?")) {
            ps.setString(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(leer(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBD("buscar el cliente", e);
        }
    }

    /** Inserta el cliente o actualiza sus datos si ya existe su id (RF-10 alta, RF-12 actualización). */
    @Override
    public void guardar(Cliente c) {
        String sql = "INSERT INTO cliente (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT (id_cliente) DO UPDATE SET nombre_completo = EXCLUDED.nombre_completo, "
                + "telefono_contacto = EXCLUDED.telefono_contacto, correo_electronico = EXCLUDED.correo_electronico, "
                + "tipo_cliente = EXCLUDED.tipo_cliente, razon_social = EXCLUDED.razon_social, rfc = EXCLUDED.rfc";
        try (Connection cn = ConexionBD.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.idCliente());
            ps.setString(2, c.nombreCompleto());
            ps.setString(3, c.telefonoContacto());
            ps.setString(4, c.correoElectronico());
            ps.setString(5, c.tipoCliente().name());
            ps.setString(6, c.razonSocial());
            ps.setString(7, c.rfc());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorBD("guardar el cliente", e);
        }
    }

    @Override
    public String nuevoId() {
        return String.format("CLI-%03d", Secuencias.siguiente("cliente_seq"));
    }

    private static Cliente leer(ResultSet rs) throws SQLException {
        return new Cliente(rs.getString(1), rs.getString(2), rs.getString(3).trim(), rs.getString(4),
                TipoCliente.desde(rs.getString(5)), rs.getString(6), rs.getString(7));
    }
}
