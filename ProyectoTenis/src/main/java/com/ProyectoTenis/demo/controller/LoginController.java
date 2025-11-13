package com.ProyectoTenis.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import practica.practica.domain.Administrador;
import practica.practica.domain.Cliente;
import practica.practica.service.AdministradorService;
import practica.practica.service.ClienteService;

import java.util.Optional;

@Controller
@SessionAttributes({"cliente", "admin"})
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private AdministradorService administradorService;

    /**
     * Muestra el formulario de inicio de sesión.
     */
    @GetMapping
    public String mostrarLogin(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("admin", new Administrador());
        return "login"; // plantilla login.html
    }

    /**
     * Procesa el inicio de sesión para cliente.
     */
    @PostMapping("/cliente")
    public String loginCliente(@ModelAttribute Cliente cliente,
                               Model model,
                               RedirectAttributes ra) {
        Optional<Cliente> clienteOpt = clienteService.buscarPorCorreoYPassword(
                cliente.getCorreo(), cliente.getPassword());

        if (clienteOpt.isPresent()) {
            model.addAttribute("cliente", clienteOpt.get());
            ra.addFlashAttribute("ok", "Bienvenido, " + clienteOpt.get().getNombre());
            return "redirect:/";
        }

        ra.addFlashAttribute("error", "Correo o contraseña incorrectos.");
        return "redirect:/login";
    }

    /**
     * Procesa el inicio de sesión para administrador.
     */
    @PostMapping("/admin")
    public String loginAdmin(@ModelAttribute Administrador admin,
                             Model model,
                             RedirectAttributes ra) {
        Optional<Administrador> adminOpt = administradorService.buscarPorUsuarioYPassword(
                admin.getUsuario(), admin.getPassword());

        if (adminOpt.isPresent()) {
            model.addAttribute("admin", adminOpt.get());
            ra.addFlashAttribute("ok", "Administrador " + adminOpt.get().getUsuario() + " conectado.");
            return "redirect:/admin/dashboard";
        }

        ra.addFlashAttribute("error", "Credenciales de administrador inválidas.");
        return "redirect:/login";
    }

    /**
     * Cierra la sesión de cliente o administrador.
     */
    @GetMapping("/logout")
    public String cerrarSesion(SessionStatus status, RedirectAttributes ra) {
        status.setComplete();
        ra.addFlashAttribute("ok", "Sesión cerrada correctamente.");
        return "redirect:/";
    }
}
