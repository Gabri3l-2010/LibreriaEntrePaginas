-- IMPORTANTE: Ejecutar el DDL primero antes que este script
USE libreriadb_in4cm;

-- =============================================================================
-- 1. CATEGORÍAS INICIALES (1 al 40)
-- =============================================================================
INSERT INTO categorias (nombre_categoria) VALUES
('Ficción Cósmica'), ('Fantasía Épica'), ('Ciencia Ficción'), ('Novela Negra'), ('Misterio'),
('Biografía'), ('Historia Universal'), ('Poesía Contemporánea'), ('Romance'), ('Terror Psicológico'),
('Autoayuda'), ('Desarrollo Personal'), ('Filosofía'), ('Arte Moderno'), ('Religión y Espiritualidad'),
('Cuentos Infantiles'), ('Literatura Juvenil'), ('Cómics y Manga'), ('Gastronomía'), ('Crónicas de Viajes');

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

-- =============================================================================
-- 3. EDITORIALES INICIALES (1001-A al 1040-NN)
-- =============================================================================
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

CALL sp_insertareditorial('1021-U', 'Editorial Alfa', '22445501', 'Zona 10, Ciudad');
CALL sp_insertareditorial('1022-V', 'Delta Libros', '22445502', 'Zona 4, Ciudad');
CALL sp_insertareditorial('1023-W', 'Ediciones Omega', '22445503', 'Zona 9, Ciudad');
CALL sp_insertareditorial('1024-X', 'Siglo XXI Editores', '22445504', 'Zona 1, Ciudad');
CALL sp_insertareditorial('1025-Y', 'Editorial Gredos', '22445505', 'Zona 15, Ciudad');
CALL sp_insertareditorial('1026-Z', 'Hiperión Ediciones', '22445506', 'Zona 2, Ciudad');
CALL sp_insertareditorial('1027-AA', 'Editorial Akal', '22445507', 'Zona 11, Ciudad');
CALL sp_insertareditorial('1028-BB', 'Acantilado Norte', '22445508', 'Zona 7, Ciudad');
CALL sp_insertareditorial('1029-CC', 'Editorial Trotta', '22445509', 'Zona 13, Ciudad');
CALL sp_insertareditorial('1030-DD', 'Ediciones Cátedra', '22445510', 'Zona 14, Ciudad');
CALL sp_insertareditorial('1031-EE', 'Editorial Valdemar', '22445511', 'Zona 6, Ciudad');
CALL sp_insertareditorial('1032-FF', 'Editorial Txalaparta', '22445512', 'Zona 3, Ciudad');
CALL sp_insertareditorial('1033-GG', 'Ediciones Duomo', '22445513', 'Zona 12, Ciudad');
CALL sp_insertareditorial('1034-HH', 'Editorial Impedimenta', '22445514', 'Zona 16, Ciudad');
CALL sp_insertareditorial('1035-II', 'Libros del Asteroide', '22445515', 'Zona 5, Ciudad');
CALL sp_insertareditorial('1036-JJ', 'Editorial Periférica', '22445516', 'Zona 8, Ciudad');
CALL sp_insertareditorial('1037-KK', 'Sexto Piso', '22445517', 'Zona 17, Ciudad');
CALL sp_insertareditorial('1038-LL', 'Editorial Malpaso', '22445518', 'Zona 19, Ciudad');
CALL sp_insertareditorial('1039-MM', 'Caja Negra Editora', '22445519', 'Zona 21, Ciudad');
CALL sp_insertareditorial('1040-NN', 'Editorial Capitán Swing', '22445520', 'Zona 18, Ciudad');

-- =============================================================================
-- 4. AUTORES INICIALES (1 al 40)
-- =============================================================================
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

