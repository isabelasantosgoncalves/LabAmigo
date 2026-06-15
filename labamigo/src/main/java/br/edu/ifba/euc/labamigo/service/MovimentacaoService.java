package br.edu.ifba.euc.labamigo.service;

import br.edu.ifba.euc.labamigo.model.Movimentacao;
import br.edu.ifba.euc.labamigo.repository.MovimentacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MovimentacaoService {

    private final MovimentacaoRepository repository;

    public List<Movimentacao> listar() {
        return repository.findAll();
    }

    public Movimentacao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada."));
    }

    public Movimentacao salvar(Movimentacao movimentacao) {
        return repository.save(movimentacao);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    public Double calcularEstoque(Long insumoId) {
        List<Movimentacao> movimentacoes = repository.findByInsumoId(insumoId);
        double total = 0;
        for (Movimentacao mov : movimentacoes) {
            if ("ENTRADA".equals(mov.getTipo())) {
                total += mov.getQuantidade();
            } else {
                total -= mov.getQuantidade();
            }
        }
        return total;
    }

    public Double calcularEstoqueDisponivelGeral(Long insumoId) {
        List<Movimentacao> movimentacoes = repository.findByInsumoId(insumoId);
        LocalDate hoje = LocalDate.now();
        double total = 0;

        for (Movimentacao mov : movimentacoes) {
            if ("ENTRADA".equals(mov.getTipo())) {
                if (mov.getDataValidade() == null || !mov.getDataValidade().isBefore(hoje)) {
                    total += mov.getQuantidade();
                }
            } else {
                total -= mov.getQuantidade();
            }
        }
        return total < 0 ? 0.0 : total;
    }

    // Janela (em dias) para considerar um lote "vencendo em breve".
    private static final long DIAS_ALERTA = 30;

    // Status simples de validade em 3 níveis (para a pílula colorida).
    // Retorna: "VENCIDO", "ALERTA", "OK" ou "SEM" (sem data de validade).
    public String statusValidade(LocalDate validade) {
        if (validade == null) {
            return "SEM";
        }
        LocalDate hoje = LocalDate.now();
        if (!validade.isAfter(hoje)) {
            return "VENCIDO";
        }
        if (!validade.isAfter(hoje.plusDays(DIAS_ALERTA))) {
            return "ALERTA";
        }
        return "OK";
    }

    // Gera as notificações de validade (vencidos + vencendo em breve),
    // considerando apenas lotes de ENTRADA que ainda tenham estoque.
    public List<Map<String, Object>> gerarAlertasValidade() {
        List<Movimentacao> movs = repository.findAll();
        LocalDate hoje = LocalDate.now();
        List<Map<String, Object>> alertas = new ArrayList<>();
        Set<String> lotesProcessados = new HashSet<>();

        for (Movimentacao m : movs) {
            if (!"ENTRADA".equals(m.getTipo()) || m.getDataValidade() == null) {
                continue;
            }
            String lote = m.getDescricaoLote();
            if (!lotesProcessados.add(lote)) {
                continue; // lote já avaliado
            }

            double estoqueLote = movs.stream()
                    .filter(x -> Objects.equals(lote, x.getDescricaoLote()))
                    .mapToDouble(x -> "ENTRADA".equals(x.getTipo()) ? x.getQuantidade() : -x.getQuantidade())
                    .sum();
            if (estoqueLote <= 0) {
                continue; // lote já consumido
            }

            long dias = ChronoUnit.DAYS.between(hoje, m.getDataValidade());
            String nome = m.getInsumo() != null ? m.getInsumo().getNome() : "Insumo";
            String tipo;
            String mensagem;

            if (dias < 0) {
                tipo = "VENCIDO";
                mensagem = nome + " venceu há " + Math.abs(dias) + " dia(s)";
            } else if (dias <= DIAS_ALERTA) {
                tipo = "ALERTA";
                mensagem = (dias == 0) ? nome + " vence hoje" : nome + " vence em " + dias + " dia(s)";
            } else {
                continue;
            }

            Map<String, Object> alerta = new HashMap<>();
            alerta.put("tipo", tipo);
            alerta.put("mensagem", mensagem);
            alerta.put("insumo", nome);
            alerta.put("dias", dias);
            alerta.put("validade", m.getDataValidade());
            alertas.add(alerta);
        }

        alertas.sort(Comparator.comparingLong(a -> ((Number) a.get("dias")).longValue()));
        return alertas;
    }

    // TENTEI POR CORES sou apenas uma menina
    public String verificarStatusValidade(Movimentacao mov) {
        if (mov.getDataValidade() == null) {
            return "OK";
        }

        LocalDate hoje = LocalDate.now();

        if (!mov.getDataValidade().isAfter(hoje)) {
            return "VENCIDO"; // Vermelho fixo
        }
        if (!mov.getDataValidade().isAfter(hoje.plusDays(15))) {
            return "CRITICO_15_DIAS"; // Amarelo
        }
        if (!mov.getDataValidade().isAfter(hoje.plusMonths(1))) {
            return "ALERTA_1_MES"; // Amarelo
        }
        if (!mov.getDataValidade().isAfter(hoje.plusMonths(2))) {
            return "ALERTA_2_MESES"; // Amarelo
        }

        return "OK";
    }
}