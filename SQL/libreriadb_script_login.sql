-- tabla de usuarios
create table if not exists usuarios (
    id_usuario int auto_increment primary key,
    username varchar(50) not null unique,
    password_hash varchar(255) not null,
    rol varchar(20) not null,
    nombre varchar(50) default '',
    apellido varchar(50) default '',
    correo varchar(100) default '',
    activo boolean default true,
    fecha_creacion timestamp default current_timestamp
);
 
-- procedimiento para registrar usuario (6 parámetros)
drop procedure if exists sp_registrar_usuario;
delimiter //
create procedure sp_registrar_usuario(
    in _username varchar(50), 
    in _password_hash varchar(255), 
    in _rol varchar(20),
    in _nombre varchar(50),
    in _apellido varchar(50),
    in _correo varchar(100)
)
begin
    insert into usuarios (username, password_hash, rol, nombre, apellido, correo) 
    values (_username, _password_hash, _rol, _nombre, _apellido, _correo)
    on duplicate key update 
        password_hash = _password_hash,
        rol = _rol,
        nombre = _nombre,
        apellido = _apellido,
        correo = _correo;
end //
delimiter ;

-- procedimiento para iniciar sesión
drop procedure if exists sp_iniciar_sesion;
delimiter //
create procedure sp_iniciar_sesion(
    in _username varchar(50), 
    in _password_hash varchar(255)
)
begin
    select id_usuario as id, id_usuario, username, password_hash, rol, nombre, apellido, correo, activo 
    from usuarios 
    where lower(username) = lower(_username) 
      and password_hash = _password_hash 
      and activo = true 
    limit 1;
end //
delimiter ;

-- prueba de registros iniciales (6 parámetros)
call sp_registrar_usuario('octavio', sha2('admin', 256), 'admin', 'Octavio', 'Letona', 'octavio@ejemplo.com'); 
call sp_iniciar_sesion('octavio', sha2('admin', 256)); 

call sp_registrar_usuario('Cajero', sha2('cajero', 256), 'cajero', 'Cajero', 'Prueba', 'cajero@ejemplo.com'); 
call sp_iniciar_sesion('Cajero', sha2('cajero', 256));  

call sp_registrar_usuario('Bodega', sha2('bodega', 256), 'bodega', 'Bodega', 'Prueba', 'bodega@ejemplo.com'); 
call sp_iniciar_sesion('Bodega', sha2('bodega', 256));   

select * from usuarios;
