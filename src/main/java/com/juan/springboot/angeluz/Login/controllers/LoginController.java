package com.juan.springboot.angeluz.login.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class LoginController {

    @GetMapping("/custom-login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model,
                        Principal principal,
                        RedirectAttributes flash) {

        if (principal != null) {
            flash.addFlashAttribute("info", "Ya ha iniciado sesión anteriormente");
            return "redirect:/";
        }

        if (error != null) {
            model.addAttribute("error",
                    "Nombre de usuario y/o contraseña es incorrecta, por favor vuelva a intentarlo");
        }

        if (logout != null) {
            model.addAttribute("success",
                    "Ha cerrado sesión con éxito!");
        }

        return "login";
    }
}