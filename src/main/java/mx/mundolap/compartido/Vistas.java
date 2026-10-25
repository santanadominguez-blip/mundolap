package mx.mundolap.compartido;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Motor mínimo de plantillas. Las vistas son archivos .html en resources/vistas.
 *   {{clave}}   -> se reemplaza escapando el valor (seguro)
 *   {{{clave}}} -> se reemplaza tal cual (para HTML ya construido y escapado)
 */
public final class Vistas {
    private static final Pattern MARCA = Pattern.compile("\\{\\{\\{(\\w+)\\}\\}\\}|\\{\\{(\\w+)\\}\\}");

    private Vistas() { }

    public static String render(String plantilla, Map<String, String> valores) {
        String texto = cargar(plantilla);
        Matcher m = MARCA.matcher(texto);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            boolean crudo = m.group(1) != null;
            String clave = crudo ? m.group(1) : m.group(2);
            String valor = valores.getOrDefault(clave, "");
            m.appendReplacement(sb, Matcher.quoteReplacement(crudo ? valor : Html.escapar(valor)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /** Crea el mapa de valores a partir de pares clave, valor, clave, valor... */
    public static Map<String, String> datos(String... pares) {
        Map<String, String> mapa = new HashMap<>();
        for (int i = 0; i + 1 < pares.length; i += 2) mapa.put(pares[i], pares[i + 1]);
        return mapa;
    }

    private static String cargar(String nombre) {
        try (InputStream in = Vistas.class.getResourceAsStream("/vistas/" + nombre + ".html")) {
            if (in == null) throw new IllegalStateException("No existe la vista: " + nombre);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer la vista: " + nombre, e);
        }
    }
}
