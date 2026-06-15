package br.edu.ifba.euc.labamigo.repository;

import br.edu.ifba.euc.labamigo.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoRepository
        extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findByInsumoId(Long id);

}
