package com.juan.springboot.angeluz.authorization.Controllers;

import com.juan.springboot.angeluz.authorization.AutorizacionForm;
import com.juan.springboot.angeluz.authorization.AutorizacionFormRepository;
import com.juan.springboot.angeluz.forms.EntryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/moderador")
@SessionAttributes("entryForm")
public class AutorizacionController {

    @Autowired
    private AutorizacionFormRepository autorizacionRepo;

    @ModelAttribute("autorizacionForm")
    public AutorizacionForm setupAutorizacionForm() {
        return new AutorizacionForm();
    }

    @GetMapping("/autorizacion")
    public String mostrarAutorizacion(Model model) {
        if (!model.containsAttribute("entryForm")) {
            return "redirect:/moderador/checkout";
        }
        model.addAttribute("autorizacionForm", new AutorizacionForm());
        return "autorizacion";
    }

    @PostMapping("/autorizacion")
    public String procesarAutorizacion(
            @ModelAttribute("entryForm") EntryForm entryForm,
            @ModelAttribute("autorizacionForm") AutorizacionForm auth) {

        auth.setEntryForm(entryForm);
        entryForm.setAutorizacionForm(auth); // Asegúrate de establecer la relación en ambos lados

        return "redirect:/resumen-total";
    }

    @GetMapping("/confirmacion")
    public String confirmacion() {
        return "confirmacion";
    }
}