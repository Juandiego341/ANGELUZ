package com.juan.springboot.angeluz.User.Controllers;

import com.juan.springboot.angeluz.User.User;
import com.juan.springboot.angeluz.User.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/perfil")
public class UserController {

    @Autowired
    private UserService userService;

    // Mostrar perfil
    @GetMapping
    public String verPerfil(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Optional<User> usuarioOpt = userService.findByUsername(username);

            if (usuarioOpt.isPresent()) {
                model.addAttribute("usuario", usuarioOpt.get());
            } else {
                return "redirect:/login";
            }
        }
        return "perfil";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar")
    public String mostrarFormularioEdicion(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("user", user); // Usado por editarPerfil.html
        return "editarPerfil";
    }

    // Procesar edición
    @PostMapping("/editar")
    public String actualizarPerfil(@Valid @ModelAttribute("user") User user, BindingResult result, Authentication authentication) {
        if (result.hasErrors()) {
            return "editarPerfil";  // Regresa a la página de edición si hay errores
        }

        String username = authentication.getName();
        User existingUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        existingUser.setNombre(user.getNombre());
        existingUser.setEmail(user.getEmail());
        existingUser.setUsername(user.getUsername());

        userService.saveUser(existingUser);
        return "redirect:/perfil";
    }
}
