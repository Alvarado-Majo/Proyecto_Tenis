package com.ProyectoTenis.demo.controller;

import com.ProyectoTenis.demo.domain.Administrador;
import com.ProyectoTenis.demo.service.AdministradorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminLoginController {

    private final AdministradorService administradorService;

    public AdminLoginController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping("/admin/login")
    public String mostrarLoginAdmin(Model model) {
        return "admin/login_admin";  
    }

    @PostMapping("/admin/login")
    public String procesarLoginAdmin(@RequestParam String email,
                                     @RequestParam String password,
                                     HttpSession session,
                                     RedirectAttributes ra) {

        Administrador admin = administradorService.buscarPorEmail(email);

        if (admin == null || !admin.getPassword().equals(password)) {
            ra.addFlashAttribute("error", "Correo o contraseña incorrectos");
            return "redirect:/admin/login";
        }

        session.setAttribute("admin", admin);
        ra.addFlashAttribute("ok", "Bienvenido, " + admin.getEmail());
        return "redirect:/admin/tenis";
    }

    @GetMapping("/admin/logout")
    public String logoutAdmin(HttpSession session, RedirectAttributes ra) {
        session.removeAttribute("admin");
        ra.addFlashAttribute("ok", "Sesión de administrador cerrada.");
        return "redirect:/";
    }
}
