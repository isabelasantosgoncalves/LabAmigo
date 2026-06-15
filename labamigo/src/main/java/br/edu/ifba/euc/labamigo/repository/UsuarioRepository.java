package br.edu.ifba.euc.labamigo.repository;

import br.edu.ifba.euc.labamigo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository <Usuario,Long> {
    List<Usuario> findByNomeContainingIgnoreCase(String nome);
}
