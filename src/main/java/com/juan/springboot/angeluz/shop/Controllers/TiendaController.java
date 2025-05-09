package com.juan.springboot.angeluz.shop.Controllers;

import com.juan.springboot.angeluz.shop.Producto;
import com.juan.springboot.angeluz.shop.ProductoRepository;
import com.juan.springboot.angeluz.shop.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

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
        // Vista pública - solo productos disponibles
        List<Producto> productos = productoService.obtenerProductosDisponibles();
        model.addAttribute("productos", productos);
        return "tienda";
    }

    @GetMapping("/admin/productos")
    public String listarProductos(Model model) {
        // Vista admin - todos los productos
        List<Producto> productos = productoService.obtenerTodos();
        model.addAttribute("productos", productos);
        return "admin/lista_productos";
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

    @GetMapping("/admin/productos/buscarPorCodigo/{codigo}")
    @ResponseBody
    public ResponseEntity<Producto> buscarProductoPorCodigo(@PathVariable String codigo) {
        Optional<Producto> productoOptional = productoRepository.findByCodigoBarras(codigo);
        if (productoOptional.isPresent()) {
            return ResponseEntity.ok(productoOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
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
    @PostMapping("/admin/productos/importarCodigosBarras")
    public String importarCodigosBarras(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            try {
                // Lógica para leer el archivo (ejemplo con CSV)
                new BufferedReader(new InputStreamReader(file.getInputStream())).lines().forEach(line -> {
                    String[] parts = line.split(","); // Asumiendo formato CSV con ID y codigoBarras separados por coma
                    if (parts.length == 2) {
                        Long productoId = Long.parseLong(parts[0].trim());
                        String codigoBarras = parts[1].trim();
                        Optional<Producto> productoOptional = productoRepository.findById(productoId);
                        productoOptional.ifPresent(producto -> {
                            producto.setCodigoBarras(codigoBarras);
                            productoRepository.save(producto);
                        });
                    }
                });
                redirectAttributes.addFlashAttribute("mensaje", "Códigos de barras importados exitosamente.");
            } catch (IOException | NumberFormatException e) {
                redirectAttributes.addFlashAttribute("error", "Error al importar el archivo: " + e.getMessage());
            }
            return "redirect:/admin/productos";
        } else {
            redirectAttributes.addFlashAttribute("error", "Por favor, selecciona un archivo para importar.");
            return "redirect:/admin/productos";
        }
    }

}
