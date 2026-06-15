package br.edu.ifba.euc.labamigo.controller;

import br.edu.ifba.euc.labamigo.service.InsumoService;
import br.edu.ifba.euc.labamigo.model.Insumo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final InsumoService insumoService;

    @GetMapping
    public String listar(Model model) {
        List<Insumo> todos = insumoService.listar();
        Map<String, Insumo> agrupados = new LinkedHashMap<>();

        for (Insumo i : todos) {
            if (i.getNome() == null) continue;
            String chaveNome = i.getNome().trim().toUpperCase();

            if (agrupados.containsKey(chaveNome)) {
                Insumo existente = agrupados.get(chaveNome);
                existente.setQuantidade(existente.getQuantidade() + (i.getQuantidade() != null ? i.getQuantidade() : 0.0));
            } else {
                Insumo objVisual = new Insumo();
                objVisual.setId(i.getId());
                objVisual.setNome(i.getNome());
                objVisual.setUnidadeMedida(i.getUnidadeMedida());
                objVisual.setQuantidade(i.getQuantidade() != null ? i.getQuantidade() : 0.0);
                agrupados.put(chaveNome, objVisual);
            }
        }

        model.addAttribute("itensEstoque", agrupados.values());
        return "estoque/lista";
    }
}