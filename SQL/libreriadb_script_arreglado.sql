DROP DATABASE IF EXISTS libreriadb_in4cm;
CREATE DATABASE IF NOT EXISTS libreriadb_in4cm;
USE libreriadb_in4cm;

-- =============================================================================
-- 1. CREACIÓN DE TABLAS (DDL)
-- =============================================================================

CREATE TABLE usuarios (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol ENUM('admin', 'bodega', 'cajero') NOT NULL,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    correo VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE proveedores (
    id_proveedor INT PRIMARY KEY AUTO_INCREMENT,
    nombre_proveedor VARCHAR(100) NOT NULL,
    contacto VARCHAR(100),
    telefono VARCHAR(15),
    direccion VARCHAR(150)
);

CREATE TABLE categorias (
    id_categoria INT PRIMARY KEY AUTO_INCREMENT,
    nombre_categoria VARCHAR(100)
);

CREATE TABLE editoriales (
    nit VARCHAR(20) PRIMARY KEY,
    nombre_editorial VARCHAR(100) NOT NULL,
    telefono_editorial VARCHAR(15),
    direccion_editorial VARCHAR(100)
);

CREATE TABLE autores (
    id_autor INT PRIMARY KEY AUTO_INCREMENT,
    nombre_autor VARCHAR(100) NOT NULL,
    apellido_autor VARCHAR(100) NOT NULL,
    nacionalidad VARCHAR(100),
    biografia TEXT
);

CREATE TABLE clientes (
    cui BIGINT PRIMARY KEY,
    nombre_cliente VARCHAR(100),
    apellido_cliente VARCHAR(100),
    correo_electronico VARCHAR(100)
);

CREATE TABLE libros (
    isbn VARCHAR(20) PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    fecha_publicacion DATE,
    precio DECIMAL(10,2) NOT NULL,
    id_categoria INT,
    nit_editorial VARCHAR(20),
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE autores_libro (
    id_autor_libro INT AUTO_INCREMENT PRIMARY KEY,
    id_autor INT,
    isbn VARCHAR(20)
);

CREATE TABLE ventas (
    id_venta INT PRIMARY KEY AUTO_INCREMENT,
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) NOT NULL,
    descuento DECIMAL(10,2) DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL,
    estado ENUM('COMPLETADA', 'ANULADA', 'DEVUELTA') DEFAULT 'COMPLETADA',
    cui_cliente BIGINT,
    id_usuario INT NOT NULL,
    usuario_autoriza_descuento INT NULL,
    fecha_anulacion TIMESTAMP NULL,
    usuario_anulacion INT NULL,
    motivo_anulacion VARCHAR(255) NULL
);

CREATE TABLE detalle_venta (
    id_detalle INT PRIMARY KEY AUTO_INCREMENT,
    id_venta INT,
    isbn VARCHAR(20),
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);

CREATE TABLE movimientos_inventario (
    id_movimiento INT PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(20),
    tipo_movimiento ENUM('INGRESO', 'VENTA', 'MERMA', 'TRASLADO', 'DEVOLUCION', 'AJUSTE') NOT NULL,
    cantidad INT NOT NULL,
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT,
    observacion VARCHAR(255)
);

-- RELACIONES (LLAVES FORÁNEAS)
ALTER TABLE autores_libro
ADD CONSTRAINT fk_al_autor FOREIGN KEY (id_autor) REFERENCES autores(id_autor) ON DELETE CASCADE,
ADD CONSTRAINT fk_al_libro FOREIGN KEY (isbn) REFERENCES libros(isbn) ON DELETE CASCADE;

ALTER TABLE ventas
ADD CONSTRAINT fk_v_cliente FOREIGN KEY (cui_cliente) REFERENCES clientes(cui) ON DELETE CASCADE,
ADD CONSTRAINT fk_v_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
ADD CONSTRAINT fk_v_autoriza FOREIGN KEY (usuario_autoriza_descuento) REFERENCES usuarios(id_usuario) ON DELETE CASCADE;

ALTER TABLE detalle_venta
ADD CONSTRAINT fk_dv_venta FOREIGN KEY (id_venta) REFERENCES ventas(id_venta) ON DELETE CASCADE,
ADD CONSTRAINT fk_dv_libros FOREIGN KEY (isbn) REFERENCES libros(isbn) ON DELETE CASCADE;

ALTER TABLE libros
ADD CONSTRAINT fk_l_categorias FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria) ON DELETE CASCADE,
ADD CONSTRAINT fk_l_editoriales FOREIGN KEY (nit_editorial) REFERENCES editoriales(nit) ON DELETE CASCADE;

