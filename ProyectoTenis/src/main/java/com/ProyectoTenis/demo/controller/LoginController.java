package com.ProyectoTenis.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginCliente(Model model) {
        model.addAttribute("tipoLogin", "cliente");
        return "/publico/login";  
    }

    @GetMapping("/admin/login")
    public String loginAdmin(Model model) {
        model.addAttribute("tipoLogin", "admin");
        return "/admin/login"; 
    }

    @GetMapping("/logout-success")
    public String logoutExitoso() {
        return "/publico/logout-success"; // si querés una vista de cierre de sesión
    }
}
