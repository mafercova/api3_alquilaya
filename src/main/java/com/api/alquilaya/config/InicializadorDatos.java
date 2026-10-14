package com.api.alquilaya.config;

import com.api.alquilaya.repositories.IUsuariosRepository;
import com.api.alquilaya.services.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(name = "app.seed-users.enabled", havingValue = "true")
public class InicializadorDatos {
    @Bean
    public CommandLineRunner crearUsuarios(
        IUsuariosRepository usuarioRepository,
        UsuarioService usuarioService,
        @Value("${app.admin.email}") String email,
        @Value("${app.admin.password}") String password,
        @Value("${app.operador.email}") String operadorEmail,
        @Value("${app.operador.password}") String operadorPassword
    ) {
        return args -> {
            if (!usuarioRepository.existsByEmail(email)) {
                usuarioService.crearUsuario(email, password, "ADMIN");
            }
            if (!usuarioRepository.existsByEmail(operadorEmail)) {
                usuarioService.crearUsuario(operadorEmail, operadorPassword, "OPERADOR");
            }
        };
    }
}