CALL sp_insertarautor('Ernest', 'Hemingway', 'Estadounidense', 'Ganador del Premio Nobel de Literatura en 1954.');
CALL sp_insertarautor('Virginia', 'Sánchez', 'Española', 'Escritora contemporánea de novela histórica y de misterio.');
CALL sp_insertarautor('Albert', 'Camus', 'Francesa', 'Novelista, ensayista y filósofo existencialista del siglo XX.');
CALL sp_insertarautor('Fyodor', 'Mikhailovich', 'Rusa', 'Cofundador de las corrientes existenciales literarias.');
CALL sp_insertarautor('Arthur', 'Conan Doyle', 'Británica', 'Célebre creador del detective más famoso, Sherlock Holmes.');
CALL sp_insertarautor('H.P.', 'Lovecraft', 'Estadounidense', 'Innovador del cuento de terror y creador del horror cósmico.');
CALL sp_insertarautor('Simone', 'de Beauvoir', 'Francesa', 'Pensadora, filósofa existencialista y escritora destacada.');
CALL sp_insertarautor('Ray', 'Bradbury', 'Estadounidense', 'Destacado autor de misterio y novelas de ciencia ficción.');
CALL sp_insertarautor('Aldous', 'Huxley', 'Británica', 'Famoso autor de novelas distópicas de crítica social.');
CALL sp_insertarautor('George', 'Orwell', 'Británica', 'Escritor de novelas críticas de sátira política universal.');
CALL sp_insertarautor('Umberto', 'Eco', 'Italiana', 'Semiólogo, filósofo y escritor de novelas históricas complejas.');
CALL sp_insertarautor('Julio', 'Verne', 'Francesa', 'Uno de los padres fundadores de la ciencia ficción moderna.');
CALL sp_insertarautor('Edgardo', 'Rodríguez', 'Guatemalteca', 'Ensayista y poeta contemporáneo con enfoque sociopolítico.');
CALL sp_insertarautor('Isabel', 'Martínez', 'Mexicana', 'Dramaturga enfocada en realismo mágico y obras de teatro.');
CALL sp_insertarautor('Carlos', 'Ruiz Zafón', 'Española', 'Famoso autor de misterio gótico contemporáneo.');
CALL sp_insertarautor('Clarice', 'Lispector', 'Brasileña', 'Una de las escritoras brasileñas más importantes del siglo XX.');
CALL sp_insertarautor('Neil', 'Gaiman', 'Británica', 'Escritor de novelas de fantasía moderna y cómics aclamados.');
CALL sp_insertarautor('Ursula', 'K. Le Guin', 'Estadounidense', 'Gran referente femenina de la fantasía y ciencia ficción.');
CALL sp_insertarautor('Philip', 'K. Dick', 'Estadounidense', 'Influyente escritor de novelas y cuentos de ciberpunk.');
CALL sp_insertarautor('Mary', 'Shelley', 'Británica', 'Creadora de la icónica novela gótica Frankenstein.');

-- =============================================================================
-- 5. CLIENTES INICIALES
-- =============================================================================
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

CALL sp_insertarcliente(2000100210101, 'Roberto', 'Gómez', 'rgomez@gmail.com');
CALL sp_insertarcliente(2000100220101, 'Fernanda', 'Pinto', 'fpinto@yahoo.com');
CALL sp_insertarcliente(2000100230101, 'Alejandro', 'Paz', 'apaz@hotmail.com');
CALL sp_insertarcliente(2000100240101, 'Diana', 'Molina', 'dmolina@gmail.com');
CALL sp_insertarcliente(2000100250101, 'Manuel', 'Estrada', 'mestrada@gmail.com');
CALL sp_insertarcliente(2000100260101, 'Beatriz', 'Juárez', 'bjuarez@yahoo.com');
CALL sp_insertarcliente(2000100270101, 'Gustavo', 'Mejía', 'gmejia@gmail.com');
CALL sp_insertarcliente(2000100280101, 'Silvia', 'Guerra', 'sguerra@hotmail.com');
CALL sp_insertarcliente(2000100290101, 'Christian', 'Orellana', 'corellana@gmail.com');
CALL sp_insertarcliente(2000100300101, 'Natalia', 'Sandoval', 'nsandoval@gmail.com');

-- =============================================================================
-- 6. LIBROS INICIALES
-- =============================================================================
INSERT INTO libros (isbn, titulo, fecha_publicacion, precio, stock_actual, stock_minimo, id_categoria, nit_editorial) VALUES
('978-0-123', 'Cien Años de Soledad', '1967-05-30', 150.00, 50, 5, 1, '1001-A'),
('978-0-124', 'Rayuela', '1963-06-28', 135.50, 40, 5, 1, '1002-B'),
('978-0-125', 'El Señor Presidente', '1946-01-01', 120.00, 30, 5, 1, '1019-S'),
('978-0-126', 'Harry Potter y la Piedra Filosofal', '1997-06-26', 180.00, 60, 10, 2, '1004-D'),
('978-0-127', 'El Resplandor', '1977-01-28', 165.00, 25, 5, 10, '1005-E'),
('978-0-128', 'Fundación', '1951-05-01', 140.00, 35, 5, 3, '1013-M'),
('978-0-129', 'El Señor de los Anillos', '1954-07-29', 250.00, 45, 10, 2, '1013-M'),
('978-0-130', 'Crimen y Castigo', '1866-01-01', 95.00, 20, 5, 1, '1007-G'),
('978-0-131', 'Diez Negritos', '1939-11-06', 110.00, 30, 5, 5, '1008-H'),
('978-0-132', 'Orgullo y Prejuicio', '1813-01-28', 85.00, 25, 5, 9, '1009-I'),
('978-0-133', 'La Casa de los Espíritus', '1982-01-01', 145.00, 35, 5, 1, '1001-A'),
('978-0-134', 'El Cuento de la Criada', '1985-01-01', 160.00, 30, 5, 3, '1004-D');

