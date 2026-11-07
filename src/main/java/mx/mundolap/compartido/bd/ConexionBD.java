package mx.mundolap.compartido.bd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Única clase que sabe cómo conectarse a PostgreSQL. Los repositorios JDBC piden una conexión con
 * {@link #obtener()} y la cierran con try-with-resources.
 *
 * Configuración (de mayor a menor prioridad):
 *   1. Propiedades de la JVM: -Dmundolap.bd.host, -Dmundolap.bd.clave, etc.
 *   2. Variables de entorno: MUNDOLAP_BD_HOST, MUNDOLAP_BD_PUERTO, MUNDOLAP_BD_NOMBRE,
 *      MUNDOLAP_BD_USUARIO, MUNDOLAP_BD_CLAVE y MUNDOLAP_BD_ESQUEMA.
 *   3. Archivo src/main/resources/bd.local.properties (opcional, ignorado por Git: aquí va la contraseña).
 *   4. Archivo src/main/resources/bd.properties.
 *   5. Valores por defecto: localhost:5432, base "Mundo_Lap", usuario "postgres", esquema "mundolap".
 *
 * Las tablas del sistema viven en su propio esquema ("mundolap") para no chocar con tablas que ya
 * existan en el esquema public de la misma base.
 */
public final class ConexionBD {
    private static final String ARCHIVO = "/bd.properties";
    private static final String ARCHIVO_LOCAL = "/bd.local.properties";
    private static final int SEGUNDOS_ESPERA = 5;
    /** Nombres de base y esquema permitidos: evita inyectar SQL al crearlos. */
    private static final Pattern IDENTIFICADOR = Pattern.compile("[A-Za-z_][A-Za-z0-9_]{0,62}");
    private static final Properties CONFIG = cargarArchivo();

    private ConexionBD() { }

    /** Abre una conexión nueva a la base del sistema, ya situada en su esquema. */
    public static Connection obtener() throws SQLException {
        return conectar(nombreBase(), true);
    }

    public static String nombreBase() { return valor("nombre", "Mundo_Lap"); }

    public static String esquema() { return valor("esquema", "mundolap"); }

    /** URL JDBC que se está usando (sin usuario ni contraseña, segura para mostrar en pantalla). */
    public static String url() { return url(nombreBase()); }

    /**
     * Prepara la base de datos para usarse: crea la base si no existe y verifica la conexión.
     * Nunca lanza excepciones; devuelve false (e imprime qué revisar) si no se pudo.
     */
    public static boolean preparar() {
        try {
            crearBaseSiFalta();
            try (Connection cn = obtener()) {
                if (!cn.isValid(SEGUNDOS_ESPERA)) return false;
                System.out.println("[BD] Conectado a " + url() + " como " + valor("usuario", "postgres")
                        + " (esquema " + esquema() + ")");
                return true;
            }
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
            return "No se encontró el driver de PostgreSQL. Recarga el proyecto Maven (clic derecho > Reload POM).";
        }
        return switch (estado) {
            case "28P01", "28000" -> "Usuario o contraseña incorrectos. Escribe tu contraseña de PostgreSQL en "
                    + "src/main/resources/bd.local.properties con la línea  bd.clave=tu_contraseña";
            case "3D000" -> "La base de datos '" + nombreBase() + "' no existe y no se pudo crear.";
            case "42501" -> "El usuario no tiene permiso para crear la base o el esquema. Usa el usuario postgres.";
            case "08001", "08004", "08006" -> "No hay respuesta del servidor. Verifica que PostgreSQL esté encendido y que "
                    + "bd.host y bd.puerto sean correctos. Detalle: " + mensaje;
            default -> "Error de base de datos (" + (estado.isEmpty() ? "sin código" : estado) + "): " + mensaje;
        };
    }

    // ---------- internos ----------

    private static Connection conectar(String base, boolean usarEsquema) throws SQLException {
        DriverManager.setLoginTimeout(SEGUNDOS_ESPERA);
        Properties p = new Properties();
        p.setProperty("user", valor("usuario", "postgres"));
        p.setProperty("password", valor("clave", ""));
        p.setProperty("ApplicationName", "MundoLap");
        if (usarEsquema) p.setProperty("currentSchema", identificador(esquema()));
        return DriverManager.getConnection(url(base), p);
    }

    private static String url(String base) {
        return "jdbc:postgresql://" + valor("host", "localhost") + ":" + valor("puerto", "5432") + "/" + base;
    }

    /** Si la base no existe, se conecta a la base "postgres" y la crea (solo con un nombre válido). */
    private static void crearBaseSiFalta() throws SQLException {
        try (Connection cn = conectar(nombreBase(), false)) {
            if (!cn.isValid(SEGUNDOS_ESPERA)) throw new SQLException("El servidor no respondió.", "08006");
            return;
        } catch (SQLException e) {
            if (!"3D000".equals(e.getSQLState())) throw e;
        }
        String base = identificador(nombreBase());
        try (Connection cn = conectar("postgres", false); Statement st = cn.createStatement()) {
            st.execute("CREATE DATABASE \"" + base + "\" ENCODING 'UTF8' TEMPLATE template0");
            System.out.println("[BD] Se creó la base de datos " + base);
        }
    }

    static String identificador(String nombre) throws SQLException {
        if (nombre == null || !IDENTIFICADOR.matcher(nombre).matches()) {
            throw new SQLException("Nombre no válido para base o esquema: '" + nombre
                    + "'. Use solo letras, números y guion bajo.", "42602");
        }
        return nombre;
    }

    private static String valor(String clave, String porDefecto) {
        String jvm = System.getProperty("mundolap.bd." + clave);
        if (jvm != null && !jvm.isBlank()) return jvm.trim();
        String entorno = System.getenv("MUNDOLAP_BD_" + clave.toUpperCase());
        if (entorno != null && !entorno.isBlank()) return entorno.trim();
        String archivo = CONFIG.getProperty("bd." + clave);
        if (archivo != null && !archivo.isBlank()) return archivo.trim();
        return porDefecto;
    }

    /** Lee bd.properties y, encima, bd.local.properties (copia personal ignorada por Git). */
    private static Properties cargarArchivo() {
        Properties p = new Properties();
        leer(p, ARCHIVO);
        leer(p, ARCHIVO_LOCAL);
        return p;
    }

    private static void leer(Properties destino, String recurso) {
        try (InputStream in = ConexionBD.class.getResourceAsStream(recurso)) {
            if (in != null) destino.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("[BD] No se pudo leer " + recurso + ": " + e.getMessage());
        }
    }
}