ALTER TABLE movimientos_inventario
ADD CONSTRAINT fk_mi_libro FOREIGN KEY (isbn) REFERENCES libros(isbn) ON DELETE CASCADE,
ADD CONSTRAINT fk_mi_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE;


USE libreriadb_in4cm;

DELIMITER $$

-- PROCEDIMIENTOS DE CATEGORIAS
CREATE PROCEDURE sp_insertarcategoria(IN _nombre_categoria VARCHAR(100))
BEGIN
    INSERT INTO categorias(nombre_categoria) VALUES (_nombre_categoria);
END $$

-- PROCEDIMIENTOS DE EDITORIALES
CREATE PROCEDURE sp_insertareditorial(
    IN _nit VARCHAR(20),
    IN _nombre_editorial VARCHAR(100),
    IN _telefono_editorial VARCHAR(15),
    IN _direccion_editorial VARCHAR(100)
)
BEGIN
    INSERT INTO editoriales(nit, nombre_editorial, telefono_editorial, direccion_editorial) 
    VALUES (_nit, _nombre_editorial, _telefono_editorial, _direccion_editorial);
END $$

-- PROCEDIMIENTOS DE AUTORES
CREATE PROCEDURE sp_insertarautor(
    IN _nombre_autor VARCHAR(100),
    IN _apellido_autor VARCHAR(100),
    IN _nacionalidad VARCHAR(100),
    IN _biografia TEXT
)
BEGIN
    INSERT INTO autores(nombre_autor, apellido_autor, nacionalidad, biografia) 
    VALUES (_nombre_autor, _apellido_autor, _nacionalidad, _biografia);
END $$

-- PROCEDIMIENTOS DE CLIENTES
CREATE PROCEDURE sp_insertarcliente(
    IN _cui BIGINT,
    IN _nombre_cliente VARCHAR(100),
    IN _apellido_cliente VARCHAR(100),
    IN _correo_electronico VARCHAR(100)
)
BEGIN
    INSERT INTO clientes(cui, nombre_cliente, apellido_cliente, correo_electronico) 
    VALUES (_cui, _nombre_cliente, _apellido_cliente, _correo_electronico);
END $$

-- PROCEDIMIENTOS DE LIBROS
CREATE PROCEDURE sp_insertarlibro(
    IN _isbn VARCHAR(20),
    IN _titulo VARCHAR(100),
    IN _fecha_publicacion DATE,
    IN _precio DECIMAL(10,2),
    IN _id_categoria INT,
    IN _nit_editorial VARCHAR(20)
)
BEGIN
    INSERT INTO libros(isbn, titulo, fecha_publicacion, precio, id_categoria, nit_editorial) 
    VALUES (_isbn, _titulo, _fecha_publicacion, _precio, _id_categoria, _nit_editorial);
END $$

-- PROCEDIMIENTOS DE AUTORES_LIBRO
CREATE PROCEDURE sp_insertarautorlibro(
    IN _id_autor INT,
    IN _isbn VARCHAR(20)
)
BEGIN
    INSERT INTO autores_libro(id_autor, isbn) VALUES (_id_autor, _isbn);
END $$

-- PROCEDIMIENTOS DE VENTAS (Reemplaza COMPRAS para retrocompatibilidad con tus calls)
CREATE PROCEDURE sp_insertarventa(
    IN _total DECIMAL(10,2),
    IN _cui_cliente BIGINT
)
BEGIN
    -- Se inserta asumiendo que el cajero default es el ID 1 y no hay descuentos
    INSERT INTO ventas (subtotal, total, cui_cliente, id_usuario, estado) 
    VALUES (_total, _total, _cui_cliente, 1, 'COMPLETADA');
END $$

-- PROCEDIMIENTOS DE DETALLE VENTA (Reemplaza DETALLE_COMPRA, deduce precios)
CREATE PROCEDURE sp_insertardetalleventa(
    IN _id_venta INT,
    IN _isbn VARCHAR(20)
)
BEGIN
    DECLARE _precio_unitario DECIMAL(10,2);
    -- Obtener el precio actual del libro
    SELECT precio INTO _precio_unitario FROM libros WHERE isbn = _isbn;
    
    -- Insertar asumiendo 1 cantidad por línea
    INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) 
    VALUES (_id_venta, _isbn, 1, _precio_unitario, _precio_unitario);
