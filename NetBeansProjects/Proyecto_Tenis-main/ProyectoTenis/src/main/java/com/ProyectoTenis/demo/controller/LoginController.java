package com.ProyectoTenis.demo.controller;

import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.domain.Administrador;
import com.ProyectoTenis.demo.service.ClienteService;
import com.ProyectoTenis.demo.service.AdministradorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.support.SessionStatus;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
@SessionAttributes({"clienteSesion", "adminSesion"})
public class LoginController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private AdministradorService administradorService;

    /**
     * Formulario de login
     */
    @GetMapping("")
    public String loginForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("admin", new Administrador());
        return "login";
    }

    /**
     * Login de cliente
     */
    @PostMapping("/cliente")
    public String loginCliente(
            @ModelAttribute Cliente cliente,
            Model model
    ) {

        boolean valido = clienteService.validarLogin(cliente.getCorreo(), cliente.getPassword());

        if (!valido) {
            model.addAttribute("errorCliente", "Correo o contraseña incorrectos.");
            return "login";
        }

        Cliente clienteBD = clienteService.buscarPorCorreo(cliente.getCorreo()).get();

        // Guardar en sesión
        model.addAttribute("clienteSesion", clienteBD);

        return "redirect:/";
    }

    /**
     * Login de administrador
     */
    @PostMapping("/admin")
    public String loginAdmin(
            @ModelAttribute Administrador admin,
            Model model
    ) {

        boolean valido = administradorService.validarLogin(admin.getUsuario(), admin.getPassword());

        if (!valido) {
            model.addAttribute("errorAdmin", "Usuario o contraseña incorrectos.");
            return "login";
        }

        Administrador adminBD = administradorService.buscarPorUsuario(admin.getUsuario());
        model.addAttribute("adminSesion", adminBD);


        return "redirect:/admin";
    }

    /**
     * Logout general
     */
    @GetMapping("/logout")
    public String logout(SessionStatus status, HttpSession session) {
        status.setComplete();
        session.invalidate();
        return "redirect:/login";
    }
}
