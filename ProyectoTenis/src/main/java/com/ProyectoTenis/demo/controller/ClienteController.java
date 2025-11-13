package com.ProyectoTenis.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.service.ClienteService;

@Controller
@RequestMapping("/cliente")
@SessionAttributes("cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Muestra el formulario de registro de nuevo cliente.
     */
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "registro"; // plantilla registro.html
    }

    /**
     * Procesa el registro de un nuevo cliente.
     */
    @PostMapping("/registro")
    public String registrarCliente(@ModelAttribute Cliente cliente,
                                   RedirectAttributes ra) {
        try {
            clienteService.registrar(cliente);
            ra.addFlashAttribute("ok", "Cuenta creada exitosamente. Ahora puede iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al registrar el cliente: " + e.getMessage());
            return "redirect:/cliente/registro";
        }
    }

    /**
     * Muestra el perfil del cliente logueado.
     */
    @GetMapping("/perfil")
    public String verPerfil(@SessionAttribute(name = "cliente", required = false) Cliente cliente,
                            Model model,
                            RedirectAttributes ra) {
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para acceder a su perfil.");
            return "redirect:/login";
        }

        model.addAttribute("cliente", cliente);
        return "perfil_cliente"; // plantilla perfil_cliente.html
    }

    /**
     * Muestra el formulario para editar los datos del cliente.
     */
    @GetMapping("/editar")
    public String editarPerfil(@SessionAttribute(name = "cliente", required = false) Cliente cliente,
                               Model model,
                               RedirectAttributes ra) {
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para editar su perfil.");
            return "redirect:/login";
        }

        model.addAttribute("cliente", cliente);
        return "editar_cliente"; // plantilla editar_cliente.html
    }

    /**
     * Procesa la actualización del perfil del cliente.
     */
    @PostMapping("/editar")
    public String actualizarPerfil(@ModelAttribute Cliente clienteActualizado,
                                   @SessionAttribute(name = "cliente", required = false) Cliente clienteSesion,
                                   Model model,
                                   RedirectAttributes ra) {

        if (clienteSesion == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para actualizar su perfil.");
            return "redirect:/login";
        }

        try {
            // Ajuste correcto del ID
            clienteActualizado.setIdCliente(clienteSesion.getIdCliente());

            clienteService.actualizar(clienteActualizado);

            // actualizar la sesión
            model.addAttribute("cliente", clienteActualizado);

            ra.addFlashAttribute("ok", "Perfil actualizado correctamente.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }

        return "redirect:/cliente/perfil";
    }
}
