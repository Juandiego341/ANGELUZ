package com.juan.springboot.angeluz.forms.Controller;

import com.juan.springboot.angeluz.Admin.servicios.Servicio;
import com.juan.springboot.angeluz.Admin.servicios.ServicioRepositorio;
import com.juan.springboot.angeluz.authorization.AutorizacionForm;
import com.juan.springboot.angeluz.authorization.AutorizacionFormRepository;
import com.juan.springboot.angeluz.forms.EntryForm;
import com.juan.springboot.angeluz.forms.EntryFormRepository;
import com.juan.springboot.angeluz.forms.Mascota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuario")
@SessionAttributes("entryForm")
public class UsuarioController {
    @Autowired
    private AutorizacionFormRepository autorizacionRepo;
    @Autowired
    private EntryFormRepository entryFormRepository;
    @Autowired
    private ServicioRepositorio servicioRepository;
    @ModelAttribute("autorizacionForm")
    public AutorizacionForm setupAutorizacionForm() {
        return new AutorizacionForm();
    }



    @PostMapping("/autorizaciones")
    public String procesarAutorizacion(
            @ModelAttribute("entryForm") EntryForm entryForm,
            @ModelAttribute("autorizacionForm") AutorizacionForm auth,
            Principal principal,
            SessionStatus status) {
        if (entryForm.getId() == null) {
            String correoUsuario = principal.getName();
            entryForm.setCorreo(correoUsuario);
            entryFormRepository.save(entryForm);
        }

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

        // Agrega los servicios disponibles desde la base de datos
        List<Servicio> servicios = servicioRepository.findAll();
        model.addAttribute("servicios", servicios);

        return "reserva"; // Esta es tu vista reserva.html o reservaUsuarios.html
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
            Principal principal, // Inyecta el objeto Principal
            SessionStatus sessionStatus) {
        if (entryForm.getCorreo() == null || entryForm.getCorreo().isEmpty()) {
            String correoUsuario = principal.getName();
            entryForm.setCorreo(correoUsuario);
        }
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
    @GetMapping("/misReservas")
    public String verMisReservas(Model model, Principal principal) {
        String correoUsuario = principal.getName(); // El correo del usuario logueado
        List<EntryForm> reservas = entryFormRepository.findByCorreo(correoUsuario);

        model.addAttribute("reservasUsuario", reservas);
        return "misReservas";
    }
    @GetMapping("/registro/detalle/{id}")
    public String mostrarDetallesRegistro(@PathVariable Long id, Model model, Authentication authentication) {
        String correoUsuario = authentication.getName();
        Optional<EntryForm> registro = entryFormRepository.findByIdWithMascotas(id);

        if (registro.isPresent()) {
            EntryForm entryForm = registro.get();
            if (entryForm.getCorreo().equals(correoUsuario)) {
                model.addAttribute("registro", entryForm);
                return "detallesDeReserva"; // Asegúrate que este nombre coincida con tu archivo HTML
            }
        }

        return "redirect:/usuario/mis-reservas"; // Corregido para coincidir con tu mapping existente
    }
    @PostMapping("/registro/eliminar/{id}")
    public String eliminarRegistro(@PathVariable Long id, RedirectAttributes redirectAttributes, Principal principal) {
        String correoUsuario = principal.getName();
        Optional<EntryForm> registroAEliminar = entryFormRepository.findById(id);

        if (registroAEliminar.isPresent()) {
            EntryForm entryForm = registroAEliminar.get();
            if (entryForm.getCorreo().equals(correoUsuario)) {
                try {
                    entryFormRepository.delete(entryForm);
                    redirectAttributes.addFlashAttribute("mensaje", "La reserva se ha eliminado correctamente.");
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "Hubo un error al eliminar la reserva.");
                    // Opcional: Log the error
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar esta reserva.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "La reserva que intentas eliminar no existe.");
        }

        return "redirect:/usuario/misReservas";
    }

}

