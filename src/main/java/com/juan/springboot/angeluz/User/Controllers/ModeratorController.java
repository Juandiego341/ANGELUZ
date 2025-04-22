package com.juan.springboot.angeluz.User.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
}