CALL sp_insertarlibro('978-0-135', 'El Viejo y el Mar', '1952-09-01', 115.00, 21, '1021-U', 50, 5, TRUE);
CALL sp_insertarlibro('978-0-136', 'Crónicas de un Destino', '2015-06-12', 140.00, 22, '1022-V', 50, 5, TRUE);
CALL sp_insertarlibro('978-0-137', 'El Extranjero', '1942-05-15', 95.00, 23, '1023-W', 50, 5, TRUE);
CALL sp_insertarlibro('978-0-138', 'Los Hermanos Karamazov', '1880-11-01', 190.00, 21, '1024-X', 50, 5, TRUE);
CALL sp_insertarlibro('978-0-139', 'Estudio en Escarlata', '1887-11-01', 85.00, 24, '1025-Y', 50, 5, TRUE);
CALL sp_insertarlibro('978-0-140', 'El horror de Dunwich', '1929-04-01', 110.00, 40, '1026-Z', 50, 5, TRUE);
CALL sp_insertarlibro('978-0-141', 'El segundo sexo', '1949-06-01', 175.00, 26, '1027-AA', 50, 5, TRUE);
CALL sp_insertarlibro('978-0-142', 'Crónicas Marcianas', '1950-05-01', 130.00, 25, '1028-BB', 50, 5, TRUE);
CALL sp_insertarlibro('978-0-143', 'Un Mundo Feliz', '1932-02-01', 125.00, 24, '1029-CC', 50, 5, TRUE);
CALL sp_insertarlibro('978-0-144', '1984', '1949-06-08', 145.00, 24, '1030-DD', 50, 5, TRUE);

-- =============================================================================
-- 7. AUTORES LIBRO (RELACIÓN AUTOR - LIBRO)
-- =============================================================================
INSERT INTO autores_libro (id_autor, isbn) VALUES
(1, '978-0-123'), (2, '978-0-124'), (5, '978-0-125'), (6, '978-0-126'), (8, '978-0-127'),  
(13, '978-0-128'), (14, '978-0-129'), (16, '978-0-130'), (12, '978-0-131'), (10, '978-0-132'), 
(3, '978-0-133'), (20, '978-0-134');

CALL sp_insertarautorlibro(21, '978-0-135');
CALL sp_insertarautorlibro(22, '978-0-136');
CALL sp_insertarautorlibro(23, '978-0-137');
CALL sp_insertarautorlibro(24, '978-0-138');
CALL sp_insertarautorlibro(25, '978-0-139');

-- =============================================================================
-- 8. VENTAS Y DETALLES DE VENTA INICIALES
-- =============================================================================

-- Usuario inicial requerido para registrar ventas
INSERT INTO usuarios (username, password_hash, rol, nombre, apellido, correo)
VALUES ('admin', SHA2('admin123', 256), 'admin', 'Administrador', 'Sistema', 'admin@libreria.com')
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO ventas (fecha_venta, subtotal, total, cui_cliente, id_usuario, estado) VALUES
(NOW(), 330.00, 330.00, 2000100010101, (SELECT id_usuario FROM usuarios LIMIT 1), 'COMPLETADA'),
(NOW(), 135.50, 135.50, 2000100020101, (SELECT id_usuario FROM usuarios LIMIT 1), 'COMPLETADA'),
(NOW(), 430.00, 430.00, 2000100030101, (SELECT id_usuario FROM usuarios LIMIT 1), 'COMPLETADA'),
(NOW(), 95.00, 95.00, 2000100040101, (SELECT id_usuario FROM usuarios LIMIT 1), 'COMPLETADA'),
(NOW(), 305.00, 305.00, 2000100050101, (SELECT id_usuario FROM usuarios LIMIT 1), 'COMPLETADA');

INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) VALUES 
(1, '978-0-123', 1, 150.00, 150.00),
(1, '978-0-126', 1, 180.00, 180.00),
(2, '978-0-124', 1, 135.50, 135.50),
(3, '978-0-129', 1, 250.00, 250.00),
(3, '978-0-126', 1, 180.00, 180.00),
(4, '978-0-130', 1, 95.00, 95.00),
(5, '978-0-134', 1, 160.00, 160.00),
(5, '978-0-133', 1, 145.00, 145.00);

-- =============================================================================
-- 9. MOVIMIENTOS DE INVENTARIO INICIALES
-- =============================================================================
INSERT INTO movimientos_inventario (isbn, tipo_movimiento, cantidad, id_usuario, observacion) VALUES
('978-0-123', 'VENTA', 1, (SELECT id_usuario FROM usuarios LIMIT 1), 'Venta inicial #1'),
('978-0-126', 'VENTA', 1, (SELECT id_usuario FROM usuarios LIMIT 1), 'Venta inicial #1'),
('978-0-124', 'VENTA', 1, (SELECT id_usuario FROM usuarios LIMIT 1), 'Venta inicial #2'),
('978-0-129', 'VENTA', 1, (SELECT id_usuario FROM usuarios LIMIT 1), 'Venta inicial #3'),
('978-0-126', 'VENTA', 1, (SELECT id_usuario FROM usuarios LIMIT 1), 'Venta inicial #3'),
('978-0-130', 'VENTA', 1, (SELECT id_usuario FROM usuarios LIMIT 1), 'Venta inicial #4'),
('978-0-134', 'VENTA', 1, (SELECT id_usuario FROM usuarios LIMIT 1), 'Venta inicial #5'),
('978-0-133', 'VENTA', 1, (SELECT id_usuario FROM usuarios LIMIT 1), 'Venta inicial #5');

SELECT 'Base de datos inicializada correctamente con DDL y DML alineados' AS mensaje;