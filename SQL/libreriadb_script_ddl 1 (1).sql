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

-- T3.4.1 (D1): Revisar la estructura actual de la tabla libros
-- T3.4.2 (D1): Campos clave de inventario y estado: stock_actual, stock_minimo, activo
CREATE TABLE libros (
    isbn VARCHAR(20) PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    fecha_publicacion DATE,
    precio DECIMAL(10,2) NOT NULL,
    id_categoria INT,
    nit_editorial VARCHAR(20),
    stock_actual INT NOT NULL DEFAULT 50,
    stock_minimo INT NOT NULL DEFAULT 5,
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

-- =============================================================================
-- 2. LLAVES FORÁNEAS (RELACIONES)
-- =============================================================================
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

-- T3.4.4 (D1): Verificar relaciones con categorías y editoriales
ALTER TABLE libros
ADD CONSTRAINT fk_l_categorias FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria) ON DELETE CASCADE,
ADD CONSTRAINT fk_l_editoriales FOREIGN KEY (nit_editorial) REFERENCES editoriales(nit) ON DELETE CASCADE;

ALTER TABLE movimientos_inventario
ADD CONSTRAINT fk_mi_libro FOREIGN KEY (isbn) REFERENCES libros(isbn) ON DELETE CASCADE,
ADD CONSTRAINT fk_mi_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE;


-- =============================================================================
-- 3. PROCEDIMIENTOS ALMACENADOS (STORED PROCEDURES)
-- =============================================================================
DELIMITER $$

-- PROCEDIMIENTOS DE USUARIOS
DROP PROCEDURE IF EXISTS sp_registrar_usuario $$
CREATE PROCEDURE sp_registrar_usuario(
    IN _username VARCHAR(50), 
    IN _password_hash VARCHAR(255), 
    IN _rol VARCHAR(20),
    IN _nombre VARCHAR(100),
    IN _apellido VARCHAR(100),
    IN _correo VARCHAR(100)
)
BEGIN
    INSERT INTO usuarios (username, password_hash, rol, nombre, apellido, correo) 
    VALUES (_username, _password_hash, LOWER(_rol), _nombre, _apellido, _correo)
    ON DUPLICATE KEY UPDATE 
        password_hash = _password_hash,
        rol = LOWER(_rol),
        nombre = _nombre,
        apellido = _apellido,
        correo = _correo;
END $$

DROP PROCEDURE IF EXISTS sp_iniciar_sesion $$
CREATE PROCEDURE sp_iniciar_sesion(
    IN _username VARCHAR(50), 
    IN _password_hash VARCHAR(255)
)
BEGIN
    SELECT id_usuario AS id, id_usuario, username, password_hash, rol, nombre, apellido, correo, activo 
    FROM usuarios 
    WHERE LOWER(username) = LOWER(_username) 
      AND password_hash = _password_hash 
      AND activo = TRUE 
    LIMIT 1;
END $$

DROP PROCEDURE IF EXISTS sp_buscar_usuario $$
CREATE PROCEDURE sp_buscar_usuario(
    IN _username VARCHAR(50)
)
BEGIN
    SELECT id_usuario AS id, id_usuario, username, rol, nombre, apellido, correo, activo 
    FROM usuarios 
    WHERE username = _username;
END $$

DROP PROCEDURE IF EXISTS sp_cambiar_password $$
CREATE PROCEDURE sp_cambiar_password(
    IN _id_usuario INT,
    IN _nueva_password_hash VARCHAR(255)
)
BEGIN
    UPDATE usuarios 
    SET password_hash = _nueva_password_hash 
    WHERE id_usuario = _id_usuario;
END $$

-- PROCEDIMIENTOS DE CATEGORIAS
DROP PROCEDURE IF EXISTS sp_insertarcategoria $$
CREATE PROCEDURE sp_insertarcategoria(IN _nombre_categoria VARCHAR(100))
BEGIN
    INSERT IGNORE INTO categorias(nombre_categoria) VALUES (_nombre_categoria);
END $$

DROP PROCEDURE IF EXISTS sp_listarcategorias $$
CREATE PROCEDURE sp_listarcategorias()
BEGIN
    SELECT id_categoria, nombre_categoria FROM categorias;
