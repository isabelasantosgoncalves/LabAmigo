package br.edu.ifba.euc.labamigo.controller;

import br.edu.ifba.euc.labamigo.service.InsumoService;
import br.edu.ifba.euc.labamigo.service.MovimentacaoService;
import br.edu.ifba.euc.labamigo.service.UsuarioService;
import br.edu.ifba.euc.labamigo.model.Insumo;
import br.edu.ifba.euc.labamigo.model.Movimentacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/insumos")
@RequiredArgsConstructor
public class InsumoController {

    private final InsumoService service;
    private final MovimentacaoService movimentacaoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "insumos",
                service.listar());

        return "insumo/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {

        model.addAttribute(
                "insumo",
                new Insumo());

        return "insumo/form";
    }

    @PostMapping
    public String salvar(
            @ModelAttribute Insumo insumo,
            @RequestParam Double quantidade,
            @RequestParam String descricaoLote,
            @RequestParam LocalDate dataValidade) {

        Insumo salvo = service.salvar(insumo);

        Movimentacao mov = new Movimentacao();

        var usuarios = usuarioService.listar();
        mov.setUsuario(usuarios.isEmpty() ? null : usuarios.get(0));

        mov.setInsumo(salvo);

        mov.setTipo("ENTRADA");

        mov.setQuantidade(quantidade);

        mov.setDescricaoLote(descricaoLote);

        mov.setDataValidade(dataValidade);

        mov.setData(LocalDate.now());

        movimentacaoService.salvar(mov);

        return "redirect:/insumos";
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "insumo",
                service.buscarPorId(id));

        return "insumo/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(
            @PathVariable Long id) {

        service.excluir(id);

        return "redirect:/insumos";
    }
}