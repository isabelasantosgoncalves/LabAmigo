package br.edu.ifba.euc.labamigo.service;

import br.edu.ifba.euc.labamigo.model.Insumo;
import br.edu.ifba.euc.labamigo.repository.InsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InsumoService {

    private final InsumoRepository repository;

    public List<Insumo> listar() {
        return repository.findAll();
    }

    public Insumo buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Insumo não encontrado."));
    }

    public Insumo salvar(Insumo insumo) {
        return repository.save(insumo);
    }

    public Insumo atualizar(Long id,
                            Insumo insumo) {
        return repository.save(insumo);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

}