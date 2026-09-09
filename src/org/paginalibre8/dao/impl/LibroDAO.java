package org.paginalibre8.dao.impl;

import java.util.List;
import org.paginalibre8.model.Libro;


public interface LibroDAO {
    List<Libro> ListarTodos();
    boolean crear(Libro libro);
    Libro buscarPorId(String isbn);
    boolean actualizar(Libro libro);
    boolean eliminar(String isbn);
}