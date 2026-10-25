package mx.mundolap.compartido;

import java.util.Set;

/** Opción del menú lateral. Solo se muestra a los roles indicados. */
public record ItemMenu(String etiqueta, String ruta, Set<Rol> roles, int orden) { }
