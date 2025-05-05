package com.juan.springboot.angeluz.Moderador;

import com.juan.springboot.angeluz.forms.EntryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CitaController {

    @Autowired
    private CitaRepository citaRepository;

    @GetMapping("/moderador/usuarios")
    public String listarCitas(Model model) {
        List<EntryForm> citas = citaRepository.findAll();

        // Verifica que los valores totales no sean nulos
        for (EntryForm cita : citas) {
            if (cita.getValorTotal() == null) {
                cita.setValorTotal(0.0); // Valor por defecto si es nulo
            }
        }

        model.addAttribute("citas", citas);
        return "moderador/citas";
    }
}
