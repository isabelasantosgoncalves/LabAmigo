package br.edu.ifba.euc.labamigo.service;

import br.edu.ifba.euc.labamigo.model.*;
import br.edu.ifba.euc.labamigo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoteiroExperimentalService {

    private final RoteiroExperimentalRepository repository;
    private final ItensRoteiroRepository itensRepository;
    private final InsumoRepository insumoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final ExecucaoRoteiroRepository execucaoRepository;

    public List<RoteiroExperimental> listar() {
        return repository.findAll();
    }

    public RoteiroExperimental buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Roteiro não encontrado."));
    }

    public RoteiroExperimental salvar(RoteiroExperimental roteiro) {
        return repository.save(roteiro);
    }


    @Transactional //indicação do estagiario
    public void salvarComItens(RoteiroExperimental roteiro, List<Long> insumoIds, List<Double> quantidadesNecessarias) {
        RoteiroExperimental salvo = repository.save(roteiro);

        if (insumoIds != null && quantidadesNecessarias != null) {
            for (int i = 0; i < insumoIds.size(); i++) {
                Insumo insumo = insumoRepository.findById(insumoIds.get(i))
                        .orElseThrow(() -> new RuntimeException("Insumo não encontrado"));

                ItensRoteiro item = new ItensRoteiro();
                item.setRoteiro(salvo);
                item.setInsumo(insumo);
                item.setQuantidadeNecessaria(quantidadesNecessarias.get(i));

                itensRepository.save(item);
            }
        }
    }

    public List<ItensRoteiro> listarItensDoRoteiro(Long roteiroId) {
        return itensRepository.findByRoteiroId(roteiroId);
    }


    @Transactional //indicação do estagiario
    public void executarRoteiro(Long roteiroId) {
        RoteiroExperimental roteiro = buscarPorId(roteiroId);
        List<ItensRoteiro> itens = itensRepository.findByRoteiroId(roteiroId);
        LocalDate hoje = LocalDate.now();
        List<String> insumosInsuficientes = new ArrayList<>();
        for (ItensRoteiro item : itens) {
            List<Movimentacao> movimentacoes = movimentacaoRepository.findByInsumoId(item.getInsumo().getId());

            // Calcula o estoque considerando apenas lotes válidos (não vencidos)
            double estoqueValido = 0;
            for (Movimentacao mov : movimentacoes) {
                if ("ENTRADA".equals(mov.getTipo())) {
                    // Se o lote já venceu ou vence hoje, ele fica indisponível (estoque dele não soma)
                    if (mov.getDataValidade() == null || !mov.getDataValidade().isBefore(hoje.plusDays(1))) {
                        estoqueValido += mov.getQuantidade();
                    }
                } else {
                    estoqueValido -= mov.getQuantidade();
                }
            }

            if (estoqueValido < item.getQuantidadeNecessaria()) {
                insumosInsuficientes.add(item.getInsumo().getNome() + " (Faltam: " + (item.getQuantidadeNecessaria() - estoqueValido) + ")");
            }
        }
        if (!insumosInsuficientes.isEmpty()) {
            throw new RuntimeException("Estoque insuficiente ou vencido para os seguintes insumos: " + String.join(", ", insumosInsuficientes));
        }
        for (ItensRoteiro item : itens) {
            Movimentacao baixa = new Movimentacao();
            baixa.setInsumo(item.getInsumo());
            baixa.setTipo("SAIDA_EXECUCAO");
            baixa.setQuantidade(item.getQuantidadeNecessaria());
            baixa.setDescricaoLote("Execução automática do roteiro: " + roteiro.getTitulo());
            baixa.setData(hoje); // Data automática do sistema
            baixa.setDataValidade(null);

            movimentacaoRepository.save(baixa);
        }

        ExecucaoRoteiro execucao = new ExecucaoRoteiro();
        execucao.setRoteiro(roteiro);
        execucao.setDataHora(LocalDateTime.now()); // Data e hora automáticas do sistema, nao consegui testar pra ver se funciona bbs
        execucaoRepository.save(execucao);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}