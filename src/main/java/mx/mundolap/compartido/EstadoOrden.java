package mx.mundolap.compartido;

/** Los seis estados de una orden de servicio (PRY03, RF-22). */
public enum EstadoOrden {
    RECIBIDO("Recibido"),
    PENDIENTE("Pendiente"),
    EN_REPARACION("En reparación"),
    ESPERANDO_PIEZAS("Esperando piezas"),
    TERMINADO("Terminado"),
    ENTREGADO("Entregado");

    private final String etiqueta;

    EstadoOrden(String etiqueta) { this.etiqueta = etiqueta; }

    public String etiqueta() { return etiqueta; }

    /** Una orden está activa mientras no se haya entregado el equipo. */
    public boolean activo() { return this != ENTREGADO; }
}