END $$

DROP PROCEDURE IF EXISTS sp_buscarcategoria $$
CREATE PROCEDURE sp_buscarcategoria(IN _id_categoria INT)
BEGIN
    SELECT id_categoria, nombre_categoria FROM categorias WHERE id_categoria = _id_categoria;
END $$

DROP PROCEDURE IF EXISTS sp_actualizarcategoria $$
CREATE PROCEDURE sp_actualizarcategoria(IN _id_categoria INT, IN _nombre_categoria VARCHAR(100))
BEGIN
    UPDATE categorias SET nombre_categoria = _nombre_categoria WHERE id_categoria = _id_categoria;
END $$

DROP PROCEDURE IF EXISTS sp_eliminarcategoria $$
CREATE PROCEDURE sp_eliminarcategoria(IN _id_categoria INT)
BEGIN
    DELETE FROM categorias WHERE id_categoria = _id_categoria;
END $$

-- PROCEDIMIENTOS DE EDITORIALES
DROP PROCEDURE IF EXISTS sp_insertareditorial $$
CREATE PROCEDURE sp_insertareditorial(
    IN _nit VARCHAR(20),
    IN _nombre_editorial VARCHAR(100),
    IN _telefono_editorial VARCHAR(15),
    IN _direccion_editorial VARCHAR(100)
)
BEGIN
    INSERT IGNORE INTO editoriales(nit, nombre_editorial, telefono_editorial, direccion_editorial) 
    VALUES (_nit, _nombre_editorial, _telefono_editorial, _direccion_editorial);
END $$

DROP PROCEDURE IF EXISTS sp_listareditoriales $$
CREATE PROCEDURE sp_listareditoriales()
BEGIN
    SELECT nit, nombre_editorial, telefono_editorial, direccion_editorial FROM editoriales;
END $$

DROP PROCEDURE IF EXISTS sp_buscareditorial $$
CREATE PROCEDURE sp_buscareditorial(IN _nit VARCHAR(20))
BEGIN
    SELECT nit, nombre_editorial, telefono_editorial, direccion_editorial FROM editoriales WHERE nit = _nit;
END $$

DROP PROCEDURE IF EXISTS sp_actualizareditorial $$
CREATE PROCEDURE sp_actualizareditorial(
    IN _nit VARCHAR(20),
    IN _nombre_editorial VARCHAR(100),
    IN _telefono_editorial VARCHAR(15),
    IN _direccion_editorial VARCHAR(100)
)
BEGIN
    UPDATE editoriales 
    SET nombre_editorial = _nombre_editorial, 
        telefono_editorial = _telefono_editorial, 
        direccion_editorial = _direccion_editorial 
    WHERE nit = _nit;
END $$

DROP PROCEDURE IF EXISTS sp_eliminareditorial $$
CREATE PROCEDURE sp_eliminareditorial(IN _nit VARCHAR(20))
BEGIN
    DELETE FROM editoriales WHERE nit = _nit;
END $$

-- PROCEDIMIENTOS DE AUTORES
DROP PROCEDURE IF EXISTS sp_insertarautor $$
CREATE PROCEDURE sp_insertarautor(
    IN _nombre_autor VARCHAR(100),
    IN _apellido_autor VARCHAR(100),
    IN _nacionalidad VARCHAR(100),
    IN _biografia TEXT
)
BEGIN
    INSERT IGNORE INTO autores(nombre_autor, apellido_autor, nacionalidad, biografia) 
    VALUES (_nombre_autor, _apellido_autor, _nacionalidad, _biografia);
END $$

DROP PROCEDURE IF EXISTS sp_listarautores $$
CREATE PROCEDURE sp_listarautores()
BEGIN
    SELECT id_autor, nombre_autor, apellido_autor, nacionalidad, biografia FROM autores;
END $$

DROP PROCEDURE IF EXISTS sp_buscarautor $$
CREATE PROCEDURE sp_buscarautor(IN _id_autor INT)
BEGIN
    SELECT id_autor, nombre_autor, apellido_autor, nacionalidad, biografia FROM autores WHERE id_autor = _id_autor;
