package mx.mundolap.compartido.bd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import mx.mundolap.acceso.AutenticacionServicio;
import mx.mundolap.acceso.Usuario;
import mx.mundolap.acceso.UsuarioRepositorioJdbc;
import mx.mundolap.clientes.Cliente;
import mx.mundolap.clientes.ClienteRepositorioJdbc;
import mx.mundolap.clientes.ClienteServicio;
import mx.mundolap.clientes.TipoCliente;
import mx.mundolap.compartido.EstadoOrden;
import mx.mundolap.compartido.Rol;
import mx.mundolap.equipos.Equipo;
import mx.mundolap.equipos.EquipoRepositorioJdbc;
import mx.mundolap.equipos.EquipoServicio;
import mx.mundolap.equipos.TipoDispositivo;
import mx.mundolap.ordenes.CostoDiagnosticoEjemplo;
import mx.mundolap.ordenes.OrdenRepositorioJdbc;
import mx.mundolap.ordenes.OrdenServicio;
import mx.mundolap.ordenes.OrdenServicioServicio;
import mx.mundolap.ordenes.Prioridad;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de los repositorios contra un PostgreSQL real, en un esquema aparte ("mundolap_prueba")
 * que se crea y se borra en cada prueba, así nunca toca los datos del sistema.
 * Si PostgreSQL no está disponible o no está configurada la contraseña, estas pruebas se omiten.
 */
class RepositoriosJdbcTest {
    private static final String ESQUEMA_PRUEBA = "mundolap_prueba";

    private ClienteServicio clientes;
    private EquipoServicio equipos;
    private OrdenServicioServicio ordenes;

    @BeforeEach
    void preparar() throws SQLException {
        System.setProperty("mundolap.bd.esquema", ESQUEMA_PRUEBA);
        assumeTrue(ConexionBD.preparar(), "PostgreSQL no disponible: se omiten las pruebas JDBC");
        borrarEsquema();
        EsquemaBD.crear();
        clientes = new ClienteServicio(new ClienteRepositorioJdbc());
        equipos = new EquipoServicio(new EquipoRepositorioJdbc());
        ordenes = new OrdenServicioServicio(new OrdenRepositorioJdbc(), clientes, equipos, new CostoDiagnosticoEjemplo());
        clientes.usarRelacionOrdenes(ordenes);
        equipos.usarRelacionOrdenes(ordenes);
    }

    @AfterEach
    void limpiar() throws SQLException {
        if (clientes != null) borrarEsquema();
        System.clearProperty("mundolap.bd.esquema");
    }

    private static void borrarEsquema() throws SQLException {
        try (Connection cn = ConexionBD.obtener(); Statement st = cn.createStatement()) {
            st.execute("DROP SCHEMA IF EXISTS " + ESQUEMA_PRUEBA + " CASCADE");
        }
    }

    @Test
    void elFlujoClienteEquipoOrdenQuedaGuardadoEnLaBase() {
        Cliente ana = clientes.guardar(new Cliente("", "Ana", "2281112233", "ana@correo.com", TipoCliente.PARTICULAR, "", ""));
        Equipo cel = equipos.guardar(new Equipo("", ana.idCliente(), TipoDispositivo.CELULAR, "Samsung", "A15", "SN-1"));
        OrdenServicio o = ordenes.crear(ana.idCliente(), cel.idEquipo(), "No enciende", Prioridad.ALTA,
                LocalDate.now().plusDays(2));

        assertEquals("CLI-001", ana.idCliente());
        assertEquals("EQ-001", cel.idEquipo());
        assertEquals("OS-0001", o.folio());

        OrdenServicio leida = new OrdenRepositorioJdbc().buscarPorFolio("OS-0001").orElseThrow();
        assertEquals(EstadoOrden.RECIBIDO, leida.estadoActual());
        assertEquals(Prioridad.ALTA, leida.prioridad());
        assertEquals(new BigDecimal("250.00"), leida.costoEstimado());
        assertEquals(1, clientes.ordenesActivas(ana.idCliente()));
        assertEquals(1, clientes.buscar("os-0001").size());
    }

    @Test
    void actualizarUnClienteNoCreaOtroRegistro() {
        Cliente ana = clientes.guardar(new Cliente("", "Ana", "2281112233", "", TipoCliente.PARTICULAR, "", ""));
        clientes.guardar(new Cliente(ana.idCliente(), "Ana María", "2281112233", "", TipoCliente.PARTICULAR, "", ""));
        assertEquals(1, clientes.buscar("").size());
        assertEquals("Ana María", clientes.buscarPorId(ana.idCliente()).orElseThrow().nombreCompleto());
    }

    @Test
    void elNumeroDeSerieRepetidoSeDetectaSinImportarMayusculas() {
        Cliente ana = clientes.guardar(new Cliente("", "Ana", "2281112233", "", TipoCliente.PARTICULAR, "", ""));
        equipos.guardar(new Equipo("", ana.idCliente(), TipoDispositivo.LAPTOP, "HP", "15", "ABC-1"));
        Equipo repetido = new Equipo("", ana.idCliente(), TipoDispositivo.TABLET, "Lenovo", "M10", "abc-1");
        assertEquals(1, equipos.validar(repetido).size());
        assertThrows(ErrorBD.class, () -> equipos.guardar(repetido), "la base también lo rechaza");
    }

    @Test
    void laBaseRechazaUnaOrdenConEquipoDeOtroCliente() {
        Cliente ana = clientes.guardar(new Cliente("", "Ana", "2281112233", "", TipoCliente.PARTICULAR, "", ""));
        Cliente beto = clientes.guardar(new Cliente("", "Beto", "2284445566", "", TipoCliente.PARTICULAR, "", ""));
        Equipo deBeto = equipos.guardar(new Equipo("", beto.idCliente(), TipoDispositivo.LAPTOP, "HP", "15", ""));
        OrdenServicio invalida = new OrdenServicio("OS-9999", ana.idCliente(), deBeto.idEquipo(), "x", "",
                EstadoOrden.RECIBIDO, java.time.LocalDateTime.now(), java.time.LocalDateTime.now(),
                Prioridad.NORMAL, BigDecimal.TEN);
        assertThrows(ErrorBD.class, () -> new OrdenRepositorioJdbc().guardar(invalida));
    }

    @Test
    void losUsuariosSeGuardanConLaContrasenaCifrada() {
        UsuarioRepositorioJdbc repo = new UsuarioRepositorioJdbc();
        assertTrue(repo.vacio());
        repo.guardar(new Usuario("USR-001", "Rosa", "recepcion", Rol.RECEPCIONISTA,
                AutenticacionServicio.generarCredencial("recepcion123")));
        assertFalse(repo.vacio());
        assertTrue(new AutenticacionServicio(repo).autenticar("recepcion", "recepcion123").isPresent());
        assertFalse(repo.buscarPorNombreUsuario("recepcion").orElseThrow().credencialesAcceso().contains("recepcion123"));
    }
}
