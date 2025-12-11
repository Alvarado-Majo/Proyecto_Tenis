package com.ProyectoTenis.demo.controller;

import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "registro";   // templates/registro.html
    }

    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("cliente") Cliente cliente,
                                   BindingResult result,
                                   Model model) {

        if (result.hasErrors()) {
            return "registro";
        }

        clienteService.guardar(cliente);
        return "redirect:/login?nuevo";
    }

    // opcional
    @GetMapping("/perfil")
    public String verPerfil() {
        return "cliente/perfil";
    }
}
