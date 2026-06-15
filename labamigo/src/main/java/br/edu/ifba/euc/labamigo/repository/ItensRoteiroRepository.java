package br.edu.ifba.euc.labamigo.repository;

import br.edu.ifba.euc.labamigo.model.ItensRoteiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItensRoteiroRepository extends JpaRepository<ItensRoteiro, Long> {

    List<ItensRoteiro> findByRoteiroId(Long roteiroId);

}