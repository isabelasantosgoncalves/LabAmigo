//package br.edu.ifba.euc.labamigo.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/execucoes")
//@RequiredArgsConstructor
//public class ExecucaoController {
//
//    private final ExecucaoService service;
//
//    @GetMapping
//    public String listar(Model model) {
//
//        model.addAttribute("execucoes", service.listar());
//
//        return "execucao/lista";
//    }
//
//    @GetMapping("/nova")
//    public String nova(Model model) {
//
//        model.addAttribute("execucao", new ExecucaoRoteiro());
//
//        return "execucao/form";
//    }
//
//    @PostMapping
//    public String salvar(@ModelAttribute ExecucaoRoteiro execucao) {
//
//        service.salvar(execucao);
//
//        return "redirect:/execucoes";
//    }
//}