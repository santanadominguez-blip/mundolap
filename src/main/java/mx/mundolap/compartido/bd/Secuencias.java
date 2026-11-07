package mx.mundolap.compartido.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/** Obtiene el siguiente número de una secuencia de PostgreSQL (únicos aunque haya varios usuarios a la vez). */
public final class Secuencias {
    private static final Set<String> PERMITIDAS =
            Set.of("usuario_seq", "cliente_seq", "equipo_seq", "orden_servicio_seq");

    private Secuencias() { }

    public static long siguiente(String secuencia) {
        if (!PERMITIDAS.contains(secuencia)) throw new IllegalArgumentException("Secuencia desconocida: " + secuencia);
        try (Connection cn = ConexionBD.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT nextval('" + secuencia + "')");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new ErrorBD("generar el identificador", e);
        }
    }
}