END $$

DELIMITER ;

-- =============================================================================
-- 3. LLENADO DE DATOS (INSERTS Y CALLS)
-- =============================================================================

-- Usuario inicial necesario para registrar las ventas
INSERT INTO usuarios (username, password_hash, rol, nombre, apellido) 
VALUES ('admin_caja', 'hash123', 'admin', 'Administrador', 'Sistema');

-- CATEGORÍAS INICIALES
INSERT INTO categorias (nombre_categoria) VALUES
('Ficción Cósmica'), ('Fantasía Épica'), ('Ciencia Ficción'), ('Novela Negra'), ('Misterio'),
('Biografía'), ('Historia Universal'), ('Poesía Contemporánea'), ('Romance'), ('Terror Psicológico'),
('Autoayuda'), ('Desarrollo Personal'), ('Filosofía'), ('Arte Moderno'), ('Religión y Espiritualidad'),
('Cuentos Infantiles'), ('Literatura Juvenil'), ('Cómics y Manga'), ('Gastronomía'), ('Crónicas de Viajes');
 
-- EDITORIALES INICIALES
INSERT INTO editoriales (nit, nombre_editorial, telefono_editorial, direccion_editorial) VALUES
('1001-A', 'Editorial Planeta', '22334455', 'Zona 1, Ciudad'),
('1002-B', 'Penguin Random House', '22334456', 'Zona 10, Ciudad'),
('1003-C', 'Editorial Santillana', '22334457', 'Zona 9, Ciudad'),
('1004-D', 'Ediciones Salamandra', '22334458', 'Zona 14, Ciudad'),
('1005-E', 'Anagrama', '22334459', 'Zona 4, Ciudad'),
('1006-F', 'Alfaguara', '22334460', 'Zona 15, Ciudad'),
('1007-G', 'Seix Barral', '22334461', 'Zona 1, Ciudad'),
('1008-H', 'Tusquets Editores', '22334462', 'Zona 2, Ciudad'),
('1009-I', 'Lumen', '22334463', 'Zona 11, Ciudad'),
('1010-J', 'Debolsillo', '22334464', 'Zona 12, Ciudad'),
('1011-K', 'Ediciones B', '22334465', 'Zona 13, Ciudad'),
('1012-L', 'Roca Editorial', '22334466', 'Zona 16, Ciudad'),
('1013-M', 'Ediciones Minotauro', '22334467', 'Zona 5, Ciudad'),
('1014-N', 'Suma de Letras', '22334468', 'Zona 6, Ciudad'),
('1015-O', 'Plaza & Janés', '22334469', 'Zona 7, Ciudad'),
('1016-P', 'Editorial Siruela', '22334470', 'Zona 8, Ciudad'),
('1017-Q', 'Ediciones Destino', '22334471', 'Zona 18, Ciudad'),
('1018-R', 'Acantilado', '22334472', 'Zona 21, Ciudad'),
('1019-S', 'Editorial Piedra Santa', '22334473', 'Zona 1, Ciudad'),
('1020-T', 'Fondo de Cultura Económica', '22334474', 'Zona 9, Ciudad');
 
-- AUTORES INICIALES
INSERT INTO autores (nombre_autor, apellido_autor, nacionalidad, biografia) VALUES
('Gabriel', 'García Márquez', 'Colombiana', 'Premio Nobel de Literatura 1982. Exponente del realismo mágico.'),
('Julio', 'Cortázar', 'Argentina', 'Maestro del relato corto y creador de Rayuela.'),
('Isabel', 'Allende', 'Chilena', 'Autora de La Casa de los Espíritus. Gran exponente latinoamericana.'),
('Jorge Luis', 'Borges', 'Argentina', 'Escritor de ficciones, poemas y ensayos aclamado mundialmente.'),
('Miguel', 'Ángel Asturias', 'Guatemalteca', 'Premio Nobel de Literatura 1967. Autor de El Señor Presidente.'),
('J.K.', 'Rowling', 'Británica', 'Creadora del famoso mundo mágico de Harry Potter.'),
('George R.R.', 'Martin', 'Estadounidense', 'Autor de la saga Canción de Hielo y Fuego.'),
('Stephen', 'King', 'Estadounidense', 'El maestro contemporáneo del terror y el suspenso.'),
('Haruki', 'Murakami', 'Japonesa', 'Autor de Tokio Blues, conocido por su surrealismo melancólico.'),
('Jane', 'Austen', 'Británica', 'Autora clásica conocida por Orgullo y Prejuicio.'),
('Edgar Allan', 'Poe', 'Estadounidense', 'Padre del cuento de terror y precursor de la novela policíaca.'),
('Agatha', 'Christie', 'Británica', 'Reina del misterio y creadora de Hércules Poirot.'),
('Isaac', 'Asimov', 'Rusa/Estadounidense', 'Uno de los grandes maestros de la ciencia ficción.'),
('J.R.R.', 'Tolkien', 'Británica', 'Creador de la Tierra Media, El Hobbit y El Señor de los Anillos.'),
('Virginia', 'Woolf', 'Británica', 'Figura destacada del modernismo literario del siglo XX.'),
('Fiódor', 'Dostoievski', 'Rusa', 'Autor de Crimen y Castigo, maestro en psicología humana.'),
('Franz', 'Kafka', 'Checa', 'Conocido por obras existencialistas como La Metamorfosis.'),
('Oscar', 'Wilde', 'Irlandesa', 'Dramaturgo y autor de El Retrato de Dorian Gray.'),
('Mario', 'Vargas Llosa', 'Peruana', 'Premio Nobel de Literatura 2010.'),
('Margaret', 'Atwood', 'Canadiense', 'Autora de El cuento de la criada, fuerte exponente distópica.');
 
