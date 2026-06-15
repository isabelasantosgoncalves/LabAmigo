package br.edu.ifba.euc.labamigo.controller;

import br.edu.ifba.euc.labamigo.service.MovimentacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Map;

/**
 * Disponibiliza as notificações de validade em TODAS as páginas,
 * para alimentar o sininho de notificações do cabeçalho.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalNotificacoes {

    private final MovimentacaoService movimentacaoService;

    @ModelAttribute
    public void adicionarNotificacoes(Model model) {
        List<Map<String, Object>> alertas = movimentacaoService.gerarAlertasValidade();
        model.addAttribute("notificacoes", alertas);
        model.addAttribute("numNotificacoes", alertas.size());
    }
}
