package com.juan.springboot.angeluz.Moderador;

import com.juan.springboot.angeluz.shop.Producto;
import com.juan.springboot.angeluz.shop.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/moderador")
public class TiendaModeradorController {



        private final ProductoService productoService;

        public TiendaModeradorController(ProductoService productoService) {
            this.productoService = productoService;
        }

        @GetMapping("/tienda")
        public String tienda() {
            return "moderador/tienda";
        }

        @PostMapping("/tienda/agregar")
        public String agregarProducto(@ModelAttribute Producto producto, RedirectAttributes redirectAttributes) {
            try {
                productoService.guardar(producto);
                redirectAttributes.addFlashAttribute("mensaje", "Producto guardado exitosamente");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al guardar el producto");
            }
            return "redirect:/moderador/tienda";
        }
    }
