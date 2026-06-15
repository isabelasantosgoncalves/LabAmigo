package br.edu.ifba.euc.labamigo.repository;

import br.edu.ifba.euc.labamigo.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsumoRepository extends JpaRepository<Insumo, Long>{
    List<Insumo> findByNomeContainingIgnoreCase(String nome);
}
