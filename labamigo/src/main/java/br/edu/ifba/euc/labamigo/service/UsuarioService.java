package br.edu.ifba.euc.labamigo.service;

import br.edu.ifba.euc.labamigo.model.Usuario;
import br.edu.ifba.euc.labamigo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class UsuarioService {
    private final UsuarioRepository repository;
    public List<Usuario> listar() {
        return repository.findAll();
    }
    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuário não encontrado."));
    }
    public Usuario salvar(Usuario usuario) {
        return repository.save(usuario);
    }

    public Usuario atualizar(Long id,Usuario usuario) {
        return repository.save(usuario);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

}
