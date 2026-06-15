package br.edu.ifba.euc.labamigo.controller;

import br.edu.ifba.euc.labamigo.service.ExecucaoRoteiroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/execucoes")
@RequiredArgsConstructor
public class ExecucaoRoteiroController {

    private final ExecucaoRoteiroService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("execucoes", service.listar());
        return "execucoes/lista";
    }
}