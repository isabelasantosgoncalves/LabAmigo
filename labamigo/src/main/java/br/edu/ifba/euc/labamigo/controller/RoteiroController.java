//package br.edu.ifba.euc.labamigo.controller;
//
//@Controller
//@RequestMapping("/roteiros")
//@RequiredArgsConstructor
//public class RoteiroController {
//
//    private final RoteiroService service;
//
//    @GetMapping
//    public String listar(Model model) {
//
//        model.addAttribute("roteiros", service.listar());
//
//        return "roteiro/lista";
//    }
//
//    @GetMapping("/novo")
//    public String novo(Model model) {
//
//        model.addAttribute("roteiro", new Roteiro());
//
//        return "roteiro/form";
//    }
//
//    @PostMapping
//    public String salvar(@ModelAttribute Roteiro roteiro) {
//
//        service.salvar(roteiro);
//
//        return "redirect:/roteiros";
//    }
//}