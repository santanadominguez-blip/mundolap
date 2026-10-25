package mx.mundolap.clientes;

public enum TipoCliente {
    PARTICULAR("Particular"), CORPORATIVO("Corporativo");

    private final String etiqueta;

    TipoCliente(String etiqueta) { this.etiqueta = etiqueta; }

    public String etiqueta() { return etiqueta; }

    public static TipoCliente desde(String texto) {
        for (TipoCliente t : values()) if (t.name().equalsIgnoreCase(texto)) return t;
        return PARTICULAR;
    }
}
