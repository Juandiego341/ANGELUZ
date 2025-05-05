package com.juan.springboot.angeluz.User.Controllers;

import com.juan.springboot.angeluz.forms.Mascota;
import com.juan.springboot.angeluz.forms.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class ModeratorController {

    @GetMapping("/moderador/home")
    public String moderatorHome() {
        return "moderador/home"; // El nombre del archivo de la vista para el moderador
    }

    @GetMapping("/moderador/entradasysalidas")
    public String entradasysalidas() {
        return "moderador/EntradasYSalidas"; // El nombre del archivo de la vista para el moderador
    }
    @GetMapping("/moderador/mascota")
    public String mascotas() {
        return "moderador/mascotas";
    }
    @GetMapping("/moderador/mascotas/fichas")
    public String mascotasFichas() {
        return "moderador/mascotas/fichas_tecnicas";
    }
    @Autowired
    private MascotaRepository mascotaRepository;
    @GetMapping("/fichas")
    public String listarFichasTecnicas(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "raza", required = false) String filtroRaza,
            Model model) {

        List<Mascota> mascotas;

        if (nombre != null && !nombre.trim().isEmpty()) {
            // Búsqueda por nombre
            mascotas = mascotaRepository.findByNombreContainingIgnoreCase(nombre);
        } else if (filtroRaza != null && !filtroRaza.isEmpty()) {
            // Filtro por raza
            mascotas = mascotaRepository.findByRaza(filtroRaza);
        } else {
            // Sin filtros
            mascotas = mascotaRepository.findAll();
        }

        model.addAttribute("mascotas", mascotas);
        model.addAttribute("filtroRaza", filtroRaza);
        model.addAttribute("razas", mascotaRepository.findDistinctRazas());
        return "moderador/mascotas/fichas_tecnicas";
    }
    @GetMapping("/fichas/{id}")
    public String verFichaTecnica(@PathVariable Long id, Model model) {
        Optional<Mascota> mascotaOptional = mascotaRepository.findById(id);
        if (mascotaOptional.isPresent()) {
            model.addAttribute("mascota", mascotaOptional.get());
            return "moderador/mascotas/ficha-detalle";
        } else {
            return "error";
        }
    }
}
