package mx.mundolap.acceso;

import mx.mundolap.compartido.Rol;

/** Entidad USUARIO del diccionario de datos (PRY03). credencialesAcceso guarda el hash, nunca la contraseña. */
public record Usuario(String idUsuario, String nombreCompleto, String nombreUsuario,
                      Rol rolAsignado, String credencialesAcceso) { }