END $$

DROP PROCEDURE IF EXISTS sp_actualizarautor $$
CREATE PROCEDURE sp_actualizarautor(
    IN _id_autor INT,
    IN _nombre_autor VARCHAR(100),
    IN _apellido_autor VARCHAR(100),
    IN _nacionalidad VARCHAR(100),
    IN _biografia TEXT
)
BEGIN
    UPDATE autores 
    SET nombre_autor = _nombre_autor, 
        apellido_autor = _apellido_autor, 
        nacionalidad = _nacionalidad, 
        biografia = _biografia 
    WHERE id_autor = _id_autor;
END $$

DROP PROCEDURE IF EXISTS sp_eliminarautor $$
CREATE PROCEDURE sp_eliminarautor(IN _id_autor INT)
BEGIN
    DELETE FROM autores WHERE id_autor = _id_autor;
END $$

-- PROCEDIMIENTOS DE CLIENTES
DROP PROCEDURE IF EXISTS sp_insertarcliente $$
CREATE PROCEDURE sp_insertarcliente(
    IN _cui BIGINT,
    IN _nombre_cliente VARCHAR(100),
    IN _apellido_cliente VARCHAR(100),
    IN _correo_electronico VARCHAR(100)
)
BEGIN
    INSERT IGNORE INTO clientes(cui, nombre_cliente, apellido_cliente, correo_electronico) 
    VALUES (_cui, _nombre_cliente, _apellido_cliente, _correo_electronico);
END $$

DROP PROCEDURE IF EXISTS sp_listarclientes $$
CREATE PROCEDURE sp_listarclientes()
BEGIN
    SELECT cui, nombre_cliente, apellido_cliente, correo_electronico FROM clientes;
END $$

DROP PROCEDURE IF EXISTS sp_buscarcliente $$
CREATE PROCEDURE sp_buscarcliente(IN _cui BIGINT)
BEGIN
    SELECT cui, nombre_cliente, apellido_cliente, correo_electronico FROM clientes WHERE cui = _cui;
END $$

DROP PROCEDURE IF EXISTS sp_actualizarcliente $$
CREATE PROCEDURE sp_actualizarcliente(
    IN _cui BIGINT,
    IN _nombre_cliente VARCHAR(100),
    IN _apellido_cliente VARCHAR(100),
    IN _correo_electronico VARCHAR(100)
)
BEGIN
    UPDATE clientes 
    SET nombre_cliente = _nombre_cliente, 
        apellido_cliente = _apellido_cliente, 
        correo_electronico = _correo_electronico 
    WHERE cui = _cui;
END $$

DROP PROCEDURE IF EXISTS sp_eliminarcliente $$
CREATE PROCEDURE sp_eliminarcliente(IN _cui BIGINT)
BEGIN
    DELETE FROM clientes WHERE cui = _cui;
END $$

-- PROCEDIMIENTOS DE LIBROS
DROP PROCEDURE IF EXISTS sp_insertarlibro $$
CREATE PROCEDURE sp_insertarlibro(
    IN _isbn VARCHAR(20),
    IN _titulo VARCHAR(100),
    IN _fecha_publicacion DATE,
    IN _precio DECIMAL(10,2),
    IN _id_categoria INT,
    IN _nit_editorial VARCHAR(20),
    IN _stock_actual INT,
    IN _stock_minimo INT,
    IN _activo BOOLEAN
)
BEGIN
    INSERT IGNORE INTO libros(isbn, titulo, fecha_publicacion, precio, id_categoria, nit_editorial, stock_actual, stock_minimo, activo) 
    VALUES (_isbn, _titulo, _fecha_publicacion, _precio, _id_categoria, _nit_editorial, IFNULL(_stock_actual, 50), IFNULL(_stock_minimo, 5), IFNULL(_activo, TRUE));
END $$

