package com.juan.springboot.angeluz.User.Controllers;

import com.juan.springboot.angeluz.User.User;
import com.juan.springboot.angeluz.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/perfil")
    public String verPerfil(Model model, Principal principal) {
        if (principal != null) {
            // Obtener el nombre de usuario autenticado (en lugar del correo)
            String username = principal.getName();
            System.out.println("Usuario autenticado: " + username); // Depuración

            // Buscar el usuario por nombre de usuario
            Optional<User> usuarioOpt = userService.findByUsername(username);

            // Verificar si el usuario está presente
            if (usuarioOpt.isPresent()) {
                User usuario = usuarioOpt.get();  // Obtener el usuario
                System.out.println("Usuario encontrado: " + usuario.getUsername()); // Depuración
                model.addAttribute("usuario", usuario);
            } else {
                System.out.println("Usuario no encontrado en la base de datos"); // Depuración
                return "redirect:/login";
            }
        }
        return "perfil";
    }

}