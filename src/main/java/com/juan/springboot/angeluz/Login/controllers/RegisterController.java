package com.juan.springboot.angeluz.Login.controllers;

import com.juan.springboot.angeluz.User.User;
import com.juan.springboot.angeluz.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RegisterController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new User());
        return "register"; // Asegúrate de que este nombre coincida con tu vista
    }

    @PostMapping("/register")
    public String registrarUsuario(@RequestParam String nombre,  // Asegúrate de que el nombre se envíe
                                   @RequestParam String username,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam String confirmPassword,
                                   Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "register";
        }

        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "El nombre de usuario ya existe");
            return "register";
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            model.addAttribute("error", "El nombre no puede estar vacío");
            return "register";
        }

        User nuevoUsuario = new User();
        nuevoUsuario.setNombre(nombre);  // Asigna el valor de nombre
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPassword(passwordEncoder.encode(password));
        nuevoUsuario.setRole("ROLE_USER"); // Siempre se crea como user

        userRepository.save(nuevoUsuario);

        return "redirect:/login";
    }
}