DROP PROCEDURE IF EXISTS sp_listarlibros $$
CREATE PROCEDURE sp_listarlibros()
BEGIN
    SELECT l.*, l.stock_actual AS stock, c.nombre_categoria, e.nombre_editorial,
           COALESCE(CONCAT(a.nombre_autor, ' ', a.apellido_autor), 'Sin autor') AS autor
    FROM libros l
    LEFT JOIN categorias c ON l.id_categoria = c.id_categoria
    LEFT JOIN editoriales e ON l.nit_editorial = e.nit
    LEFT JOIN autores_libro al ON l.isbn = al.isbn
    LEFT JOIN autores a ON al.id_autor = a.id_autor;
END $$

DROP PROCEDURE IF EXISTS sp_buscarlibro $$
CREATE PROCEDURE sp_buscarlibro(IN _isbn VARCHAR(20))
BEGIN
    SELECT l.*, l.stock_actual AS stock, c.nombre_categoria, e.nombre_editorial,
           COALESCE(CONCAT(a.nombre_autor, ' ', a.apellido_autor), 'Sin autor') AS autor
    FROM libros l
    LEFT JOIN categorias c ON l.id_categoria = c.id_categoria
    LEFT JOIN editoriales e ON l.nit_editorial = e.nit
    LEFT JOIN autores_libro al ON l.isbn = al.isbn
    LEFT JOIN autores a ON al.id_autor = a.id_autor
    WHERE l.isbn = _isbn;
END $$

DROP PROCEDURE IF EXISTS sp_actualizarlibro $$
CREATE PROCEDURE sp_actualizarlibro(
    IN _isbn VARCHAR(20),
    IN _titulo VARCHAR(100),
    IN _fecha_publicacion DATE,
    IN _precio DECIMAL(10,2),
    IN _id_categoria INT,
    IN _nit_editorial VARCHAR(20),
    IN _stock_actual INT,
    IN _stock_minimo INT,
    IN _activo BOOLEAN
)
BEGIN
    UPDATE libros 
    SET titulo = _titulo, 
        fecha_publicacion = _fecha_publicacion, 
        precio = _precio, 
        id_categoria = _id_categoria, 
        nit_editorial = _nit_editorial,
        stock_actual = IFNULL(_stock_actual, stock_actual),
        stock_minimo = IFNULL(_stock_minimo, stock_minimo),
        activo = IFNULL(_activo, activo)
    WHERE isbn = _isbn;
END $$

DROP PROCEDURE IF EXISTS sp_eliminarlibro $$
CREATE PROCEDURE sp_eliminarlibro(IN _isbn VARCHAR(20))
BEGIN
    DELETE FROM libros WHERE isbn = _isbn;
END $$

-- PROCEDIMIENTOS DE AUTORES_LIBRO
DROP PROCEDURE IF EXISTS sp_insertarautorlibro $$
CREATE PROCEDURE sp_insertarautorlibro(
    IN _id_autor INT,
    IN _isbn VARCHAR(20)
)
BEGIN
    INSERT IGNORE INTO autores_libro(id_autor, isbn) VALUES (_id_autor, _isbn);
END $$

-- PROCEDIMIENTOS DE VENTAS Y REPORTES
DROP PROCEDURE IF EXISTS sp_insertarventa $$
CREATE PROCEDURE sp_insertarventa(
    IN _total DECIMAL(10,2),
    IN _cui_cliente BIGINT
)
BEGIN
    INSERT INTO ventas (subtotal, total, cui_cliente, id_usuario, estado) 
    VALUES (_total, _total, _cui_cliente, 1, 'COMPLETADA');
END $$

DROP PROCEDURE IF EXISTS sp_insertardetalleventa $$
CREATE PROCEDURE sp_insertardetalleventa(
    IN _id_venta INT,
    IN _isbn VARCHAR(20)
)
BEGIN
    DECLARE _precio_unitario DECIMAL(10,2);
    SELECT precio INTO _precio_unitario FROM libros WHERE isbn = _isbn;
    
    INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) 
    VALUES (_id_venta, _isbn, 1, _precio_unitario, _precio_unitario);
END $$

