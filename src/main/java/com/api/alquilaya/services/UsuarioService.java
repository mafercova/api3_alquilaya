package com.api.alquilaya.services;

import com.api.alquilaya.models.UsuariosModel;
import com.api.alquilaya.repositories.IUsuariosRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final IUsuariosRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(IUsuariosRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuariosModel crearUsuario(String email, String password, String rol) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (rol == null || rol.isBlank()) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        String emailNormalizado = email.trim();
        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        String encodedPassword = passwordEncoder.encode(password);
        UsuariosModel usuario = new UsuariosModel();
        usuario.setEmail(emailNormalizado);
        usuario.setPassword(encodedPassword);
        usuario.setRol(rol.trim().toUpperCase(Locale.ROOT));
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }
}
