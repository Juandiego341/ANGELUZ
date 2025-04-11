package com.juan.springboot.angeluz.config;

                import org.springframework.context.annotation.Bean;
                import org.springframework.context.annotation.Configuration;
                import org.springframework.security.config.annotation.web.builders.HttpSecurity;
                import org.springframework.security.core.userdetails.User;
                import org.springframework.security.core.userdetails.UserDetailsService;
                import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
                import org.springframework.security.crypto.password.PasswordEncoder;
                import org.springframework.security.provisioning.InMemoryUserDetailsManager;
                import org.springframework.security.web.SecurityFilterChain;

                @Configuration
                public class SecurityConfig {

                    @Bean
                    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                        http
                            .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/", "/register", "/css/**", "/js/**").permitAll() // Permitir acceso a estas rutas
                                .anyRequest().authenticated() // Requiere autenticación para otras rutas
                            )
                            .formLogin(form -> form
                                .loginPage("/login") // Página personalizada de login
                                .defaultSuccessUrl("/") // Redirigir al inicio tras login exitoso
                                .permitAll()
                            )
                            .logout(logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login") // Redirigir al login tras logout
                                .permitAll()
                            );
                        return http.build();
                    }

                    @Bean
                    public UserDetailsService userDetailsService() {
                        // Usuario en memoria para pruebas
                        return new InMemoryUserDetailsManager(
                            User.withDefaultPasswordEncoder()
                                .username("juan")
                                .password("1234")
                                .roles("USER")
                                .build()
                        );
                    }
                    @Bean
                    public PasswordEncoder passwordEncoder() {
                        return new BCryptPasswordEncoder();
                    }
                }