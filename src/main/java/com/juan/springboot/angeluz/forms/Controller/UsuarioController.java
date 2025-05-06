package com.juan.springboot.angeluz.forms.Controller;

import com.juan.springboot.angeluz.Admin.servicios.Servicio;
import com.juan.springboot.angeluz.Admin.servicios.ServicioRepositorio;
import com.juan.springboot.angeluz.User.User;
import com.juan.springboot.angeluz.User.UserRepository;
import com.juan.springboot.angeluz.authorization.AutorizacionForm;
import com.juan.springboot.angeluz.authorization.AutorizacionFormRepository;
import com.juan.springboot.angeluz.forms.EntryForm;
import com.juan.springboot.angeluz.forms.EntryFormRepository;
import com.juan.springboot.angeluz.forms.Mascota;
import com.juan.springboot.angeluz.shop.Producto;
import com.juan.springboot.angeluz.shop.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private UserRepository userRepository;

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
    public String mostrarFormularioReserva(Model model) {
        if (!model.containsAttribute("entryForm")) {
            EntryForm entryForm = new EntryForm();
            entryForm.setQueVaASer("");
            entryForm.setProductosSeleccionados(new ArrayList<>());
            model.addAttribute("entryForm", entryForm);
        }

        List<Servicio> servicios = servicioRepository.findAll();
        model.addAttribute("servicios", servicios);
        return "reserva";
    }

    @PostMapping("/reserva")
    public String procesarReserva(
            @ModelAttribute("entryForm") EntryForm entryForm,
            @RequestParam("servicioId") Long servicioId,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener el servicio seleccionado
            Servicio servicioSeleccionado = servicioRepository.findById(servicioId)
                    .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));

            // Establecer el servicio directamente
            entryForm.setServicioSeleccionado(servicioSeleccionado);
            // El precio se actualiza automáticamente en el setter

            return "redirect:/usuario/mascotas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la reserva: " + e.getMessage());
            return "redirect:/usuario/reserva";
        }
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
        String correoUsuario = principal.getName(); // Obtiene el correo del usuario autenticado
        List<EntryForm> reservas = entryFormRepository.findByCorreo(correoUsuario); // Filtra por correo

        model.addAttribute("reservasUsuario", reservas); // Agrega las reservas al modelo
        return "misReservas"; // Retorna la vista correspondiente
    }
  @GetMapping("/registro/detalle/{id}")
  public String mostrarDetallesRegistro(@PathVariable Long id, Model model, Authentication authentication) {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      // Obtener el usuario desde la base de datos para tener acceso al correo
      User usuario = userRepository.findByUsername(userDetails.getUsername())
              .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

      Optional<EntryForm> registro = entryFormRepository.findById(id);

      if (registro.isPresent()) {
          EntryForm entryForm = registro.get();
          // Verificar que el usuario tenga acceso a este registro usando el username
          if (entryForm.getCorreo().equals(userDetails.getUsername())) {
              // Actualizar el correo con el correo real del usuario
              entryForm.setCorreo(usuario.getEmail());
              entryFormRepository.save(entryForm);

              model.addAttribute("registro", entryForm);
              return "detallesDeReserva";
          }
      }
      return "redirect:/usuario/misReservas";
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

