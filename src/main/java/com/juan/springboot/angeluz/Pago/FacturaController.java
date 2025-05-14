package com.juan.springboot.angeluz.Pago;

import com.juan.springboot.angeluz.forms.EntryForm;
import com.juan.springboot.angeluz.forms.EntryFormRepository;
import com.juan.springboot.angeluz.shop.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/moderador/EntradasYSalidas") // Modificamos la ruta base
public class FacturaController {

    @Autowired
    private EntryFormRepository entryFormRepository;

    @GetMapping("/factura/{id}") // Mantenemos la ruta para el ID
    public String mostrarFactura(@PathVariable Long id, Model model) {
        try {
            EntryForm entryForm = entryFormRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

            double total = 0;
            if (entryForm.getServicioSeleccionado() != null) {
                total += entryForm.getServicioSeleccionado().getPrecio();
            }
            if (entryForm.getProductosSeleccionados() != null) {
                total += entryForm.getProductosSeleccionados().stream()
                        .mapToDouble(Producto::getPrecio)
                        .sum();
            }

            model.addAttribute("entryForm", entryForm);
            model.addAttribute("total", total);
            model.addAttribute("metodoPago", entryForm.getMetodoPago());

            return "moderador/factura";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar la factura: " + e.getMessage());
            return "redirect:/moderador/EntradasYSalidas";
        }
    }
}