package com.juan.springboot.angeluz.authorization.Controllers;

import com.juan.springboot.angeluz.authorization.AutorizacionForm;
import com.juan.springboot.angeluz.authorization.AutorizacionFormRepository;
import com.juan.springboot.angeluz.forms.EntryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

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
        return "autorizacion";
    }

    @PostMapping("/autorizacion")
    public String procesarAutorizacion(
            @ModelAttribute("entryForm") EntryForm entryForm,
            @ModelAttribute("autorizacionForm") AutorizacionForm auth,
            SessionStatus status) {

        // asociar con el entryForm
        auth.setEntryForm(entryForm);
        autorizacionRepo.save(auth);

        // limpiar sesión
        status.setComplete();

        return "redirect:/moderador/confirmacion";
    }

    @GetMapping("/confirmacion")
    public String confirmacion() {
        return "confirmacion";
    }
}