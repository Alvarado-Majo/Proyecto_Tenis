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

    // BCrypt para encriptar contraseñas
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Registrar un nuevo administrador (nombre de método acorde a tus controladores).
     */
    public boolean registrar(Administrador admin) {
        Optional<Administrador> existente = administradorRepository.findByUsuario(admin.getUsuario());
        if (existente.isPresent()) {
            return false; // Usuario ya existe
        }

        // Encriptar contraseña
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        administradorRepository.save(admin);
        return true;
    }

    /**
     * Validar credenciales del administrador.
     */
    public boolean validarLogin(String usuario, String password) {
        Optional<Administrador> adminOpt = administradorRepository.findByUsuario(usuario);
        if (adminOpt.isEmpty()) {
            return false;
        }

        Administrador adminBD = adminOpt.get();
        return passwordEncoder.matches(password, adminBD.getPassword());
    }

    /**
     * Buscar administrador por usuario.
     */
    public Optional<Administrador> buscarPorUsuario(String usuario) {
        return administradorRepository.findByUsuario(usuario);
    }

    /**
     * Buscar por ID (Long según tu modelo).
     */
    public Optional<Administrador> buscarPorId(Long id) {
        return administradorRepository.findById(id);
    }

    /**
     * Listar todos los administradores.
     */
    public Iterable<Administrador> listarAdministradores() {
        return administradorRepository.findAll();
    }
}
