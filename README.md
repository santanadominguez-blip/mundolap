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
├── acceso/             P-01 Inicio de sesión, P-05 Panel
├── clientes/           P-06 Clientes
├── equipos/            P-07 Registrar equipo
├── ordenes/            P-08 Nueva orden (con folio) y P-10 Entrega (solo vista)
├── pagos/              P-09 Pagos (solo vista)
├── tecnico/            P-11 Mis órdenes y P-12 Detalle de orden (solo vista)
├── refacciones/        P-13 Solicitar refacción (solo vista)
├── usuarios/           P-14 Administración de usuarios (solo vista)
├── caja/               P-15 Cierre de caja (solo vista)
├── reportes/           P-16 Reportes y P-17 Metas (solo vista)
└── demo/               Datos de ejemplo (se borra al conectar la BD)
src/main/resources/vistas/   Las pantallas (HTML) de cada módulo
src/main/resources/static/   CSS y JS
```
Cada módulo separa: **Controlador** (recibe la petición) → **Servicio** (reglas de negocio) → **Repositorio** (datos).
El servicio no usa HTTP ni SQL, por eso se puede reutilizar tal cual.

### Conectar la base de datos (cuando el equipo termine el modelo relacional)
Cada `XRepositorio` es una interfaz y hoy tiene una versión `XRepositorioMemoria`.
Se crea `XRepositorioJdbc implements XRepositorio` y se cambia **una línea** en `App.java`.
Controladores, servicios y vistas no cambian. Los nombres de los campos de los modelos coinciden con el diccionario de datos del PRY03.

Pendientes conocidos
Sin base de datos: los datos viven en memoria y se pierden al detener el programa. También las sesiones.
Datos de ejemplo: el paquete demo carga usuarios, clientes, equipos y órdenes de prueba; se elimina al conectar la BD.
Seguridad de demostración: contraseñas con SHA-256 sin sal, sin tokens CSRF.
CostoDiagnosticoEjemplo: tarifas de ejemplo (Celular y Tablet $250, Laptop $350, PC de escritorio y Otro $300); el PRY03 no define cómo se calcula el costo estimado.
Lógica de P-09 a P-17: depende del modelo relacional (entidades Pago, Refacción, Proveedor, solicitudes, metas, desglose de costo, técnico asignado, entre otras).
Portal del cliente (P-02 a P-04): no existe.
Panel (P-05): las alertas y el aviso de entrega son texto fijo.
Exportar a PDF (P-15 y P-16): pendiente de decidir cómo hacerlo sin librerías externas.
