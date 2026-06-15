package br.edu.ifba.euc.labamigo.controller;

import br.edu.ifba.euc.labamigo.service.InsumoService;
import br.edu.ifba.euc.labamigo.service.MovimentacaoService;
import br.edu.ifba.euc.labamigo.service.UsuarioService;
import br.edu.ifba.euc.labamigo.model.Insumo;
import br.edu.ifba.euc.labamigo.model.Movimentacao;
import br.edu.ifba.euc.labamigo.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/movimentacoes")
@RequiredArgsConstructor
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;
    private final InsumoService insumoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        List<Movimentacao> listaRaw = movimentacaoService.listar();
        LocalDate hoje = LocalDate.now();
        List<Map<String, Object>> movimentacoesDecoradas = new ArrayList<>();

        for (Movimentacao m : listaRaw) {
            String classeCor = "";
            if (m.getDataValidade() != null) {
                if (!m.getDataValidade().isAfter(hoje)) {
                    classeCor = "table-danger";
                } else if (!m.getDataValidade().isAfter(hoje.plusMonths(2))) {
                    classeCor = "table-warning";
                }
            }

            Map<String, Object> item = new HashMap<>();
            item.put("id", m.getId());
            item.put("insumoNome", m.getInsumo() != null ? m.getInsumo().getNome() : "N/A");
            item.put("tipo", m.getTipo());
            item.put("quantidade", m.getQuantidade());
            item.put("descricaoLote", m.getDescricaoLote());
            item.put("dataValidade", m.getDataValidade());
            item.put("data", m.getData());
            item.put("statusValidade", movimentacaoService.statusValidade(m.getDataValidade()));
            //item.put("usuarioNome", m.getUsuario() != null ? m.getUsuario().getNome() : "Sistema");
            item.put("classeCor", classeCor);
            movimentacoesDecoradas.add(item);
        }

        model.addAttribute("movimentacoes", movimentacoesDecoradas);
        return "movimentacoes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("movimentacao", new Movimentacao());
        model.addAttribute("insumos", insumoService.listar());
        return "movimentacoes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Movimentacao movimentacao) {

        // Este formulário registra uma PERDA/baixa de material.
        // Como o formulário não envia esses campos, definimos aqui:
        // tipo = SAIDA (para descontar do estoque) e data = hoje.
        if (movimentacao.getTipo() == null || movimentacao.getTipo().isBlank()) {
            movimentacao.setTipo("SAIDA");
        }
        if (movimentacao.getData() == null) {
            movimentacao.setData(LocalDate.now());
        }

        movimentacaoService.salvar(movimentacao);

        if ("SAIDA".equals(movimentacao.getTipo()) || "SAIDA_EXECUCAO".equals(movimentacao.getTipo())) {
            Insumo insumo = movimentacao.getInsumo();
            insumo.setQuantidade(insumo.getQuantidade() - movimentacao.getQuantidade());
            insumoService.atualizar(insumo.getId(), insumo);
        }

        return "redirect:/movimentacoes";
    }
}