DROP PROCEDURE IF EXISTS sp_obtener_ventas_del_dia $$
CREATE PROCEDURE sp_obtener_ventas_del_dia()
BEGIN
    SELECT v.id_venta AS id, v.id_venta, v.fecha_venta AS fecha, v.subtotal, v.descuento, v.total, v.estado, v.cui_cliente AS nit_cliente, v.id_usuario,
           u.username AS username_usuario, CONCAT(c.nombre_cliente, ' ', c.apellido_cliente) AS nombre_cliente
    FROM ventas v
    LEFT JOIN usuarios u ON v.id_usuario = u.id_usuario
    LEFT JOIN clientes c ON v.cui_cliente = c.cui
    WHERE DATE(v.fecha_venta) = CURDATE()
    ORDER BY v.id_venta DESC;
END $$

DROP PROCEDURE IF EXISTS sp_listar_usuarios $$
CREATE PROCEDURE sp_listar_usuarios()
BEGIN
    SELECT id_usuario AS id, id_usuario, username, password_hash, rol, nombre, apellido, correo, activo 
    FROM usuarios;
END $$

DROP PROCEDURE IF EXISTS sp_obtener_usuario_por_id $$
CREATE PROCEDURE sp_obtener_usuario_por_id(IN _id_usuario INT)
BEGIN
    SELECT id_usuario AS id, id_usuario, username, password_hash, rol, nombre, apellido, correo, activo 
    FROM usuarios 
    WHERE id_usuario = _id_usuario;
END $$

DROP PROCEDURE IF EXISTS sp_actualizar_usuario $$
CREATE PROCEDURE sp_actualizar_usuario(
    IN _id_usuario INT,
    IN _username VARCHAR(50),
    IN _rol VARCHAR(20),
    IN _nombre VARCHAR(100),
    IN _apellido VARCHAR(100),
    IN _correo VARCHAR(100),
    IN _activo BOOLEAN
)
BEGIN
    UPDATE usuarios 
    SET username = _username, rol = LOWER(_rol), nombre = _nombre, apellido = _apellido, correo = _correo, activo = _activo 
    WHERE id_usuario = _id_usuario;
END $$

DROP PROCEDURE IF EXISTS sp_desactivar_usuario $$
CREATE PROCEDURE sp_desactivar_usuario(IN _id_usuario INT)
BEGIN
    UPDATE usuarios SET activo = FALSE WHERE id_usuario = _id_usuario;
END $$

DROP PROCEDURE IF EXISTS sp_validar_password $$
CREATE PROCEDURE sp_validar_password(
    IN _id_usuario INT,
    IN _password_hash VARCHAR(255)
)
BEGIN
    SELECT 1 FROM usuarios WHERE id_usuario = _id_usuario AND password_hash = _password_hash LIMIT 1;
END $$

DROP PROCEDURE IF EXISTS sp_existe_username $$
CREATE PROCEDURE sp_existe_username(IN _username VARCHAR(50))
BEGIN
    SELECT 1 FROM usuarios WHERE username = _username LIMIT 1;
END $$

DROP PROCEDURE IF EXISTS sp_buscar_libros_por_titulo $$
CREATE PROCEDURE sp_buscar_libros_por_titulo(IN _titulo VARCHAR(100))
BEGIN
    SELECT l.*, l.stock_actual AS stock, c.nombre_categoria, e.nombre_editorial,
           COALESCE(CONCAT(a.nombre_autor, ' ', a.apellido_autor), 'Sin autor') AS autor 
    FROM libros l 
    LEFT JOIN categorias c ON l.id_categoria = c.id_categoria
    LEFT JOIN editoriales e ON l.nit_editorial = e.nit
    LEFT JOIN autores_libro al ON l.isbn = al.isbn
    LEFT JOIN autores a ON al.id_autor = a.id_autor
    WHERE LOWER(l.titulo) LIKE LOWER(CONCAT('%', _titulo, '%'));
END $$

DROP PROCEDURE IF EXISTS sp_buscar_libros_por_autor $$
CREATE PROCEDURE sp_buscar_libros_por_autor(IN _autor VARCHAR(100))
BEGIN
    SELECT l.*, l.stock_actual AS stock, c.nombre_categoria, e.nombre_editorial,
           CONCAT(a.nombre_autor, ' ', a.apellido_autor) AS autor 
    FROM libros l 
    LEFT JOIN categorias c ON l.id_categoria = c.id_categoria
    LEFT JOIN editoriales e ON l.nit_editorial = e.nit
    JOIN autores_libro al ON l.isbn = al.isbn 
    JOIN autores a ON al.id_autor = a.id_autor 
    WHERE LOWER(CONCAT(a.nombre_autor, ' ', a.apellido_autor)) LIKE LOWER(CONCAT('%', _autor, '%'));
