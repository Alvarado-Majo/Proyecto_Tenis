package com.ProyectoTenis.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.repository.ClienteRepository;

import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Registrar nuevo cliente
     */
    public boolean registrarCliente(Cliente cliente) {
        Optional<Cliente> existente = clienteRepository.findByCorreo(cliente.getCorreo());
        if (existente.isPresent()) {
            return false;
        }

        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        clienteRepository.save(cliente);
        return true;
    }

    /**
     * Alias para el controlador antiguo
     */
    public boolean registrar(Cliente cliente) {
        return registrarCliente(cliente);
    }

    /**
     * Actualizar datos del cliente
     */
    public Cliente actualizar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    /**
     * Validación de login
     */
    public boolean validarLogin(String correo, String password) {
        Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(correo);
        if (clienteOpt.isEmpty()) {
            return false;
        }

        Cliente cliente = clienteOpt.get();
        return passwordEncoder.matches(password, cliente.getPassword());
    }

    /**
     * Buscar cliente por correo
     */
    public Optional<Cliente> buscarPorCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    /**
     * Buscar cliente por ID
     */
    public Optional<Cliente> buscarPorId(Long idCliente) {
        return clienteRepository.findById(idCliente);
    }
}
