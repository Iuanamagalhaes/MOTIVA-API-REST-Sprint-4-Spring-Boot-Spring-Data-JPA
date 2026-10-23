package br.com.motiva.service;

import br.com.motiva.dto.EquipeRequest;
import br.com.motiva.dto.EquipeResponse;
import br.com.motiva.exception.RecursoNaoEncontradoException;
import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.model.EquipeManutencao;
import br.com.motiva.repository.EquipeManutencaoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipeService {

    private final EquipeManutencaoRepository repository;

    public EquipeService(EquipeManutencaoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EquipeResponse> listar(String nome) {
        List<EquipeManutencao> equipes = (nome != null && !nome.isBlank())
                ? repository.findByNomeContainingIgnoreCase(nome.trim())
                : repository.findAll(Sort.by("id"));
        return equipes.stream().map(EquipeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public EquipeResponse buscarPorId(Long id) {
        return EquipeResponse.from(obter(id));
    }

    @Transactional
    public EquipeResponse criar(EquipeRequest req) {
        validar(req);
        EquipeManutencao equipe = new EquipeManutencao(req.nome().trim(), req.quantidadeIntegrantes());
        return EquipeResponse.from(repository.save(equipe));
    }

    @Transactional
    public EquipeResponse atualizar(Long id, EquipeRequest req) {
        EquipeManutencao equipe = obter(id);
        validar(req);
        equipe.setNome(req.nome().trim());
        equipe.setQuantidadeIntegrantes(req.quantidadeIntegrantes());
        return EquipeResponse.from(repository.save(equipe));
    }

    @Transactional
    public void remover(Long id) {
        repository.delete(obter(id));
    }

    private EquipeManutencao obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipe", id));
    }

    private void validar(EquipeRequest req) {
        if (req.nome() == null || req.nome().isBlank()) {
            throw new RegraNegocioException("O nome da equipe é obrigatório.");
        }
        if (req.quantidadeIntegrantes() == null || req.quantidadeIntegrantes() <= 0) {
            throw new RegraNegocioException("A equipe deve ter ao menos 1 integrante.");
        }
    }
}
