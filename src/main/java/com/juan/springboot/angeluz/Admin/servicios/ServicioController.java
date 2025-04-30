package com.juan.springboot.angeluz.Admin.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/servicios")
public class ServicioController {

    @Autowired
    private ServicioRepositorio servicioRepositorio;

    // Mostrar lista de servicios
    @GetMapping
    public String listarServicios(Model modelo) {
        modelo.addAttribute("servicios", servicioRepositorio.findAll());
        return "admin/servicios"; // Nombre de la vista (servicios.html)
    }

    // Mostrar formulario de nuevo servicio
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model modelo) {
        modelo.addAttribute("servicio", new Servicio());
        return "admin/formulario-servicio"; // Vista para agregar o editar
    }

    // Guardar nuevo servicio
    @PostMapping("/guardar")
    public String guardarServicio(@ModelAttribute("servicio") Servicio servicio) {
        servicioRepositorio.save(servicio);
        return "redirect:/admin/servicios";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model modelo) {
        Optional<Servicio> servicio = servicioRepositorio.findById(id);
        if (servicio.isPresent()) {
            modelo.addAttribute("servicio", servicio.get());
            return "admin/formulario-servicio";
        } else {
            return "redirect:/admin/servicios";
        }
    }

    // Eliminar servicio
    @GetMapping("/eliminar/{id}")
    public String eliminarServicio(@PathVariable Long id) {
        servicioRepositorio.deleteById(id);
        return "redirect:/admin/servicios";
    }
}
