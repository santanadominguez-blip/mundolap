package mx.mundolap.compartido;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Utilidades para generar HTML seguro (evita XSS escapando todo dato del usuario). */
public final class Html {
    private Html() { }

    public static String escapar(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (char ch : s.toCharArray()) {
            switch (ch) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#39;");
                default -> sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static String url(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }

    public static String opcion(String valor, String etiqueta, boolean seleccionada) {
        return "<option value=\"" + escapar(valor) + "\"" + (seleccionada ? " selected" : "")
                + ">" + escapar(etiqueta) + "</option>";
    }

    public static String alerta(String tipo, String mensaje) {
        return "<div class=\"alerta alerta-" + tipo + "\" role=\"alert\">" + escapar(mensaje) + "</div>";
    }
}
