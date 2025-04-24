package com.juan.springboot.angeluz.shop.Controllers;

import com.juan.springboot.angeluz.shop.Producto;
import com.juan.springboot.angeluz.shop.ProductoRepository;
import com.juan.springboot.angeluz.shop.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TiendaController {
    private final ProductoRepository productoRepository;

    private final ProductoService productoService;

    public TiendaController(ProductoRepository productoRepository, ProductoService productoService) {
        this.productoRepository = productoRepository;
        this.productoService = productoService;
    }

    @GetMapping("/tienda")
    public String mostrarTienda(Model model) {
        List<Producto> productos = productoService.obtenerTodos();
        model.addAttribute("productos", productos);
        return "tienda";
    }

    @GetMapping("/admin/nuevo_producto")
    public String mostrarFormularioProducto(Model model) {
        model.addAttribute("producto", new Producto());
        return "admin/nuevo_producto";
    }

    @PostMapping("/admin/productos/guardar")
    public String guardarProducto(@ModelAttribute Producto producto) {
        productoService.guardar(producto);
        return "redirect:/admin/productos";
    }

    @GetMapping("/admin/productos")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.obtenerTodos());
        return "admin/lista_productos";
    }

    @GetMapping("/admin/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.eliminarPorId(id);
        return "redirect:/admin/productos";
    }
    @GetMapping("/admin/tienda")
    public String mostrarGestionTienda() {
        return "admin/gestion_tienda";
    }
    @GetMapping("/admin/productos/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        model.addAttribute("producto", producto);
        return "admin/editar_producto";
    }

    @PostMapping("/admin/productos/actualizar")
    public String actualizarProducto(@ModelAttribute Producto producto) {
        productoRepository.save(producto);
        return "redirect:/admin/productos";
    }

}