END $$

DROP PROCEDURE IF EXISTS sp_buscar_venta_por_id $$
CREATE PROCEDURE sp_buscar_venta_por_id(IN _id_venta INT)
BEGIN
    SELECT v.id_venta AS id, v.*, v.fecha_venta AS fecha, v.cui_cliente AS nit_cliente, u.username AS username_usuario, CONCAT(c.nombre_cliente, ' ', c.apellido_cliente) AS nombre_cliente 
    FROM ventas v 
    LEFT JOIN usuarios u ON v.id_usuario = u.id_usuario 
    LEFT JOIN clientes c ON v.cui_cliente = c.cui 
    WHERE v.id_venta = _id_venta;
END $$

DROP PROCEDURE IF EXISTS sp_listar_ventas $$
CREATE PROCEDURE sp_listar_ventas()
BEGIN
    SELECT v.id_venta AS id, v.*, v.fecha_venta AS fecha, v.cui_cliente AS nit_cliente, u.username AS username_usuario, CONCAT(c.nombre_cliente, ' ', c.apellido_cliente) AS nombre_cliente 
    FROM ventas v 
    LEFT JOIN usuarios u ON v.id_usuario = u.id_usuario 
    LEFT JOIN clientes c ON v.cui_cliente = c.cui 
    ORDER BY v.id_venta DESC;
END $$

DROP PROCEDURE IF EXISTS sp_validar_stock_libro $$
CREATE PROCEDURE sp_validar_stock_libro(IN _isbn VARCHAR(20))
BEGIN
    SELECT stock_actual FROM libros WHERE isbn = _isbn;
END $$

DROP PROCEDURE IF EXISTS sp_actualizar_stock_libro $$
CREATE PROCEDURE sp_actualizar_stock_libro(IN _isbn VARCHAR(20), IN _cantidad INT)
BEGIN
    UPDATE libros SET stock_actual = stock_actual - _cantidad WHERE isbn = _isbn AND stock_actual >= _cantidad;
END $$

DROP PROCEDURE IF EXISTS sp_obtener_ventas_del_dia_por_usuario $$
CREATE PROCEDURE sp_obtener_ventas_del_dia_por_usuario(IN _id_usuario INT)
BEGIN
    SELECT v.id_venta AS id, v.*, v.fecha_venta AS fecha, v.cui_cliente AS nit_cliente, u.username AS username_usuario, CONCAT(c.nombre_cliente, ' ', c.apellido_cliente) AS nombre_cliente 
    FROM ventas v 
    LEFT JOIN usuarios u ON v.id_usuario = u.id_usuario 
    LEFT JOIN clientes c ON v.cui_cliente = c.cui 
    WHERE DATE(v.fecha_venta) = CURDATE() AND v.id_usuario = _id_usuario 
    ORDER BY v.id_venta DESC;
END $$

DROP PROCEDURE IF EXISTS sp_obtener_detalles_por_venta $$
CREATE PROCEDURE sp_obtener_detalles_por_venta(IN _id_venta INT)
BEGIN
    SELECT d.id_detalle AS id, d.*, d.isbn AS isbn_libro, l.titulo AS titulo_libro 
    FROM detalle_venta d 
    LEFT JOIN libros l ON d.isbn = l.isbn 
    WHERE d.id_venta = _id_venta;
END $$

DROP PROCEDURE IF EXISTS sp_registrar_movimiento_inventario $$
CREATE PROCEDURE sp_registrar_movimiento_inventario(
    IN _isbn VARCHAR(20),
    IN _tipo_movimiento VARCHAR(20),
    IN _cantidad INT,
    IN _id_usuario INT,
    IN _observacion VARCHAR(255)
)
BEGIN
    INSERT INTO movimientos_inventario (isbn, tipo_movimiento, cantidad, id_usuario, observacion) 
    VALUES (_isbn, _tipo_movimiento, _cantidad, _id_usuario, _observacion);