-- CLIENTES INICIALES
INSERT INTO clientes (cui, nombre_cliente, apellido_cliente, correo_electronico) VALUES
(2000100010101, 'Ana', 'López', 'ana.l@gmail.com'),
(2000100020101, 'Carlos', 'Méndez', 'cmendez@yahoo.com'),
(2000100030101, 'Luis', 'Pérez', 'lperez@hotmail.com'),
(2000100040101, 'María', 'García', 'mgarcia@gmail.com'),
(2000100050101, 'Jorge', 'Castillo', 'jcastillo@gmail.com'),
(2000100060101, 'Lucía', 'Fernández', 'lfernandez@yahoo.com'),
(2000100070101, 'Mario', 'Gómez', 'mgomez@gmail.com'),
(2000100080101, 'Elena', 'Morales', 'emorales@hotmail.com'),
(2000100090101, 'Pedro', 'Ramírez', 'pramirez@gmail.com'),
(2000100100101, 'Sofía', 'Vásquez', 'svasquez@gmail.com'),
(2000100110101, 'Diego', 'Hernández', 'dhernandez@yahoo.com'),
(2000100120101, 'Camila', 'Cruz', 'ccruz@hotmail.com'),
(2000100130101, 'Andrés', 'Reyes', 'areyes@gmail.com'),
(2000100140101, 'Valeria', 'Ortiz', 'vortiz@gmail.com'),
(2000100150101, 'Javier', 'Flores', 'jflores@yahoo.com'),
(2000100160101, 'Daniela', 'Díaz', 'ddiaz@gmail.com'),
(2000100170101, 'Ricardo', 'Alonso', 'ralonso@hotmail.com'),
(2000100180101, 'Gabriela', 'Rojas', 'grojas@gmail.com'),
(2000100190101, 'Héctor', 'Salazar', 'hsalazar@yahoo.com'),
(2000100200101, 'Mónica', 'Herrera', 'mherrera@gmail.com');
 
-- LIBROS INICIALES
INSERT INTO libros (isbn, titulo, fecha_publicacion, precio, id_categoria, nit_editorial) VALUES
('978-0-123', 'Cien Años de Soledad', '1967-05-30', 150.00, 1, '1001-A'),
('978-0-124', 'Rayuela', '1963-06-28', 135.50, 1, '1002-B'),
('978-0-125', 'El Señor Presidente', '1946-01-01', 120.00, 1, '1019-S'),
('978-0-126', 'Harry Potter y la Piedra Filosofal', '1997-06-26', 180.00, 2, '1004-D'),
('978-0-127', 'El Resplandor', '1977-01-28', 165.00, 10, '1005-E'),
('978-0-128', 'Fundación', '1951-05-01', 140.00, 3, '1013-M'),
('978-0-129', 'El Señor de los Anillos', '1954-07-29', 250.00, 2, '1013-M'),
('978-0-130', 'Crimen y Castigo', '1866-01-01', 95.00, 1, '1007-G'),
('978-0-131', 'Diez Negritos', '1939-11-06', 110.00, 5, '1008-H'),
('978-0-132', 'Orgullo y Prejuicio', '1813-01-28', 85.00, 9, '1009-I'),
('978-0-133', 'La Casa de los Espíritus', '1982-01-01', 145.00, 1, '1001-A'),
('978-0-134', 'El Cuento de la Criada', '1985-01-01', 160.00, 3, '1004-D');

