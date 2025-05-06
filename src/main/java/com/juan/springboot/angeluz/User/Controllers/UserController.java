package com.juan.springboot.angeluz.User.Controllers;

import com.juan.springboot.angeluz.User.User;
import com.juan.springboot.angeluz.User.UserRepository;
import com.juan.springboot.angeluz.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private UserRepository userRepository;
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
    public String editarPerfil(@ModelAttribute("user") User user,
                               Authentication authentication,
                               HttpServletRequest request) {
        // Obtener usuario actual
        String username = authentication.getName();
        User usuarioActual = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Actualizar datos
        usuarioActual.setNombre(user.getNombre());
        usuarioActual.setEmail(user.getEmail());

        // Si el username cambia, actualizar con cuidado
        boolean usernameChanged = !usuarioActual.getUsername().equals(user.getUsername());
        if (usernameChanged) {
            // Verificar que el nuevo username no exista
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new RuntimeException("El nombre de usuario ya existe");
            }
            usuarioActual.setUsername(user.getUsername());
        }

        // Guardar cambios
        userRepository.save(usuarioActual);

        // Si el username cambió, necesitamos reautenticar
        if (usernameChanged) {
            // Realizar logout
            SecurityContextHolder.clearContext();
            request.getSession().invalidate();
            return "redirect:/login";
        }

        return "redirect:/perfil";
    }
}

