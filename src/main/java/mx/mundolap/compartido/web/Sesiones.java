package mx.mundolap.compartido.web;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import mx.mundolap.compartido.UsuarioSesion;

/** Sesiones de usuario en memoria (se pierden al reiniciar el servidor). */
public final class Sesiones {
    private final Map<String, UsuarioSesion> activas = new ConcurrentHashMap<>();
    private final SecureRandom azar = new SecureRandom();

    public String iniciar(UsuarioSesion usuario) {
        byte[] bytes = new byte[24];
        azar.nextBytes(bytes);
        String token = HexFormat.of().formatHex(bytes);
        activas.put(token, usuario);
        return token;
    }

    public Optional<UsuarioSesion> obtener(String token) {
        return token == null ? Optional.empty() : Optional.ofNullable(activas.get(token));
    }

    public void cerrar(String token) {
        if (token != null) activas.remove(token);
    }
}
