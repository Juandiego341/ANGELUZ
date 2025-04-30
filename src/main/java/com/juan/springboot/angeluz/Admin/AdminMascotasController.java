package com.juan.springboot.angeluz.Admin;

import com.juan.springboot.angeluz.forms.Mascota;
import com.juan.springboot.angeluz.forms.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/mascotas")
public class AdminMascotasController {

    @Autowired
    private MascotaRepository mascotaRepository;
    @GetMapping("/fichas")
    public String listarFichasTecnicas(@RequestParam(value = "raza", required = false) String filtroRaza, Model model) {
        List<Mascota> mascotas;
        if (filtroRaza != null && !filtroRaza.isEmpty()) {
            mascotas = mascotaRepository.findByRaza(filtroRaza); // Asumiendo que tienes un método así en tu repositorio
        } else {
            mascotas = mascotaRepository.findAll();
        }
        model.addAttribute("mascotas", mascotas);
        model.addAttribute("filtroRaza", filtroRaza); // Pasar el filtro actual al modelo para mantener la selección
        model.addAttribute("razas", mascotaRepository.findDistinctRazas()); // Necesitas un método para obtener todas las razas únicas
        return "admin/mascotas/fichas";
    }
    @GetMapping("/fichas/{id}")
    public String verFichaTecnica(@PathVariable Long id, Model model) {
        Optional<Mascota> mascotaOptional = mascotaRepository.findById(id);
        if (mascotaOptional.isPresent()) {
            model.addAttribute("mascota", mascotaOptional.get());
            return "admin/mascotas/ficha-detalle";
        } else {
            return "error";
        }
    }
}