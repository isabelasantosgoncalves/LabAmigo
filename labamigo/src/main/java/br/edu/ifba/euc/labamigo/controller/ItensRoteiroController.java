package br.edu.ifba.euc.labamigo.controller;

import br.edu.ifba.euc.labamigo.service.ItensRoteiroService;
import br.edu.ifba.euc.labamigo.model.ItensRoteiro;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/itens-roteiro")
@RequiredArgsConstructor
public class ItensRoteiroController {

    private final ItensRoteiroService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("itens", service.listar());
        return "itensroteiro/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("item", new ItensRoteiro());
        return "itensroteiro/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ItensRoteiro item) {
        service.salvar(item);
        return "redirect:/itens-roteiro";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        service.excluir(id);
        return "redirect:/itens-roteiro";
    }
}