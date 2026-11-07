package mx.mundolap.acceso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import mx.mundolap.compartido.Rol;
import mx.mundolap.compartido.bd.ConexionBD;
import mx.mundolap.compartido.bd.ErrorBD;

/** Usuarios guardados en la tabla usuario de PostgreSQL. */
public final class UsuarioRepositorioJdbc implements UsuarioRepositorio {

    @Override
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        String sql = "SELECT id_usuario, nombre_completo, nombre_usuario, rol_asignado, credenciales_acceso "
                + "FROM usuario WHERE nombre_usuario = ?";
        try (Connection cn = ConexionBD.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Usuario(rs.getString(1), rs.getString(2), rs.getString(3),
                        Rol.valueOf(rs.getString(4)), rs.getString(5)));
            }
        } catch (SQLException e) {
            throw new ErrorBD("buscar el usuario", e);
        }
    }

    /** Inserta el usuario o actualiza sus datos si ya existe su id. */
    @Override
    public void guardar(Usuario u) {
        String sql = "INSERT INTO usuario (id_usuario, nombre_completo, nombre_usuario, rol_asignado, credenciales_acceso) "
                + "VALUES (?, ?, ?, ?, ?) ON CONFLICT (id_usuario) DO UPDATE SET "
                + "nombre_completo = EXCLUDED.nombre_completo, nombre_usuario = EXCLUDED.nombre_usuario, "
                + "rol_asignado = EXCLUDED.rol_asignado, credenciales_acceso = EXCLUDED.credenciales_acceso";
        try (Connection cn = ConexionBD.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.idUsuario());
            ps.setString(2, u.nombreCompleto());
            ps.setString(3, u.nombreUsuario());
            ps.setString(4, u.rolAsignado().name());
            ps.setString(5, u.credencialesAcceso());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorBD("guardar el usuario", e);
        }
    }

    @Override
    public boolean vacio() {
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT NOT EXISTS (SELECT 1 FROM usuario)");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException e) {
            throw new ErrorBD("consultar los usuarios", e);
        }
    }
}
