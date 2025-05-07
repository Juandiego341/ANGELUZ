package com.juan.springboot.angeluz.Pago;



import com.juan.springboot.angeluz.forms.EntryForm;
import com.juan.springboot.angeluz.forms.EntryFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class PagoController {

    @Autowired
    private EntryFormRepository entryFormRepository;

    @GetMapping("/moderador/EntradasYSalidas/pago/{id}")
    public String mostrarFormularioPago(@PathVariable Long id, Model model) {
        Optional<EntryForm> entryFormOptional = entryFormRepository.findById(id);
        if (entryFormOptional.isPresent()) {
            EntryForm entryForm = entryFormOptional.get();
            model.addAttribute("entryForm", entryForm);
            model.addAttribute("total", entryForm.getValorTotal()); // Asegúrate de que este método esté en tu EntryForm
            return "moderador/FormularioPago"; // Nombre de tu página HTML para el formulario de pago
        } else {
            model.addAttribute("errorMessage", "Registro no encontrado.");
            return "redirect:/moderador/EntradasYSalidas";
        }
    }
    @PostMapping("/moderador/EntradasYSalidas/procesar-pago/{id}")
    public String procesarPago(@PathVariable Long id,
                               @RequestParam String metodoPago,
                               @RequestParam(required = false) String detallesPago,
                               RedirectAttributes redirectAttributes) {
        Optional<EntryForm> entryFormOptional = entryFormRepository.findById(id);
        if (entryFormOptional.isPresent()) {
            EntryForm entryForm = entryFormOptional.get();
            entryForm.setMetodoPago(metodoPago);
            entryForm.setEstadoPago("Pagado"); // Establecer el estado del pago
            entryFormRepository.save(entryForm);
            redirectAttributes.addFlashAttribute("successMessage", "Pago registrado correctamente.");
            return "redirect:/moderador/EntradasYSalidas"; // Redirigir a la lista de entradas y salidas
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar el pago: Registro no encontrado.");
            return "redirect:/moderador/EntradasYSalidas";
        }
    }
    // Aquí puedes agregar métodos POST para procesar el pago si es necesario
}