package br.edu.ifba.euc.labamigo.service;

import br.edu.ifba.euc.labamigo.model.ItensRoteiro;
import br.edu.ifba.euc.labamigo.repository.ItensRoteiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItensRoteiroService {

    private final ItensRoteiroRepository repository;

    public List<ItensRoteiro> listar() {
        return repository.findAll();
    }

    public List<ItensRoteiro> listarItensDoRoteiro(Long roteiroId) {
        return repository.findByRoteiroId(roteiroId);
    }

    public ItensRoteiro buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Itens não encontrados."));
    }

    public ItensRoteiro salvar(ItensRoteiro item) {
        return repository.save(item);
    }

    public ItensRoteiro atualizar(
            Long id,
            ItensRoteiro item) {

        return repository.save(item);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}