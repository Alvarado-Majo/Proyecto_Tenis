package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public Cliente getClienteByCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    @Transactional
    public void save(Cliente cliente) {
        clienteRepository.save(cliente);
    }
}
