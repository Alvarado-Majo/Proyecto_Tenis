package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.repository.ClienteRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Cliente guardar(Cliente cliente) {
        if (cliente.getPassword() != null && !cliente.getPassword().isBlank()) {
            cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        }
        return clienteRepository.save(cliente);
    }

    // Compatibilidad con código viejo
    public boolean registrar(Cliente cliente) {
        guardar(cliente);
        return true;
    }

    public Cliente actualizar(Cliente cliente) {
        return guardar(cliente);
    }

    public Cliente buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email).orElse(null);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    // 👇 Nuevo método para validar correos duplicados
    public boolean existeEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return clienteRepository.findByEmail(email).isPresent();
    }
}