END $$

DELIMITER ;

-- =============================================================================
-- 4. VISTAS (VIEWS)
-- =============================================================================

CREATE OR REPLACE VIEW vw_lista_categorias AS
SELECT 
    id_categoria AS 'id categoría',
    nombre_categoria AS 'categoría'
FROM categorias;

CREATE OR REPLACE VIEW vw_lista_editoriales AS
SELECT 
    nit AS 'nit editorial',
    nombre_editorial AS 'editorial',
    telefono_editorial AS 'teléfono',
    direccion_editorial AS 'dirección'
FROM editoriales;

CREATE OR REPLACE VIEW vw_lista_autores AS
SELECT 
    id_autor AS 'id autor',
    CONCAT(nombre_autor, ' ', apellido_autor) AS 'autor',
    nacionalidad AS 'nacionalidad',
    biografia AS 'biografía'
FROM autores;

CREATE OR REPLACE VIEW vw_lista_clientes AS
SELECT 
    cui AS 'cui cliente',
    CONCAT(nombre_cliente, ' ', apellido_cliente) AS 'cliente',
    correo_electronico AS 'correo electrónico'
FROM clientes;

CREATE OR REPLACE VIEW vw_lista_libros AS
SELECT 
    l.isbn AS 'isbn',
    l.titulo AS 'título',
    COALESCE(CONCAT(a.nombre_autor, ' ', a.apellido_autor), 'Sin autor') AS 'autor',
    l.fecha_publicacion AS 'fecha de publicación',
    l.precio AS 'precio',
    l.stock_actual AS 'stock',
    c.nombre_categoria AS 'categoría',
    e.nombre_editorial AS 'editorial'
FROM libros l
LEFT JOIN categorias c ON l.id_categoria = c.id_categoria
LEFT JOIN editoriales e ON l.nit_editorial = e.nit
LEFT JOIN autores_libro al ON l.isbn = al.isbn
LEFT JOIN autores a ON al.id_autor = a.id_autor;

CREATE OR REPLACE VIEW vw_lista_autores_libro AS
SELECT 
    al.id_autor_libro AS 'id relación',
    CONCAT(a.nombre_autor, ' ', a.apellido_autor) AS 'autor',
    l.titulo AS 'título del libro',
    l.isbn AS 'isbn'
FROM autores_libro al
INNER JOIN autores a ON al.id_autor = a.id_autor
INNER JOIN libros l ON al.isbn = l.isbn;

CREATE OR REPLACE VIEW vw_lista_ventas AS
SELECT 
    v.id_venta AS 'no. venta',
    v.fecha_venta AS 'fecha/hora',
    v.subtotal AS 'subtotal',
    v.total AS 'total',
    v.cui_cliente AS 'cui cliente',
    CONCAT(cl.nombre_cliente, ' ', cl.apellido_cliente) AS 'cliente',
    u.username AS 'cajero'
FROM ventas v
INNER JOIN clientes cl ON v.cui_cliente = cl.cui
INNER JOIN usuarios u ON v.id_usuario = u.id_usuario;

CREATE OR REPLACE VIEW vw_factura_ventas AS
SELECT 
    v.id_venta AS 'numero_factura',
    v.fecha_venta AS 'fecha_emision',
    cl.cui AS 'cui_cliente',
    CONCAT(cl.nombre_cliente, ' ', cl.apellido_cliente) AS 'nombre_cliente',
    cl.correo_electronico AS 'correo_cliente',
    l.isbn AS 'isbn_libro',
    l.titulo AS 'descripcion_libro',
    dv.cantidad AS 'cantidad',
    dv.precio_unitario AS 'precio_articulo',
    dv.subtotal AS 'subtotal_linea',
    v.total AS 'gran_total'
FROM ventas v
INNER JOIN clientes cl ON v.cui_cliente = cl.cui
INNER JOIN detalle_venta dv ON v.id_venta = dv.id_venta
INNER JOIN libros l ON dv.isbn = l.isbn;
