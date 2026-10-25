package mx.mundolap.compartido.web;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.mundolap.compartido.Html;
import mx.mundolap.compartido.ItemMenu;
import mx.mundolap.compartido.UsuarioSesion;
import mx.mundolap.compartido.Vistas;

/** Petición actual: parámetros, usuario en sesión y métodos para responder. */
public final class Contexto {
    private final HttpExchange ex;
    private final Router router;
    private final Sesiones sesiones;
    private final Map<String, String> params = new HashMap<>();

    Contexto(HttpExchange ex, Router router, Sesiones sesiones) throws IOException {
        this.ex = ex;
        this.router = router;
        this.sesiones = sesiones;
        analizar(ex.getRequestURI().getRawQuery());
        if ("POST".equals(ex.getRequestMethod())) {
            analizar(new String(ex.getRequestBody().readNBytes(1_048_576), StandardCharsets.UTF_8));
        }
    }

    private void analizar(String texto) {
        if (texto == null || texto.isEmpty()) return;
        for (String par : texto.split("&")) {
            String[] kv = par.split("=", 2);
            String clave = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String valor = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            params.putIfAbsent(clave, valor);
        }
    }

    public String ruta() { return ex.getRequestURI().getPath(); }

    /** Valor de un parámetro (formulario o URL) sin espacios sobrantes; "" si no vino. */
    public String param(String nombre) {
        String v = params.get(nombre);
        return v == null ? "" : v.trim();
    }

    // ---------- sesión ----------
    public String token() {
        List<String> cabeceras = ex.getRequestHeaders().get("Cookie");
        if (cabeceras == null) return null;
        for (String cab : cabeceras) {
            for (String par : cab.split(";")) {
                String[] kv = par.trim().split("=", 2);
                if (kv.length == 2 && kv[0].equals("SESION")) return kv[1];
            }
        }
        return null;
    }

    public UsuarioSesion usuario() { return sesiones.obtener(token()).orElse(null); }

    public void fijarSesion(String token) {
        ex.getResponseHeaders().add("Set-Cookie", "SESION=" + token + "; Path=/; HttpOnly; SameSite=Lax");
    }

    public void borrarSesion() {
        ex.getResponseHeaders().add("Set-Cookie", "SESION=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax");
    }

    // ---------- respuestas ----------
    public void html(String contenido) throws IOException { responder(200, contenido); }

    public void responder(int estado, String contenido) throws IOException {
        byte[] bytes = contenido.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        ex.sendResponseHeaders(estado, bytes.length);
        ex.getResponseBody().write(bytes);
    }

    public void redirigir(String url) throws IOException {
        ex.getResponseHeaders().set("Location", url);
        ex.sendResponseHeaders(303, -1);
    }

    /** Envuelve el contenido en la estructura común: barra superior, menú lateral y título. */
    public String pagina(String titulo, String contenidoHtml) {
        UsuarioSesion u = usuario();
        String usuarioHtml = "";
        StringBuilder menu = new StringBuilder();
        if (u != null) {
            usuarioHtml = "<span class=\"usuario-datos\">" + Html.escapar(u.nombreCompleto()) + " · "
                    + Html.escapar(u.rol().etiqueta()) + "</span>"
                    + "<form method=\"post\" action=\"/logout\"><button class=\"btn btn-secundario btn-chico\" "
                    + "type=\"submit\">Cerrar sesión</button></form>";
            String actual = base(ruta());
            for (ItemMenu item : router.menuPara(u.rol())) {
                boolean activo = base(item.ruta()).equals(actual);
                menu.append("<a class=\"menu-item").append(activo ? " activo\" aria-current=\"page" : "")
                        .append("\" href=\"").append(Html.escapar(item.ruta())).append("\">")
                        .append(Html.escapar(item.etiqueta())).append("</a>");
            }
        }
        Map<String, String> v = Vistas.datos("titulo", titulo);
        v.put("usuarioHtml", usuarioHtml);
        v.put("menuHtml", menu.toString());
        v.put("contenidoHtml", contenidoHtml);
        return Vistas.render("layout", v);
    }

    private static String base(String ruta) {
        int i = ruta.indexOf('/', 1);
        return i < 0 ? ruta : ruta.substring(0, i);
    }
}
