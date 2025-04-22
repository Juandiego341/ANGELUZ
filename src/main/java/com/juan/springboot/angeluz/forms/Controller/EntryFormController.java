package com.juan.springboot.angeluz.forms.Controller;

    import com.juan.springboot.angeluz.forms.EntryForm;
    import com.juan.springboot.angeluz.forms.EntryFormRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.ModelAttribute;
    import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EntryFormController {

    @Autowired
    private EntryFormRepository entryFormRepository;

    // Mostrar el formulario inicial
    @GetMapping("/moderador/checkout")
    public String mostrarFormularioCheckout(Model model) {
        model.addAttribute("entryForm", new EntryForm());
        return "checkout";
    }

    // Procesar el formulario inicial y redirigir al formulario de mascotas
    @PostMapping("/moderador/checkout")
    public String procesarFormularioCheckout(@ModelAttribute("entryForm") EntryForm entryForm, Model model) {
        model.addAttribute("entryForm", entryForm); // Pasar los datos al siguiente formulario
        return "mascotas"; // Redirigir al formulario de mascotas
    }

    // Guardar toda la información (incluyendo las mascotas)
    @PostMapping("/moderador/mascotas")
    public String guardarFormularioCompleto(@ModelAttribute("entryForm") EntryForm entryForm) {
        entryFormRepository.save(entryForm); // Guardar toda la información
        return "redirect:/moderador/EntradasYSalidas";
    }
}