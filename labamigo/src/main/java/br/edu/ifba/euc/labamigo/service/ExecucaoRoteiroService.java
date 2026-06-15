package br.edu.ifba.euc.labamigo.service;

import br.edu.ifba.euc.labamigo.model.ExecucaoRoteiro;
import br.edu.ifba.euc.labamigo.repository.ExecucaoRoteiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExecucaoRoteiroService{
    private final ExecucaoRoteiroRepository repository;

    public List<ExecucaoRoteiro> listar() {
        return repository.findAll();
    }

    public ExecucaoRoteiro buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Execução do roteiro não encontrada."));
    }

    public ExecucaoRoteiro salvar(ExecucaoRoteiro execucaoroteiro) {
        return repository.save(execucaoroteiro);
    }

    public ExecucaoRoteiro atualizar(Long id,
                                     ExecucaoRoteiro execucaoroteiro) {
        return repository.save(execucaoroteiro);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

}
