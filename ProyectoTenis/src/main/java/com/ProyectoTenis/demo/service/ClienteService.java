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

    // Codificador de contraseñas seguro
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Registra un nuevo cliente si el correo no existe.
     * La contraseña se cifra antes de guardarse.
     */
    public boolean registrarCliente(Cliente cliente) {
        Optional<Cliente> existente = clienteRepository.findByCorreo(cliente.getCorreo());
        if (existente.isPresent()) {
            // Ya existe un cliente con este correo
            return false;
        }
        // Cifrar contraseña
        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        clienteRepository.save(cliente);
        return true;
    }

    /**
     * Valida el inicio de sesión de un cliente.
     * Compara el correo y la contraseña (encriptada).
     */
    public boolean validarLogin(String correo, String password) {
        Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(correo);
        if (clienteOpt.isEmpty()) {
            return false;
        }

        Cliente cliente = clienteOpt.get();
        // Verificar si la contraseña coincide con la encriptada
        return passwordEncoder.matches(password, cliente.getPassword());
    }

    /**
     * Busca un cliente por su correo electrónico.
     */
    public Optional<Cliente> buscarPorCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    /**
     * Lista o busca cliente por ID (útil en perfil, carrito, etc.)
     */
    public Optional<Cliente> buscarPorId(Integer idCliente) {
        return clienteRepository.findById(idCliente);
    }
}
