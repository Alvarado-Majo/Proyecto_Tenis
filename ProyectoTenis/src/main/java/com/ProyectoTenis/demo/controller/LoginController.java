package com.ProyectoTenis.demo.controller;

import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final ClienteService clienteService;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginController(ClienteService clienteService,
                           BCryptPasswordEncoder passwordEncoder) {
        this.clienteService = clienteService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model,
                               @RequestParam(value = "nuevo", required = false) String nuevo) {
        if (nuevo != null) {
            model.addAttribute("ok", "Tu cuenta se creó correctamente. Ahora inicia sesión.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                                @RequestParam String password,
                                HttpSession session,
                                RedirectAttributes ra) {

        Cliente cliente = clienteService.buscarPorEmail(email);

        if (cliente == null || !passwordEncoder.matches(password, cliente.getPassword())) {
            ra.addFlashAttribute("error", "Correo o contraseña incorrectos.");
            return "redirect:/login";
        }

        session.setAttribute("clienteLogueado", cliente);
        ra.addFlashAttribute("ok", "Bienvenido, " + cliente.getNombre() + "!");
        return "redirect:/";
    }

    @GetMapping("/logout-cliente")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("ok", "Has cerrado sesión correctamente.");
        return "redirect:/";
    }
}
