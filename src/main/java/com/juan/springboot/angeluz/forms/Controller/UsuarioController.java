package com.juan.springboot.angeluz.forms.Controller;

import com.juan.springboot.angeluz.authorization.AutorizacionForm;
import com.juan.springboot.angeluz.authorization.AutorizacionFormRepository;
import com.juan.springboot.angeluz.forms.EntryForm;
import com.juan.springboot.angeluz.forms.EntryFormRepository;
import com.juan.springboot.angeluz.forms.Mascota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/usuario")
@SessionAttributes("entryForm")
public class UsuarioController {
    @Autowired
    private AutorizacionFormRepository autorizacionRepo;
    @Autowired
    private EntryFormRepository entryFormRepository;

    @ModelAttribute("autorizacionForm")
    public AutorizacionForm setupAutorizacionForm() {
        return new AutorizacionForm();
    }



    @PostMapping("/autorizaciones")
    public String procesarAutorizacion(
            @ModelAttribute("entryForm") EntryForm entryForm,
            @ModelAttribute("autorizacionForm") AutorizacionForm auth,
            SessionStatus status) {
        // Asegúrate de que el EntryForm se guarde primero.
        if (entryForm.getId() == null) { // Check if EntryForm is new
            entryFormRepository.save(entryForm); // Save EntryForm
        }
        auth.setEntryForm(entryForm);
        autorizacionRepo.save(auth);
        status.setComplete();
        return "redirect:/usuario/confirmacion";
    }

    @GetMapping("/confirmacion")
    public String confirmacion() {
        return "reserva-confirmada";
    }


    @ModelAttribute("entryForm")
    public EntryForm setupEntryForm() {
        EntryForm entryForm = new EntryForm();
        entryForm.setQueVaASer("");
        entryForm.setMascotas(new ArrayList<>());
        return entryForm;
    }

    @GetMapping("/reserva")
    public String mostrarFormularioCheckoutUsuario(Model model) {
        if (!model.containsAttribute("entryForm")) {
            EntryForm entryForm = new EntryForm();
            entryForm.setQueVaASer("");
            entryForm.setMascotas(new ArrayList<>());
            model.addAttribute("entryForm", entryForm);
        }
        return "reserva";
    }

    @PostMapping("/checkout")
    public String procesarFormularioCheckoutUsuario(@ModelAttribute("entryForm") EntryForm entryForm, Model model) {
        model.addAttribute("entryForm", entryForm);
        return "redirect:/usuario/mascotas";
    }

    @GetMapping("/mascotas")
    public String mostrarFormularioMascotasUsuario(Model model) {
        EntryForm entryForm = (EntryForm) model.asMap().get("entryForm");
        if (entryForm == null) {
            return "redirect:/usuario/checkout";
        }
        if (entryForm.getMascotas() == null || entryForm.getMascotas().isEmpty()) {
            entryForm.setMascotas(new ArrayList<>());
            entryForm.getMascotas().add(new Mascota());
        }
        model.addAttribute("entryForm", entryForm);
        // Asegúrate de que el nombre de la vista sea correcto
        return "registro_mascotasUsuarios";
    }

    @PostMapping("/mascotas")
    public String guardarFormularioMascotasUsuario(@ModelAttribute("entryForm") EntryForm entryForm, RedirectAttributes redirectAttributes) {
        for (Mascota mascota : entryForm.getMascotas()) {
            mascota.setEntryForm(entryForm);
        }
        redirectAttributes.addAttribute("entryFormId", entryForm.getId());
        return "redirect:/usuario/autorizacion";
    }

    @GetMapping("/autorizacion")
    public String mostrarFormularioAutorizacionUsuario(Model model, @ModelAttribute("entryForm") EntryForm entryForm) {
        if (entryForm == null) {
            return "redirect:/usuario/checkout";
        }
        model.addAttribute("entryForm", entryForm);
        return "autorizacion_Usuario";
    }

    @PostMapping("/autorizacion")
    public String guardarFormularioAutorizacionUsuario(
            @ModelAttribute("entryForm") EntryForm entryForm,
            SessionStatus sessionStatus) {
        entryFormRepository.save(entryForm);
        sessionStatus.setComplete();
        return "redirect:/usuario/reserva-confirmada";
    }

    @GetMapping("/reserva-confirmada")
    public String mostrarReservaConfirmadaUsuario(Model model, @SessionAttribute("entryForm") EntryForm entryForm) {
        model.addAttribute("entryForm", entryForm);
        return "reserva-confirmada";
    }
    @GetMapping("/autorizaciones")
    public String mostrarAutorizacion(Model model) {
        if (!model.containsAttribute("entryForm")) {
            return "redirect:/usuario/checkout";
        }
        model.addAttribute("autorizacionForm", new AutorizacionForm());
        return "autorizacion_Usuario";
    }
    @GetMapping("/usuario/mis-reservas")
    public String verMisReservas(Model model, Principal principal) {
        String correoUsuario = principal.getName(); // El correo del usuario logueado
        List<EntryForm> reservas = entryFormRepository.findByCorreo(correoUsuario);

        model.addAttribute("reservasUsuario", reservas);
        return "misReservas";
    }

}