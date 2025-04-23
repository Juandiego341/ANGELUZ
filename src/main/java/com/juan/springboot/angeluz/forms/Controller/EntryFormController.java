package com.juan.springboot.angeluz.forms.Controller;

        import com.juan.springboot.angeluz.authorization.AutorizacionForm;
        import com.juan.springboot.angeluz.authorization.AutorizacionFormRepository;
        import com.juan.springboot.angeluz.forms.EntryForm;
        import com.juan.springboot.angeluz.forms.EntryFormRepository;
        import com.juan.springboot.angeluz.forms.Mascota;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.format.annotation.DateTimeFormat;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.*;
        import org.springframework.web.bind.support.SessionStatus;

        import java.time.LocalDate;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Optional;

@Controller
@SessionAttributes("entryForm")
        public class EntryFormController {
            @Autowired
            private AutorizacionFormRepository autorizacionRepo;

            @Autowired
            private EntryFormRepository entryFormRepository;

            @ModelAttribute("entryForm")
            public EntryForm setupEntryForm() {
                return new EntryForm();
            }

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
                    entryForm.setMascotas(new ArrayList<>());
                    entryForm.getMascotas().add(new Mascota());
                }

                model.addAttribute("entryForm", entryForm);
                return "mascotas";
            }

            // Guardar formulario completo
            @PostMapping("/moderador/mascotas")
            public String guardarFormularioCompleto(
                    @ModelAttribute("entryForm") EntryForm entryForm,
                    SessionStatus sessionStatus) {

                // … guardas entryForm y mascotas …
                entryFormRepository.save(entryForm);


                // ahora iba a EntradasYSalidas, cámbialo:
                return "redirect:/moderador/autorizacion";
            }


            // Listar registros
            @GetMapping("/moderador/EntradasYSalidas")
            public String listarRegistros(Model model) {
                List<EntryForm> registros = entryFormRepository.findAll();
                model.addAttribute("registros", registros);
                return "moderador/EntradasYSalidas";
            }

            // Buscar registro por ID
            @GetMapping("/moderador/EntradasYSalidas/buscar")
            public String buscarRegistroPorId(@RequestParam("id") Long id, Model model) {
                Optional<EntryForm> registro = entryFormRepository.findById(id);
                if (registro.isPresent()) {
                    model.addAttribute("registro", registro.get());
                } else {
                    model.addAttribute("error", "No se encontró un registro con el ID proporcionado.");
                }
                return "moderador/EntradasYSalidas";
            }

            // Mostrar formulario de edición
            @GetMapping("/moderador/EntradasYSalidas/editar/{id}")
            public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model) {
                Optional<EntryForm> registro = entryFormRepository.findById(id);
                if (registro.isPresent()) {
                    model.addAttribute("registro", registro.get());
                    return "moderador/EditarRegistro";
                } else {
                    model.addAttribute("error", "No se encontró un registro con el ID proporcionado.");
                    return "redirect:/moderador/EntradasYSalidas";
                }
            }


    // Procesar edición de registro
            @PostMapping("/moderador/EntradasYSalidas/editar/{id}")
            public String procesarEdicion(@PathVariable("id") Long id, @ModelAttribute EntryForm registroActualizado) {
                Optional<EntryForm> registro = entryFormRepository.findById(id);
                if (registro.isPresent()) {
                    EntryForm existente = registro.get();
                    existente.setNombrePropietario(registroActualizado.getNombrePropietario());
                    existente.setCorreo(registroActualizado.getCorreo());
                    // Actualiza otros campos según sea necesario
                    entryFormRepository.save(existente);
                }
                return "redirect:/moderador/EntradasYSalidas";
            }

            // Eliminar registro
            @GetMapping("/moderador/EntradasYSalidas/eliminar/{id}")
            public String eliminarRegistro(@PathVariable("id") Long id) {
                entryFormRepository.deleteById(id);
                return "redirect:/moderador/EntradasYSalidas";
            }


            @GetMapping("/moderador/EntradasYSalidas/filtrar")
            public String filtrarPorFechaYBusqueda(
                    @RequestParam(value = "startDate", required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                    @RequestParam(value = "endDate", required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                    @RequestParam(value = "search", required = false) String search,
                    Model model) {

                // 1. Obtener todos los registros
                List<EntryForm> todos = entryFormRepository.findAll();

                // 2. Filtrar por rango de fechas si ambos están presentes
                List<EntryForm> filtradosPorFecha = todos.stream()
                        .filter(r -> {
                            if (startDate != null && endDate != null && r.getFechaInicio() != null && r.getFechaFin() != null) {
                                return !r.getFechaInicio().isAfter(endDate) && !r.getFechaFin().isBefore(startDate);
                            }
                            return true; // si no hay fechas, dejamos pasar todo
                        })
                        .toList();

                // 3. Filtrar por el término de búsqueda si se proporcionó
                List<EntryForm> filtrados = filtradosPorFecha.stream()
                        .filter(r -> {
                            if (search == null || search.isBlank()) return true;
                            String s = search.toLowerCase();
                            return String.valueOf(r.getId()).contains(s)
                                    || (r.getNombrePropietario() != null && r.getNombrePropietario().toLowerCase().contains(s))
                                    || (r.getCorreo() != null && r.getCorreo().toLowerCase().contains(s));

                        })
                        .toList();

                // 4. Separar en “con fecha” y “sin fecha”
                List<EntryForm> registrosConFecha = filtrados.stream()
                        .filter(r -> r.getFechaInicio() != null && r.getFechaFin() != null)
                        .toList();
                List<EntryForm> registrosSinFecha = filtrados.stream()
                        .filter(r -> r.getFechaInicio() == null || r.getFechaFin() == null)
                        .toList();

                // 5. Pasar al modelo
                model.addAttribute("registrosConFecha", registrosConFecha);
                model.addAttribute("registrosSinFecha", registrosSinFecha);
                // También devolvemos el propio término de búsqueda
                model.addAttribute("search", search);
                model.addAttribute("startDate", startDate);
                model.addAttribute("endDate", endDate);

                return "moderador/EntradasYSalidas";
            }

        }