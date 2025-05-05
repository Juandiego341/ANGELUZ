package com.juan.springboot.angeluz.User.Controllers;

import com.juan.springboot.angeluz.forms.EntryForm;
import com.juan.springboot.angeluz.forms.EntryFormService;
import com.juan.springboot.angeluz.forms.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {

    private final EntryFormService entryFormService;
    private final MascotaRepository mascotaRepository;

    @Autowired
    public AdminController(EntryFormService entryFormService, MascotaRepository mascotaRepository) {
        this.entryFormService = entryFormService;
        this.mascotaRepository = mascotaRepository;
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "admin/home";
    }

    @GetMapping("/admin/mascotas")
    public String adminMascotas() {
        return "admin/mascotas_admin";
    }



    @GetMapping("/admin/reportes")
    public String mostrarReportes(Model model) {
        try {
            // Obtener estadísticas
            long totalMascotas = mascotaRepository.count();
            int totalCitas = entryFormService.contarCitasActivas();
            double ingresosTotales = entryFormService.calcularIngresosTotales();
            List<EntryForm> entradas = entryFormService.getAllEntries();

            // Agregar atributos al modelo
            model.addAttribute("totalMascotas", totalMascotas);
            model.addAttribute("totalCitas", totalCitas);
            model.addAttribute("ingresosTotales", ingresosTotales);
            model.addAttribute("entradas", entradas);

            return "admin/reportes";
        } catch (Exception e) {
            // Log del error
            e.printStackTrace();
            // Agregar mensaje de error al modelo
            model.addAttribute("error", "Error al cargar los reportes: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/reportes/generar")
    public ResponseEntity<ByteArrayResource> generarReporte(
            @RequestParam("tipo") String tipo,
            @RequestParam("formato") String formato) {

        byte[] reporte = entryFormService.generarReporte(tipo, formato);
        String nombreArchivo = "reporte_" + tipo + "." + formato.toLowerCase();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + nombreArchivo)
                .body(new ByteArrayResource(reporte));
    }
}