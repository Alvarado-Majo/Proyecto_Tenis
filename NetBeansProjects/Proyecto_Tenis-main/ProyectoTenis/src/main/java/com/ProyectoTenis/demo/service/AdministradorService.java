package com.ProyectoTenis.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ProyectoTenis.demo.domain.Administrador;
import com.ProyectoTenis.demo.repository.AdministradorRepository;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean registrar(Administrador admin) {

        Administrador existente = administradorRepository.findByUsuario(admin.getUsuario());

        if (existente != null) {
            return false; 
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        administradorRepository.save(admin);
        return true;
    }

    public boolean validarLogin(String usuario, String password) {

        Administrador adminBD = administradorRepository.findByUsuario(usuario);

        if (adminBD == null) {
            return false;
        }

        return passwordEncoder.matches(password, adminBD.getPassword());
    }

    public Administrador buscarPorUsuario(String usuario) {
        return administradorRepository.findByUsuario(usuario);
    }

    public Administrador buscarPorId(Long id) {
        return administradorRepository.findById(id).orElse(null);
    }
}
