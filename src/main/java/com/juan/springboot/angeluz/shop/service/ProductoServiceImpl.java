package com.juan.springboot.angeluz.shop.service;

import com.juan.springboot.angeluz.shop.Producto;
import com.juan.springboot.angeluz.shop.ProductoRepository;
import com.juan.springboot.angeluz.shop.service.ProductoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    @Override
    public List<Producto> obtenerProductosDisponibles() {
        return productoRepository.findByStockGreaterThanAndActivoTrue(0L);
    }

    @Override
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public void eliminarPorId(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }
    // Nuevo método para actualizar stock
    public void actualizarStock(Long productoId, Long cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Long nuevoStock = producto.getStock() - cantidad;
        producto.setStock(nuevoStock);

        if (nuevoStock <= 0) {
            producto.desactivar();
        }

        productoRepository.save(producto);
    }

    // Método para reactivar producto
    public void reactivarProducto(Long productoId, Long nuevoStock) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (nuevoStock > 0) {
            producto.setStock(nuevoStock);
            producto.activar();
            productoRepository.save(producto);
        }
    }
}

