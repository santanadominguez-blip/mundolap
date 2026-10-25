# Mundo Lap – Sistema de punto de venta y servicios de reparación (Equipo Verde)

Java puro (JDK 17 o superior). Sin frameworks ni servidores externos: el servidor web es el que trae el JDK.

## Abrir y ejecutar en NetBeans
1. File > Open Project > seleccionar la carpeta `mundolap` (NetBeans la reconoce como proyecto Maven).
2. Clic derecho en el proyecto > Run (o F6). La clase principal es `mx.mundolap.App`.
3. Abrir en el navegador: http://localhost:8080
   (Si el puerto está ocupado: clic derecho > Set Configuration / o pasar otro puerto como argumento, p. ej. `8090`).

Usuarios de ejemplo: `recepcion / recepcion123`, `tecnico / tecnico123`, `gerente / gerente123`.

## Cómo está organizado (para poder crecer sin reprogramar)
```
mx.mundolap
├── App.java            Arma el sistema: crea piezas, las conecta y registra los módulos.
├── compartido/         Lo común: roles, estados, menú, plantillas, utilidades.
│   └── web/            Servidor, rutas (Router), sesiones. Única parte que "sabe" de HTTP.
├── acceso/             P-01 Login, P-05 Panel
├── clientes/           P-06 Clientes
├── equipos/            P-07 Registrar equipo
├── ordenes/            P-08 Nueva orden (con folio)
└── demo/               Datos de ejemplo (se borra al conectar la BD)
src/main/resources/vistas/   Las pantallas (HTML) de cada módulo
src/main/resources/static/   CSS y JS
```
Cada módulo separa: **Controlador** (recibe la petición) → **Servicio** (reglas de negocio) → **Repositorio** (datos).
El servicio no usa HTTP ni SQL, por eso se puede reutilizar tal cual.

### Agregar un módulo nuevo (ej. Pagos)
1. Crear el paquete `pagos` con su Modelo, Repositorio, Servicio, Controlador y una clase `PagosModulo implements Modulo`.
2. Crear sus pantallas en `resources/vistas/pagos/`.
3. Agregar `new PagosModulo(...)` a la lista `modulos` en `App.java`. El menú se actualiza solo.

### Conectar la base de datos (cuando el equipo termine el modelo relacional)
Cada `XRepositorio` es una interfaz y hoy tiene una versión `XRepositorioMemoria`.
Se crea `XRepositorioJdbc implements XRepositorio` y se cambia **una línea** en `App.java`.
Controladores, servicios y vistas no cambian. Los nombres de los campos de los modelos coinciden con el diccionario de datos del PRY03.

## Pendientes conocidos
- `CostoDiagnosticoEjemplo`: tarifas de ejemplo; el PRY03 no define cómo se calcula el costo estimado.
- Faltan las pantallas P-02 a P-04 (portal del cliente) y P-09 a P-17.
