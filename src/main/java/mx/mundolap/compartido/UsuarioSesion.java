package mx.mundolap.compartido;

/** Datos mínimos del usuario autenticado que el resto de módulos necesita conocer. */
public record UsuarioSesion(String idUsuario, String nombreCompleto, Rol rol) { }
