package mx.mundolap.ordenes;

public enum Prioridad {
    NORMAL("Normal"), ALTA("Alta");

    private final String etiqueta;

    Prioridad(String etiqueta) { this.etiqueta = etiqueta; }

    public String etiqueta() { return etiqueta; }

    public static Prioridad desde(String texto) {
        for (Prioridad p : values()) if (p.name().equalsIgnoreCase(texto)) return p;
        return NORMAL;
    }
}
