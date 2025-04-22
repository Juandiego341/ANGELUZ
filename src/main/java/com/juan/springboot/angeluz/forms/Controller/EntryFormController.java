package com.juan.springboot.angeluz.forms.Controller;

        import com.juan.springboot.angeluz.forms.EntryForm;
        import com.juan.springboot.angeluz.forms.EntryFormRepository;
        import com.juan.springboot.angeluz.forms.Mascota;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.*;

        import java.util.List;
        import java.util.Optional;

        @Controller
        @SessionAttributes("entryForm")
        public class EntryFormController {

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
                    entryForm.setMascotas(List.of(new Mascota())); // Inicializa la lista de mascotas
                }

                model.addAttribute("entryForm", entryForm);
                return "mascotas";
            }

            // Guardar formulario completo
            @PostMapping("/moderador/mascotas")
            public String guardarFormularioCompleto(@ModelAttribute("entryForm") EntryForm entryForm, Model model) {
                if (entryForm.getMascotas() != null) {
                    for (Mascota mascota : entryForm.getMascotas()) {
                        mascota.setEntryForm(entryForm); // Asigna el EntryForm a cada Mascota
                    }
                }

                entryFormRepository.save(entryForm);
                return "redirect:/moderador/entradasysalidas";
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
        }