package com.juan.springboot.angeluz.shop.service;

import com.juan.springboot.angeluz.shop.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> obtenerTodos();
    Optional<Producto> obtenerPorId(Long id);
    Producto guardar(Producto producto);
    void eliminarPorId(Long id);
    List<Producto> buscarPorCategoria(String categoria);

    List<Producto> obtenerProductosDisponibles();
}
