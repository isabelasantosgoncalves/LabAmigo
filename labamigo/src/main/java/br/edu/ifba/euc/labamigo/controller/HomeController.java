package br.edu.ifba.euc.labamigo.controller;

import br.edu.ifba.euc.labamigo.service.*;
import br.edu.ifba.euc.labamigo.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final InsumoService insumoService;
    private final RoteiroExperimentalService roteiroService;
    private final ExecucaoRoteiroService execucaoService;
    private final MovimentacaoService movimentacaoService;

    @GetMapping("/")
    public String home(Model model) {
        List<Insumo> todosInsumos = insumoService.listar();
        List<RoteiroExperimental> todosRoteiros = roteiroService.listar();
        List<Movimentacao> movimentacoes = movimentacaoService.listar();
        LocalDate hoje = LocalDate.now();
        Set<String> insumosVencidos = new HashSet<>();
        for (Movimentacao mov : movimentacoes) {
            if (mov.getDataValidade() != null && !mov.getDataValidade().isAfter(hoje)) {
                double estLote = calcularEstoqueDoLote(mov.getDescricaoLote(), movimentacoes);
                if (estLote > 0) {
                    insumosVencidos.add(mov.getInsumo().getNome());
                }
            }
        }

        List<String> analisesRoteiros = new ArrayList<>();
        for (RoteiroExperimental roteiro : todosRoteiros) {
            List<ItensRoteiro> itens = roteiroService.listarItensDoRoteiro(roteiro.getId());
            long maxExecucoes = Long.MAX_VALUE;
            boolean possuiItens = !itens.isEmpty();

            for (ItensRoteiro item : itens) {
                double estoqueDisponivel = obterEstoqueDisponivelSemVencidos(item.getInsumo().getNome(), movimentacoes);
                long qtdPossivel = (long) (estoqueDisponivel / item.getQuantidadeNecessaria());
                if (qtdPossivel < maxExecucoes) {
                    maxExecucoes = qtdPossivel;
                }
            }

            if (!possuiItens) {
                maxExecucoes = 0;
            }

            if (maxExecucoes == 0) {
                analisesRoteiros.add("O roteiro '" + roteiro.getTitulo() + "' não pode mais ser executado por falta de insumos.");
            } else if (maxExecucoes <= 2) {
                analisesRoteiros.add("O roteiro '" + roteiro.getTitulo() + "' pode ser executado apenas " + maxExecucoes + " vez(es) com o estoque atual.");
            }
        }

        model.addAttribute("tituloPagina", "Dashboard LabAmigo");
        model.addAttribute("totalInsumos", todosInsumos.size());
        model.addAttribute("totalRoteiros", todosRoteiros.size());
        model.addAttribute("totalExecucoes", execucaoService.listar().size());
        model.addAttribute("insumosVencidos", insumosVencidos);
        model.addAttribute("analisesRoteiros", analisesRoteiros);

        return "index";
    }

    private double calcularEstoqueDoLote(String lote, List<Movimentacao> movs) {
        return movs.stream()
                .filter(m -> lote.equals(m.getDescricaoLote()))
                .mapToDouble(m -> "ENTRADA".equals(m.getTipo()) ? m.getQuantidade() : -m.getQuantidade())
                .sum();
    }

    private double obterEstoqueDisponivelSemVencidos(String nomeInsumo, List<Movimentacao> movs) {
        LocalDate hoje = LocalDate.now();
        return movs.stream()
                .filter(m -> m.getInsumo().getNome().equalsIgnoreCase(nomeInsumo))
                .filter(m -> m.getDataValidade() == null || m.getDataValidade().isAfter(hoje))
                .mapToDouble(m -> "ENTRADA".equals(m.getTipo()) ? m.getQuantidade() : -m.getQuantidade())
                .sum();
    }
}