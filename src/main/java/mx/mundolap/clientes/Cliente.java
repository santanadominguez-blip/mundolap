package mx.mundolap.clientes;

/** Entidad CLIENTE del diccionario de datos (PRY03). */
public record Cliente(String idCliente, String nombreCompleto, String telefonoContacto,
                      String correoElectronico, TipoCliente tipoCliente, String razonSocial, String rfc) {

    public static Cliente vacio() {
        return new Cliente("", "", "", "", TipoCliente.PARTICULAR, "", "");
    }

    public Cliente conId(String nuevoId) {
        return new Cliente(nuevoId, nombreCompleto, telefonoContacto, correoElectronico,
                tipoCliente, razonSocial, rfc);
    }
}
