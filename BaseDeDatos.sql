DROP DATABASE IF EXISTS proyecto_tenis;
CREATE DATABASE proyecto_tenis
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
USE proyecto_tenis;

--      TABLA CATEGORIA
CREATE TABLE categoria (
  id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre       VARCHAR(100) NOT NULL,
  UNIQUE KEY uk_categoria_nombre (nombre)
) ENGINE=InnoDB;

--      TABLA TENIS
CREATE TABLE tenis (
  id_tenis     BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre       VARCHAR(100) NOT NULL,
  precio       DECIMAL(10,2) NOT NULL,
  marca        VARCHAR(100),
  descripcion  VARCHAR(500),
  imagen       VARCHAR(255),
  stock        INT NULL,
  talla        VARCHAR(20) NULL,
  id_categoria BIGINT,

  CONSTRAINT fk_tenis_categoria
    FOREIGN KEY (id_categoria)
    REFERENCES categoria(id_categoria)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
  INDEX idx_tenis_categoria (id_categoria),
  INDEX idx_tenis_nombre (nombre),
  INDEX idx_tenis_marca (marca)
) ENGINE=InnoDB;

--      TABLA ADMIN
CREATE TABLE administrador (
  id_admin BIGINT AUTO_INCREMENT PRIMARY KEY,
  email  VARCHAR(50)  NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

--      TABLA CLIENTE
CREATE TABLE cliente (
  id_cliente BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre     VARCHAR(100) NOT NULL,
  email      VARCHAR(100) NOT NULL UNIQUE,
  password   VARCHAR(255) NOT NULL,
  telefono   VARCHAR(20),
  direccion  VARCHAR(255),
  rol        VARCHAR(50) NOT NULL DEFAULT 'CLIENTE'
) ENGINE=InnoDB;

--      TABLA CARRITO
CREATE TABLE carrito (
  id_carrito     BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_cliente     BIGINT NOT NULL,
  estado         ENUM('ABIERTO','PAGADO') NOT NULL DEFAULT 'ABIERTO',
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_carrito_cliente
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
    ON UPDATE CASCADE ON DELETE CASCADE,
  INDEX idx_carrito_cliente (id_cliente)
) ENGINE=InnoDB;

--      TABLA CARRITO DETALLE
CREATE TABLE carrito_detalle (
  id_detalle   BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_carrito   BIGINT NOT NULL,
  id_tenis     BIGINT NOT NULL,
  cantidad     INT NOT NULL DEFAULT 1,
  precio_unit  DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_cd_carrito FOREIGN KEY (id_carrito)
    REFERENCES carrito(id_carrito)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_cd_tenis FOREIGN KEY (id_tenis)
    REFERENCES tenis(id_tenis)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  UNIQUE KEY uk_item_unico (id_carrito, id_tenis),
  INDEX idx_cd_carrito (id_carrito),
  INDEX idx_cd_tenis (id_tenis)
) ENGINE=InnoDB;

--      TABLA ORDEN
CREATE TABLE orden (
  id_orden   BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_cliente BIGINT NOT NULL,
  fecha      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  total      DECIMAL(10,2) NOT NULL,
  estado     ENUM('PENDIENTE','PAGADA','CANCELADA') NOT NULL DEFAULT 'PENDIENTE',
  CONSTRAINT fk_orden_cliente FOREIGN KEY (id_cliente)
    REFERENCES cliente(id_cliente)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  INDEX idx_orden_cliente (id_cliente),
  INDEX idx_orden_fecha (fecha)
) ENGINE=InnoDB;

--      TABLA ORDEN DETALLE
CREATE TABLE orden_detalle (
  id_detalle  BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_orden    BIGINT NOT NULL,
  id_tenis    BIGINT NOT NULL,
  cantidad    INT NOT NULL,
  precio_unit DECIMAL(10,2) NOT NULL,
  subtotal    DECIMAL(10,2) AS (cantidad * precio_unit) STORED,
  CONSTRAINT fk_od_orden FOREIGN KEY (id_orden)
    REFERENCES orden(id_orden)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_od_tenis FOREIGN KEY (id_tenis)
    REFERENCES tenis(id_tenis)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  INDEX idx_od_orden (id_orden),
  INDEX idx_od_tenis (id_tenis)
) ENGINE=InnoDB;

INSERT INTO administrador (email, password)
SELECT 'admin@gmail.com', 'admin123'
WHERE NOT EXISTS (SELECT 1 FROM administrador WHERE email='admin@gmail.com');

INSERT INTO categoria (nombre)
VALUES ('Deportivos'), ('Casuales'), ('Edición Limitada')
ON DUPLICATE KEY UPDATE nombre = nombre;

INSERT INTO tenis (nombre, precio, marca, descripcion, imagen, stock, talla, id_categoria)
VALUES ('Nike Air Max 270', 120000, 'Nike', 'Tenis de alto confort urbano', '/img/airmax270.jpg', 12, '42',
 (SELECT id_categoria FROM categoria WHERE nombre='Deportivos'));

INSERT INTO tenis (nombre, precio, marca, descripcion, imagen, stock, talla, id_categoria)
VALUES ('Adidas Ultraboost 22', 98000, 'Adidas', 'Modelo premium para correr largas distancias', '/img/ultraboost22.jpg', 8, '41',
 (SELECT id_categoria FROM categoria WHERE nombre='Deportivos'));

INSERT INTO tenis (nombre, precio, marca, descripcion, imagen, stock, talla, id_categoria)
VALUES ('Puma Velocity Nitro', 75000, 'Puma', 'Tenis ligeros para entrenamiento diario', '/img/puma_velocity.jpg', 6, '40',
 (SELECT id_categoria FROM categoria WHERE nombre='Deportivos'));

INSERT INTO tenis (nombre, precio, marca, descripcion, imagen, stock, talla, id_categoria)
VALUES ('New Balance 1080v12', 110000, 'New Balance', 'Amortiguación máxima para corredores', '/img/nb1080.jpg', 5, '43',
 (SELECT id_categoria FROM categoria WHERE nombre='Deportivos'));

INSERT INTO tenis (nombre, precio, marca, descripcion, imagen, stock, talla, id_categoria)
VALUES ('Vans Old Skool', 45000, 'Vans', 'Clásico modelo casual de skate', '/img/vans_oldskool.jpg', 10, '41',
 (SELECT id_categoria FROM categoria WHERE nombre='Casuales'));

INSERT INTO tenis (nombre, precio, marca, descripcion, imagen, stock, talla, id_categoria)
VALUES ('Converse Chuck Taylor', 38000, 'Converse', 'Tenis icónicos casuales', '/img/converse_chuck.jpg', 7, '40',
 (SELECT id_categoria FROM categoria WHERE nombre='Casuales'));

INSERT INTO tenis (nombre, precio, marca, descripcion, imagen, stock, talla, id_categoria)
VALUES ('Nike Dunk Low Retro', 95000, 'Nike', 'Edición moderna del clásico Dunk', '/img/nike_dunk.jpg', NULL, NULL,
 (SELECT id_categoria FROM categoria WHERE nombre='Edición Limitada'));

INSERT INTO tenis (nombre, precio, marca, descripcion, imagen, stock, talla, id_categoria)
VALUES ('Adidas Forum 84', 105000, 'Adidas', 'Modelo edición limitada estilo vintage', '/img/adidas_forum.jpg', 3, '42',
 (SELECT id_categoria FROM categoria WHERE nombre='Edición Limitada'));

INSERT INTO tenis (nombre, precio, marca, descripcion, imagen, stock, talla, id_categoria)
VALUES ('Jordan 1 High OG', 155000, 'Jordan', 'Edición limitada para coleccionistas', '/img/jordan1_og.jpg', NULL, NULL,
 (SELECT id_categoria FROM categoria WHERE nombre='Edición Limitada'));

INSERT INTO tenis (nombre, precio, marca, descripcion, imagen, stock, talla, id_categoria)
VALUES ('Reebok Classic Leather', 52000, 'Reebok', 'Tenis casuales de cuero con estilo retro', '/img/reebok_classic.jpg', 9, '41',
 (SELECT id_categoria FROM categoria WHERE nombre='Casuales'));

