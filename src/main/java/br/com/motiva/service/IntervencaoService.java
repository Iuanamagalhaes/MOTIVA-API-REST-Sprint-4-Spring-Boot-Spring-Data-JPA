package br.com.motiva.service;

import br.com.motiva.dto.IntervencaoRequest;
import br.com.motiva.dto.IntervencaoResponse;
import br.com.motiva.exception.RecursoNaoEncontradoException;
import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.model.IntervencaoOperacional;
import br.com.motiva.model.Pulverizacao;
import br.com.motiva.model.RocadaManual;
import br.com.motiva.model.RocadaMecanizada;
import br.com.motiva.model.TipoIntervencao;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.repository.IntervencaoOperacionalRepository;
import br.com.motiva.repository.TrechoRodoviaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IntervencaoService {

    private final IntervencaoOperacionalRepository repository;
    private final TrechoRodoviaRepository trechoRepository;

    public IntervencaoService(IntervencaoOperacionalRepository repository,
                              TrechoRodoviaRepository trechoRepository) {
        this.repository = repository;
        this.trechoRepository = trechoRepository;
    }

    @Transactional(readOnly = true)
    public List<IntervencaoResponse> listar(TipoIntervencao tipo, Long idTrecho) {
        List<IntervencaoOperacional> lista;
        if (tipo != null) {
            lista = repository.findByTipoIntervencao(tipo.name());
            if (idTrecho != null) {
                lista = lista.stream().filter(i -> i.getTrechoAlvo().getId().equals(idTrecho)).toList();
            }
        } else if (idTrecho != null) {
            lista = repository.findByTrechoAlvoId(idTrecho);
        } else {
            lista = repository.findAll(Sort.by("id"));
        }
        return lista.stream().map(IntervencaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public IntervencaoResponse buscarPorId(Long id) {
        return IntervencaoResponse.from(obter(id));
    }

    @Transactional
    public IntervencaoResponse criar(IntervencaoRequest req) {
        validarCamposDoTipo(req);
        TrechoRodovia trecho = obterTrechoAlvo(req.idTrechoAlvo());
        String descricao = req.descricao().trim();

        IntervencaoOperacional nova = switch (req.tipoIntervencao()) {
            case MECANIZADA -> new RocadaMecanizada(descricao, trecho, req.tipoEquipamento().trim());
            case MANUAL -> new RocadaManual(descricao, trecho, req.numeroPessoas());
            case PULVERIZACAO -> new Pulverizacao(descricao, trecho,
                    req.produtoQuimico().trim(), req.volumeLitros());
        };
        return IntervencaoResponse.from(repository.save(nova));
    }

    @Transactional
    public IntervencaoResponse atualizar(Long id, IntervencaoRequest req) {
        IntervencaoOperacional existente = obter(id);
        validarCamposDoTipo(req);
        if (existente.getTipo() != req.tipoIntervencao()) {
            throw new RegraNegocioException("Não é permitido alterar o tipo da intervenção (atual: "
                    + existente.getTipo() + "). Remova e crie uma nova.");
        }
        TrechoRodovia trecho = obterTrechoAlvo(req.idTrechoAlvo());

        existente.setDescricao(req.descricao().trim());
        existente.setTrechoAlvo(trecho);
        if (existente instanceof RocadaMecanizada rm) {
            rm.setTipoEquipamento(req.tipoEquipamento().trim());
        } else if (existente instanceof RocadaManual rman) {
            rman.setNumeroDePessoas(req.numeroPessoas());
        } else if (existente instanceof Pulverizacao p) {
            p.setProdutoQuimico(req.produtoQuimico().trim());
            p.setVolumeLitros(req.volumeLitros());
        }
        return IntervencaoResponse.from(repository.save(existente));
    }

    @Transactional
    public void remover(Long id) {
        repository.delete(obter(id));
    }

    private IntervencaoOperacional obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Intervenção", id));
    }

    private TrechoRodovia obterTrechoAlvo(Long idTrecho) {
        return trechoRepository.findById(idTrecho)
                .orElseThrow(() -> new RegraNegocioException(
                        "Trecho alvo com id " + idTrecho + " não existe."));
    }

    private void validarCamposDoTipo(IntervencaoRequest req) {
        switch (req.tipoIntervencao()) {
            case MECANIZADA -> {
                if (req.tipoEquipamento() == null || req.tipoEquipamento().isBlank()) {
                    throw new RegraNegocioException("tipoEquipamento é obrigatório para MECANIZADA.");
                }
            }
            case MANUAL -> {
                if (req.numeroPessoas() == null || req.numeroPessoas() <= 0) {
                    throw new RegraNegocioException("numeroPessoas deve ser maior que zero para MANUAL.");
                }
            }
            case PULVERIZACAO -> {
                if (req.produtoQuimico() == null || req.produtoQuimico().isBlank()) {
                    throw new RegraNegocioException("produtoQuimico é obrigatório para PULVERIZACAO.");
                }
                if (req.volumeLitros() == null || req.volumeLitros() <= 0) {
                    throw new RegraNegocioException("volumeLitros deve ser maior que zero para PULVERIZACAO.");
                }
            }
        }
    }
}