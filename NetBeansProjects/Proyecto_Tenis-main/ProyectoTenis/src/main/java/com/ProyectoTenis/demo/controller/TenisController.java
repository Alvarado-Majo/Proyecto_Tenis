/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ProyectoTenis.demo.controller;
import com.ProyectoTenis.demo.domain.Tenis;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 *
 * @author ivann
 */


@Controller
public class TenisController {

   
    private static List<Tenis> listaTenis = new ArrayList<>();

   
    public TenisController() {
        if (listaTenis.isEmpty()) {
             listaTenis.add(new Tenis(1, "Nike Air Max", "Tenis deportivos", 45000, "Hombre", 10));
            listaTenis.add(new Tenis(2, "Adidas UltraBoost", "Tenis running", 52000, "Mujer", 8));
            listaTenis.add(new Tenis(3, "Puma RS-X", "Casual unisex", 39000, "Unisex", 12));
        }
    }

    
    @GetMapping("/tenis/listado")
    public String listarTenis(Model model) {
        model.addAttribute("tenis", listaTenis);
        return "tenis/listado"; // HTML listado
    }

    
    @GetMapping("/tenis/eliminar/{id}")
    public String eliminarTenis(@PathVariable int id) {
        listaTenis.removeIf(t -> t.getIdTenis() == id);
        return "redirect:/tenis/listado";
    }

}
