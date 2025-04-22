package com.juan.springboot.angeluz.forms.Controller;

import com.juan.springboot.angeluz.forms.EntryForm;
import com.juan.springboot.angeluz.forms.EntryFormRepository;
import com.juan.springboot.angeluz.forms.Mascota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

@Controller
@SessionAttributes("entryForm")
public class EntryFormController {

    @Autowired
    private EntryFormRepository entryFormRepository;

    // Mostrar formulario de checkout
    @GetMapping("/moderador/checkout")
    public String mostrarFormularioCheckout(Model model) {
        if (!model.containsAttribute("entryForm")) {
            EntryForm entryForm = new EntryForm();
            entryForm.setQueVaASer(""); // Inicializa con un valor predeterminado
            model.addAttribute("entryForm", entryForm);
        }
        return "checkout";
    }

    // Procesar formulario de checkout
    @PostMapping("/moderador/checkout")
    public String procesarFormularioCheckout(@ModelAttribute("entryForm") EntryForm entryForm, Model model) {
        model.addAttribute("entryForm", entryForm);
        return "redirect:/moderador/mascotas";
    }

    // Mostrar formulario de mascotas
    @GetMapping("/moderador/mascotas")
    public String mostrarFormularioMascotas(Model model) {
        EntryForm entryForm = (EntryForm) model.asMap().get("entryForm");

        if (entryForm == null) {
            return "redirect:/moderador/checkout"; // Redirige si no está en la sesión
        }

        if (entryForm.getMascotas() == null || entryForm.getMascotas().isEmpty()) {
            entryForm.setMascotas(List.of(new Mascota())); // Inicializa la lista de mascotas
        }

        model.addAttribute("entryForm", entryForm);
        return "mascotas";
    }

    @PostMapping("/moderador/mascotas")
    public String guardarFormularioCompleto(@ModelAttribute("entryForm") EntryForm entryForm, Model model) {
        // Establece la relación bidireccional entre EntryForm y Mascota
        if (entryForm.getMascotas() != null) {
            for (Mascota mascota : entryForm.getMascotas()) {
                mascota.setEntryForm(entryForm); // Asigna el EntryForm a cada Mascota
            }
        }

        // Guarda el formulario en la base de datos
        entryFormRepository.save(entryForm);

        // Redirige a la página de confirmación o inicio
        return "redirect:/moderador/entradasysalidas";
    }
}