package com.juan.springboot.angeluz.User.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class AdminController {
    @GetMapping("/admin/home")
    public String adminHome() {
        return "admin/home"; // El nombre del archivo de la vista para el administrador
    }
}
