package mx.mundolap.compartido.bd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Única clase que sabe cómo conectarse a PostgreSQL. Los repositorios JDBC (XRepositorioJdbc)
 * piden una conexión con {@link #obtener()} y la cierran con try-with-resources:
 *
 * <pre>
 *   try (Connection cn = ConexionBD.obtener();
 *        PreparedStatement ps = cn.prepareStatement("SELECT ...")) { ... }
 * </pre>
 *
 * Configuración (de mayor a menor prioridad):
 *   1. Variables de entorno: MUNDOLAP_BD_HOST, MUNDOLAP_BD_PUERTO, MUNDOLAP_BD_NOMBRE,
 *      MUNDOLAP_BD_USUARIO y MUNDOLAP_BD_CLAVE.
 *   2. Archivo src/main/resources/bd.properties.
 *   3. Valores por defecto de PostgreSQL local (localhost:5432, base "mundolap", usuario "postgres").
 */
public final class ConexionBD {
    private static final String ARCHIVO = "/bd.properties";
    private static final int SEGUNDOS_ESPERA = 5;
    private static final Properties CONFIG = cargarArchivo();

    private ConexionBD() { }

    /** Abre una conexión nueva. Quien la pide es responsable de cerrarla. */
    public static Connection obtener() throws SQLException {
        DriverManager.setLoginTimeout(SEGUNDOS_ESPERA);
        Properties credenciales = new Properties();
        credenciales.setProperty("user", valor("usuario", "postgres"));
        credenciales.setProperty("password", valor("clave", ""));
        credenciales.setProperty("ApplicationName", "MundoLap");
        return DriverManager.getConnection(url(), credenciales);
    }

    /** URL JDBC que se está usando (sin usuario ni contraseña, segura para mostrar en pantalla). */
    public static String url() {
        return "jdbc:postgresql://" + valor("host", "localhost") + ":" + valor("puerto", "5432")
                + "/" + valor("nombre", "mundolap");
    }

    /**
     * Intenta conectarse e imprime el resultado. Nunca lanza excepciones: si no hay base de datos,
     * el sistema sigue funcionando con los repositorios en memoria.
     */
    public static boolean probar() {
        try (Connection cn = obtener()) {
            System.out.println("[BD] Conectado a " + url() + " como " + valor("usuario", "postgres"));
            return cn.isValid(SEGUNDOS_ESPERA);
        } catch (SQLException e) {
            System.err.println("[BD] No se pudo conectar a " + url());
            System.err.println("[BD] " + describirError(e));
            return false;
        }
    }

    /** Traduce los errores más comunes de PostgreSQL a un mensaje que diga qué revisar. */
    public static String describirError(SQLException e) {
        String mensaje = e.getMessage() == null ? "" : e.getMessage();
        String estado = e.getSQLState() == null ? "" : e.getSQLState();
        if (mensaje.contains("No suitable driver")) {
            return "No se encontró el driver de PostgreSQL. Recarga el proyecto Maven (o ejecuta 'mvn compile') "
                    + "para que se descargue la dependencia del pom.xml.";
        }
        return switch (estado) {
            case "28P01", "28000" -> "Usuario o contraseña incorrectos. Revisa bd.usuario y bd.clave.";
            case "3D000" -> "La base de datos '" + valor("nombre", "mundolap")
                    + "' no existe. Revisa bd.nombre (debe coincidir con el nombre que ves en pgAdmin).";
            case "08001", "08006" -> "No hay respuesta del servidor. Verifica que PostgreSQL esté encendido y que "
                    + "bd.host y bd.puerto sean correctos. Detalle: " + mensaje;
            default -> "Error de base de datos (" + (estado.isEmpty() ? "sin código" : estado) + "): " + mensaje;
        };
    }

    // ---------- configuración ----------

    private static String valor(String clave, String porDefecto) {
        String entorno = System.getenv("MUNDOLAP_BD_" + clave.toUpperCase());
        if (entorno != null && !entorno.isBlank()) return entorno.trim();
        String archivo = CONFIG.getProperty("bd." + clave);
        if (archivo != null && !archivo.isBlank()) return archivo.trim();
        return porDefecto;
    }

    private static Properties cargarArchivo() {
        Properties p = new Properties();
        try (InputStream in = ConexionBD.class.getResourceAsStream(ARCHIVO)) {
            if (in != null) p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("[BD] No se pudo leer bd.properties: " + e.getMessage());
        }
        return p;
    }
}
