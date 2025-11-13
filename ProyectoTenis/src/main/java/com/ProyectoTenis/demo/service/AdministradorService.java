package com.ProyectoTenis.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ProyectoTenis.demo.domain.Administrador;
import com.ProyectoTenis.demo.repository.AdministradorRepository;
import java.util.Optional;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    // Instancia del codificador de contraseñas
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Registra un nuevo administrador si el usuario no existe.
     * La contraseña se almacena de forma cifrada.
     */
    public boolean registrarAdministrador(Administrador admin) {
        Optional<Administrador> existente = administradorRepository.findByUsuario(admin.getUsuario());
        if (existente.isPresent()) {
            // Ya existe un administrador con este nombre de usuario
            return false;
        }
        // Cifrar contraseña antes de guardar
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        administradorRepository.save(admin);
        return true;
    }

    /**
     * Valida las credenciales de inicio de sesión del administrador.
     * Compara la contraseña ingresada con la versión cifrada almacenada.
     */
    public boolean validarLogin(String usuario, String password) {
        Optional<Administrador> adminOpt = administradorRepository.findByUsuario(usuario);
        if (adminOpt.isEmpty()) {
            return false;
        }

        Administrador admin = adminOpt.get();
        // Compara contraseñas usando BCrypt
        return passwordEncoder.matches(password, admin.getPassword());
    }

    /**
     * Busca un administrador por su nombre de usuario.
     */
    public Optional<Administrador> buscarPorUsuario(String usuario) {
        return administradorRepository.findByUsuario(usuario);
    }

    /**
     * Obtiene un administrador por su ID.
     */
    public Optional<Administrador> buscarPorId(Integer id) {
        return administradorRepository.findById(id);
    }

    /**
     * Lista todos los administradores registrados (opcional).
     */
    public Iterable<Administrador> listarAdministradores() {
        return administradorRepository.findAll();
    }
}
