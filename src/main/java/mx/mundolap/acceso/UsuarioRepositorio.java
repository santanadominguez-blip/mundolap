package mx.mundolap.acceso;

import java.util.Optional;

/** Acceso a datos de usuarios. Hoy hay una versión en memoria; después se hace una versión JDBC. */
public interface UsuarioRepositorio {
    Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario);

    void guardar(Usuario usuario);
}
