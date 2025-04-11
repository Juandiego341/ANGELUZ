package com.juan.springboot.angeluz.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Página de login
    }
    @GetMapping("/register")
    public String register() {
        return "register"; // Página de registro
    }
}