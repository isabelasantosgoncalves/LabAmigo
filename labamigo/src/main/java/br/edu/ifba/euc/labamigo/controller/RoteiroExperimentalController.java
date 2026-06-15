package br.edu.ifba.euc.labamigo.controller;

import br.edu.ifba.euc.labamigo.service.*;
import br.edu.ifba.euc.labamigo.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/roteiros")
@RequiredArgsConstructor
public class RoteiroExperimentalController {

    private final RoteiroExperimentalService roteiroService;
    private final InsumoService insumoService;
    private final ItensRoteiroService itensRoteiroService;
    private final MovimentacaoService movimentacaoService;
    private final ExecucaoRoteiroService execucaoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("roteiros", roteiroService.listar());
        return "roteiros/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("roteiro", new RoteiroExperimental());
        model.addAttribute("insumos", insumoService.listar());
        return "roteiros/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute RoteiroExperimental roteiro,
                         @RequestParam(required = false) List<Long> insumoIds,
                         @RequestParam(required = false) List<Double> quantidades) {


        if (roteiro.getId() != null) {
            List<ItensRoteiro> itensAntigos = itensRoteiroService.listarItensDoRoteiro(roteiro.getId());
            for (ItensRoteiro item : itensAntigos) {
                itensRoteiroService.excluir(item.getId());
            }
        }


        RoteiroExperimental salvo = roteiroService.salvar(roteiro);


        if (insumoIds != null && quantidades != null) {
            for (int i = 0; i < insumoIds.size(); i++) {
                ItensRoteiro item = new ItensRoteiro();
                item.setRoteiro(salvo);
                item.setInsumo(insumoService.buscarPorId(insumoIds.get(i)));
                item.setQuantidadeNecessaria(quantidades.get(i));
                itensRoteiroService.salvar(item);
            }
        }
        return "redirect:/roteiros";
    }

    @GetMapping("/executar/{id}")
    public String executar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        RoteiroExperimental roteiro = roteiroService.buscarPorId(id);
        List<ItensRoteiro> itens = itensRoteiroService.listarItensDoRoteiro(id);
        List<Movimentacao> movimentacoes = movimentacaoService.listar();
        LocalDate hoje = LocalDate.now();

        List<String> insumosInsuficientes = new ArrayList<>();

        for (ItensRoteiro item : itens) {
            double estoqueValido = movimentacoes.stream()
                    .filter(m -> m.getInsumo().getId().equals(item.getInsumo().getId()))
                    .filter(m -> m.getDataValidade() == null || m.getDataValidade().isAfter(hoje))
                    .mapToDouble(m -> "ENTRADA".equals(m.getTipo()) ? m.getQuantidade() : -m.getQuantidade())
                    .sum();
            if (estoqueValido < item.getQuantidadeNecessaria()) {
                insumosInsuficientes.add(item.getInsumo().getNome() + " (Disponível: " + estoqueValido + ", Necessário: " + item.getQuantidadeNecessaria() + ")");
            }
        }

        if (!insumosInsuficientes.isEmpty()) {
            redirectAttributes.addFlashAttribute("erroEstoque", "Não foi possível realizar a ação. Estoques insuficientes ou vencidos de: " + String.join(", ", insumosInsuficientes));
            return "redirect:/roteiros";
        }

        Usuario user = usuarioService.listar().isEmpty() ? null : usuarioService.listar().get(0);
        for (ItensRoteiro item : itens) {
            Movimentacao movSaida = new Movimentacao();
            movSaida.setInsumo(item.getInsumo());

            movSaida.setQuantidade(item.getQuantidadeNecessaria());

            movSaida.setTipo("SAIDA_EXECUCAO");
            movSaida.setData(hoje);
            //movSaida.setUsuario(user); COMENTADO PQ VC JA SABE
            movSaida.setDescricaoLote("Execução de Roteiro: " + roteiro.getTitulo());
            movSaida.setDataValidade(null);
            movimentacaoService.salvar(movSaida);
        }

        ExecucaoRoteiro exec = new ExecucaoRoteiro();
        exec.setRoteiro(roteiro);
        exec.setUsuario(user);
        exec.setDataHora(LocalDateTime.now());
        execucaoService.salvar(exec);

        return "redirect:/execucoes";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        List<ItensRoteiro> itens = itensRoteiroService.listarItensDoRoteiro(id);
        for (ItensRoteiro item : itens) {
            itensRoteiroService.excluir(item.getId());
        }

        roteiroService.excluir(id);

        return "redirect:/roteiros";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        RoteiroExperimental roteiro = roteiroService.buscarPorId(id);
        model.addAttribute("roteiro", roteiro);
        model.addAttribute("insumos", insumoService.listar());
        List<ItensRoteiro> itensExistentes = itensRoteiroService.listarItensDoRoteiro(id);
        model.addAttribute("itensExistentes", itensExistentes);

        return "roteiros/form";
    }
}