package mx.mundolap.compartido.bd;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Crea (si no existen) el esquema y las tablas del sistema ejecutando resources/bd/esquema.sql.
 * Todo se hace en una transacción: si una sentencia falla, no queda nada a medias.
 */
public final class EsquemaBD {
    private static final String SCRIPT = "/bd/esquema.sql";

    private EsquemaBD() { }

    public static void crear() throws SQLException {
        String esquema = ConexionBD.identificador(ConexionBD.esquema());
        try (Connection cn = ConexionBD.obtener()) {
            cn.setAutoCommit(false);
            try (Statement st = cn.createStatement()) {
                st.execute("CREATE SCHEMA IF NOT EXISTS \"" + esquema + "\"");
                st.execute("SET LOCAL search_path TO \"" + esquema + "\"");
                for (String sentencia : sentencias()) st.execute(sentencia);
                cn.commit();
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    /** Sentencias del script, sin líneas de comentario, separadas por punto y coma. */
    static List<String> sentencias() {
        StringBuilder sinComentarios = new StringBuilder();
        for (String linea : leerScript().split("\\R")) {
            if (!linea.trim().startsWith("--")) sinComentarios.append(linea).append('\n');
        }
        List<String> lista = new ArrayList<>();
        for (String s : sinComentarios.toString().split(";")) {
            if (!s.isBlank()) lista.add(s.trim());
        }
        return lista;
    }

    private static String leerScript() {
        try (InputStream in = EsquemaBD.class.getResourceAsStream(SCRIPT)) {
            if (in == null) throw new IllegalStateException("No se encontró el script " + SCRIPT);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + SCRIPT, e);
        }
    }
}