-- AUTORES LIBRO INICIALES
INSERT INTO autores_libro (id_autor, isbn) VALUES
(1, '978-0-123'), (2, '978-0-124'), (5, '978-0-125'), (6, '978-0-126'), (8, '978-0-127'),  
(13, '978-0-128'), (14, '978-0-129'), (16, '978-0-130'), (12, '978-0-131'), (10, '978-0-132'), 
(3, '978-0-133'), (20, '978-0-134');
 
-- VENTAS INICIALES (Adaptadas con el nuevo formato manual)
INSERT INTO ventas (fecha_venta, subtotal, total, cui_cliente, id_usuario, estado) VALUES
('2026-04-20 10:30:00', 330.00, 330.00, 2000100010101, 1, 'COMPLETADA'),
('2026-04-21 14:15:00', 135.50, 135.50, 2000100020101, 1, 'COMPLETADA'),
('2026-04-22 09:45:00', 430.00, 430.00, 2000100030101, 1, 'COMPLETADA'),
('2026-04-23 16:20:00', 95.00, 95.00, 2000100040101, 1, 'COMPLETADA'),
('2026-04-24 11:10:00', 305.00, 305.00, 2000100050101, 1, 'COMPLETADA'),
('2026-04-25 18:05:00', 120.00, 120.00, 2000100060101, 1, 'COMPLETADA');
 
-- DETALLE VENTA INICIALES
-- Venta 1: Total 330.00 (Libro 978-0-123 [150.00] + Libro 978-0-126 [180.00])
INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) VALUES (1, '978-0-123', 1, 150.00, 150.00);
INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) VALUES (1, '978-0-126', 1, 180.00, 180.00);
-- Venta 2: Total 135.50
INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) VALUES (2, '978-0-124', 1, 135.50, 135.50);
-- Venta 3: Total 430.00
INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) VALUES (3, '978-0-129', 1, 250.00, 250.00);
INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) VALUES (3, '978-0-126', 1, 180.00, 180.00);
-- Venta 4: Total 95.00
INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) VALUES (4, '978-0-130', 1, 95.00, 95.00);
-- Venta 5: Total 305.00
INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) VALUES (5, '978-0-134', 1, 160.00, 160.00);
INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) VALUES (5, '978-0-133', 1, 145.00, 145.00);
-- Venta 6: Total 120.00
INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) VALUES (6, '978-0-125', 1, 120.00, 120.00);


-- =============================================================================
-- INSERTS DE LOS NUEVOS DATOS UTILIZANDO LOS PROCEDIMIENTOS ALMACENADOS
-- =============================================================================

-- CATEGORÍAS (21 - 40)
CALL sp_insertarcategoria('Drama Realista');
CALL sp_insertarcategoria('Fantasía Urbana');
CALL sp_insertarcategoria('Novela Histórica');
CALL sp_insertarcategoria('Distopía');
CALL sp_insertarcategoria('Ciberpunk');
CALL sp_insertarcategoria('Antropología');
CALL sp_insertarcategoria('Sociología Contemporánea');
CALL sp_insertarcategoria('Teatro Clásico');
CALL sp_insertarcategoria('Realismo Mágico');
CALL sp_insertarcategoria('Ensayo Político');
CALL sp_insertarcategoria('Biografía Científica');
CALL sp_insertarcategoria('Poesía Vanguardista');
CALL sp_insertarcategoria('Novela de Aventuras');
CALL sp_insertarcategoria('Mitología Comparada');
CALL sp_insertarcategoria('Divulgación Física');
CALL sp_insertarcategoria('Thriller Legal');
CALL sp_insertarcategoria('Novela Epistolar');
CALL sp_insertarcategoria('Ensayo Filosófico');
CALL sp_insertarcategoria('Crítica Literaria');
CALL sp_insertarcategoria('Literatura Gótica');

-- EDITORIALES (1021-U al 1040-N)
CALL sp_insertareditorial('1021-U', 'Editorial Alfa', '22445501', 'Zona 10, Ciudad');
CALL sp_insertareditorial('1022-V', 'Delta Libros', '22445502', 'Zona 4, Ciudad');
CALL sp_insertareditorial('1023-W', 'Ediciones Omega', '00000000', 'Sin dirección');