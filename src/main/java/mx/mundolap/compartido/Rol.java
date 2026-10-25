package mx.mundolap.compartido;

/** Roles del personal definidos en el PRY03 (RF-01). */
public enum Rol {
    RECEPCIONISTA("Recepcionista"),
    TECNICO("Técnico"),
    GERENTE("Gerente");

    private final String etiqueta;

    Rol(String etiqueta) { this.etiqueta = etiqueta; }

    public String etiqueta() { return etiqueta; }
}
