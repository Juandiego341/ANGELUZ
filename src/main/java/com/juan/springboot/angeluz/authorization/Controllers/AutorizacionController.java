package com.juan.springboot.angeluz.authorization.Controllers;

import com.juan.springboot.angeluz.authorization.AutorizacionForm;
import com.juan.springboot.angeluz.authorization.AutorizacionFormRepository;
import com.juan.springboot.angeluz.forms.EntryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import java.util.Optional; // Importa la clase Optional

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
            @ModelAttribute("autorizacionForm") AutorizacionForm auth,
            SessionStatus status) {

        Optional<AutorizacionForm> existingAuth = autorizacionRepo.findByEntryForm(entryForm);
        if (existingAuth.isPresent()) {
            // Si ya existe una autorización para este EntryForm, actualízala
            AutorizacionForm authToUpdate = existingAuth.get();
            authToUpdate.setAutorizacionContactoVeterinario(auth.isAutorizacionContactoVeterinario());
            authToUpdate.setConsentimientoPrimerosAuxilios(auth.isConsentimientoPrimerosAuxilios());
            authToUpdate.setAutorizacionUsoImagen(auth.isAutorizacionUsoImagen());
            authToUpdate.setDeclaracionResponsabilidad(auth.isDeclaracionResponsabilidad());
            authToUpdate.setAutorizoCuidado(auth.isAutorizoCuidado());
            authToUpdate.setAutorizoDecisionesMedicas(auth.isAutorizoDecisionesMedicas());
            authToUpdate.setHorariosEntradaSalida(auth.getHorariosEntradaSalida());
            authToUpdate.setTarifasYPago(auth.getTarifasYPago());
            authToUpdate.setPoliticasCancelacion(auth.getPoliticasCancelacion());
            authToUpdate.setReglamentoInterno(auth.getReglamentoInterno());
            authToUpdate.setFirmaPropietario(auth.getFirmaPropietario());
            authToUpdate.setFechaFirma(auth.getFechaFirma());
            autorizacionRepo.save(authToUpdate);
        } else {
            // Si no existe, guarda la nueva autorización
            auth.setEntryForm(entryForm);
            autorizacionRepo.save(auth);
        }
        status.setComplete();
        return "redirect:/moderador/confirmacion";
    }

    @GetMapping("/confirmacion")
    public String confirmacion() {
        return "confirmacion";
    }
}