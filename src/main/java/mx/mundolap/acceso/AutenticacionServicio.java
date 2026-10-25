package mx.mundolap.acceso;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

/** Valida credenciales (RF-02, RF-04). */
public final class AutenticacionServicio {
    private final UsuarioRepositorio repositorio;

    public AutenticacionServicio(UsuarioRepositorio repositorio) { this.repositorio = repositorio; }

    public Optional<Usuario> autenticar(String nombreUsuario, String clave) {
        if (nombreUsuario == null || nombreUsuario.isBlank() || clave == null) return Optional.empty();
        return repositorio.buscarPorNombreUsuario(nombreUsuario.trim())
                .filter(u -> MessageDigest.isEqual(
                        u.credencialesAcceso().getBytes(StandardCharsets.UTF_8),
                        hash(clave).getBytes(StandardCharsets.UTF_8)));
    }

    /** SHA-256 para la demo. Al integrar con la BD real conviene un hash con sal (BCrypt/PBKDF2). */
    public static String hash(String clave) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(clave.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
