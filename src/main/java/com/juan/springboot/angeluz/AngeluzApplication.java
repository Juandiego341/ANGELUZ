package com.juan.springboot.angeluz;

import com.juan.springboot.angeluz.User.User;
import com.juan.springboot.angeluz.User.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AngeluzApplication {

	public static void main(String[] args) {
		SpringApplication.run(AngeluzApplication.class, args);
	}

	@Bean
	public CommandLineRunner crearUsuariosPorDefecto(UserRepository userRepository, PasswordEncoder encoder) {
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {
				User admin = new User();
				admin.setNombre("admin");
				admin.setUsername("admin");
				admin.setEmail("admin@angeluz.com");
				admin.setPassword(encoder.encode("admin123"));
				admin.setRole("ROLE_ADMIN");
				userRepository.save(admin);
			}

			if (userRepository.findByUsername("moderador").isEmpty()) {
				User mod = new User();
				mod.setNombre("moderador");
				mod.setUsername("moderador");
				mod.setEmail("mod@angeluz.com");
				mod.setPassword(encoder.encode("mod123"));
				mod.setRole("ROLE_MODERATOR");
				userRepository.save(mod);
			}
		};
	}
}