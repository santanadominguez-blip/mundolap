package mx.mundolap.equipos;

public enum TipoDispositivo {
    CELULAR("Celular"), LAPTOP("Laptop"), PC_ESCRITORIO("Computadora de escritorio"),
    TABLET("Tablet"), OTRO("Otro");

    private final String etiqueta;

    TipoDispositivo(String etiqueta) { this.etiqueta = etiqueta; }

    public String etiqueta() { return etiqueta; }

    /** Devuelve null si el texto no corresponde a un tipo válido. */
    public static TipoDispositivo desde(String texto) {
        for (TipoDispositivo t : values()) if (t.name().equalsIgnoreCase(texto)) return t;
        return null;
    }
}
