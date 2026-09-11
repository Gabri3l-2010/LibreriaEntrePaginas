package org.paginalibre8.dao.impl;

import java.util.List;
import org.paginalibre8.model.MovimientoInventario;

public interface MovimientoInventarioDAO {
    boolean registrarMovimiento(MovimientoInventario movimiento);
    List<MovimientoInventario> listarMovimientos();
    List<MovimientoInventario> listarMovimientosPorLibro(String isbn);
}
