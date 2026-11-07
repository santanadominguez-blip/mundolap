-- =====================================================================================
-- Mundo Lap - Esquema de la base de datos (PostgreSQL 13 o superior)
-- El sistema ejecuta este script al arrancar dentro de su esquema propio (por defecto "mundolap").
-- Es idempotente: se puede ejecutar varias veces sin perder datos (IF NOT EXISTS).
-- Para ejecutarlo a mano en pgAdmin (Query Tool) sobre la base Mundo_Lap, antes ejecute:
--     CREATE SCHEMA IF NOT EXISTS mundolap;  SET search_path TO mundolap;
-- =====================================================================================

-- Secuencias para los identificadores con formato (USR-001, CLI-001, EQ-001, OS-0001)
CREATE SEQUENCE IF NOT EXISTS usuario_seq START 1;
CREATE SEQUENCE IF NOT EXISTS cliente_seq START 1;
CREATE SEQUENCE IF NOT EXISTS equipo_seq START 1;
CREATE SEQUENCE IF NOT EXISTS orden_servicio_seq START 1;

-- -------------------------------------------------------------------------------------
-- USUARIO: personal del taller que inicia sesión (P-01). Rol según el control de acceso.
-- -------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario          VARCHAR(10)  NOT NULL,
    nombre_completo     VARCHAR(120) NOT NULL,
    nombre_usuario      VARCHAR(40)  NOT NULL,
    rol_asignado        VARCHAR(20)  NOT NULL,
    credenciales_acceso VARCHAR(200) NOT NULL,   -- hash PBKDF2 con sal, nunca la contraseña
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_nombre UNIQUE (nombre_usuario),
    CONSTRAINT ck_usuario_rol CHECK (rol_asignado IN ('RECEPCIONISTA', 'TECNICO', 'GERENTE')),
    CONSTRAINT ck_usuario_nombre CHECK (length(trim(nombre_completo)) > 0 AND length(trim(nombre_usuario)) > 0)
);

-- -------------------------------------------------------------------------------------
-- CLIENTE: persona o empresa que trae equipos al taller (P-06).
-- -------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente          VARCHAR(10)  NOT NULL,
    nombre_completo     VARCHAR(120) NOT NULL,
    telefono_contacto   CHAR(10)     NOT NULL,
    correo_electronico  VARCHAR(120) NOT NULL DEFAULT '',
    tipo_cliente        VARCHAR(15)  NOT NULL,
    razon_social        VARCHAR(150) NOT NULL DEFAULT '',
    rfc                 VARCHAR(13)  NOT NULL DEFAULT '',
    CONSTRAINT pk_cliente PRIMARY KEY (id_cliente),
    CONSTRAINT ck_cliente_tipo CHECK (tipo_cliente IN ('PARTICULAR', 'CORPORATIVO')),
    CONSTRAINT ck_cliente_telefono CHECK (telefono_contacto ~ '^[0-9]{10}$'),
    CONSTRAINT ck_cliente_nombre CHECK (length(trim(nombre_completo)) > 0),
    -- Los datos fiscales solo aplican a clientes corporativos
    CONSTRAINT ck_cliente_fiscal CHECK (tipo_cliente = 'CORPORATIVO' OR (razon_social = '' AND rfc = ''))
);

-- -------------------------------------------------------------------------------------
-- EQUIPO: dispositivo de un cliente (P-07). Relación CLIENTE 1 — * EQUIPO.
-- -------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS equipo (
    id_equipo           VARCHAR(10)  NOT NULL,
    id_cliente          VARCHAR(10)  NOT NULL,
    tipo_dispositivo    VARCHAR(20)  NOT NULL,
    marca               VARCHAR(60)  NOT NULL,
    modelo              VARCHAR(60)  NOT NULL,
    numero_serie        VARCHAR(60)  NOT NULL DEFAULT '',
    CONSTRAINT pk_equipo PRIMARY KEY (id_equipo),
    CONSTRAINT fk_equipo_cliente FOREIGN KEY (id_cliente)
        REFERENCES cliente (id_cliente) ON UPDATE CASCADE ON DELETE RESTRICT,
    -- Permite que la orden verifique que el equipo pertenece al mismo cliente
    CONSTRAINT uq_equipo_cliente UNIQUE (id_equipo, id_cliente),
    CONSTRAINT ck_equipo_tipo CHECK (tipo_dispositivo IN ('CELULAR', 'LAPTOP', 'PC_ESCRITORIO', 'TABLET', 'OTRO')),
    CONSTRAINT ck_equipo_datos CHECK (length(trim(marca)) > 0 AND length(trim(modelo)) > 0)
);

-- Un número de serie no puede repetirse (sin distinguir mayúsculas); se permiten equipos sin serie.
CREATE UNIQUE INDEX IF NOT EXISTS uq_equipo_numero_serie
    ON equipo (lower(numero_serie)) WHERE numero_serie <> '';
CREATE INDEX IF NOT EXISTS ix_equipo_cliente ON equipo (id_cliente);

-- -------------------------------------------------------------------------------------
-- ORDEN_SERVICIO: orden generada al recibir un equipo (P-08), identificada por su folio.
-- Relaciones CLIENTE 1 — * ORDEN_SERVICIO y EQUIPO 1 — * ORDEN_SERVICIO.
-- -------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orden_servicio (
    folio                   VARCHAR(12)   NOT NULL,
    id_cliente              VARCHAR(10)   NOT NULL,
    id_equipo               VARCHAR(10)   NOT NULL,
    problema_reportado      VARCHAR(1000) NOT NULL,
    diagnostico_tecnico     VARCHAR(2000) NOT NULL DEFAULT '',
    estado_actual           VARCHAR(20)   NOT NULL DEFAULT 'RECIBIDO',
    fecha_recepcion         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega_estimada  TIMESTAMP     NOT NULL,
    prioridad               VARCHAR(10)   NOT NULL DEFAULT 'NORMAL',
    costo_estimado          NUMERIC(10,2) NOT NULL,
    CONSTRAINT pk_orden_servicio PRIMARY KEY (folio),
    CONSTRAINT fk_orden_cliente FOREIGN KEY (id_cliente)
        REFERENCES cliente (id_cliente) ON UPDATE CASCADE ON DELETE RESTRICT,
    -- El equipo de la orden debe ser del mismo cliente de la orden
    CONSTRAINT fk_orden_equipo_cliente FOREIGN KEY (id_equipo, id_cliente)
        REFERENCES equipo (id_equipo, id_cliente) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_orden_estado CHECK (estado_actual IN
        ('RECIBIDO', 'PENDIENTE', 'EN_REPARACION', 'ESPERANDO_PIEZAS', 'TERMINADO', 'ENTREGADO')),
    CONSTRAINT ck_orden_prioridad CHECK (prioridad IN ('NORMAL', 'ALTA')),
    CONSTRAINT ck_orden_costo CHECK (costo_estimado >= 0),
    CONSTRAINT ck_orden_problema CHECK (length(trim(problema_reportado)) > 0)
);

CREATE INDEX IF NOT EXISTS ix_orden_cliente ON orden_servicio (id_cliente);
CREATE INDEX IF NOT EXISTS ix_orden_equipo ON orden_servicio (id_equipo);
CREATE INDEX IF NOT EXISTS ix_orden_estado ON orden_servicio (estado_actual);
