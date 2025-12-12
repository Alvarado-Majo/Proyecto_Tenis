package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.Administrador;
import com.ProyectoTenis.demo.repository.AdministradorRepository;
import org.springframework.stereotype.Service;

@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;

    public AdministradorService(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    public Administrador buscarPorEmail(String email) {
        return administradorRepository.findByEmail(email);
    }
}
