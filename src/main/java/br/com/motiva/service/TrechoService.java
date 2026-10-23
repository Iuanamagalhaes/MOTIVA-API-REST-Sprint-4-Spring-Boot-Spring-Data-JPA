package br.com.motiva.service;

import br.com.motiva.dto.TrechoRequest;
import br.com.motiva.dto.TrechoResponse;
import br.com.motiva.exception.ConflitoException;
import br.com.motiva.exception.RecursoNaoEncontradoException;
import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.model.TipoAmbiente;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.repository.IntervencaoOperacionalRepository;
import br.com.motiva.repository.TrechoRodoviaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrechoService {

    private final TrechoRodoviaRepository repository;
    private final IntervencaoOperacionalRepository intervencaoRepository;

    public TrechoService(TrechoRodoviaRepository repository,
                         IntervencaoOperacionalRepository intervencaoRepository) {
        this.repository = repository;
        this.intervencaoRepository = intervencaoRepository;
    }

    @Transactional(readOnly = true)
    public List<TrechoResponse> listar(Double nivelMinimo, TipoAmbiente tipoAmbiente) {
        List<TrechoRodovia> trechos;
        if (nivelMinimo != null) {
            trechos = repository.findByNivelVegetacaoGreaterThanEqual(nivelMinimo);
            if (tipoAmbiente != null) {
                trechos = trechos.stream().filter(t -> t.getTipoAmbiente() == tipoAmbiente).toList();
            }
        } else if (tipoAmbiente != null) {
            trechos = repository.findByTipoAmbiente(tipoAmbiente);
        } else {
            trechos = repository.findAll(Sort.by("id"));
        }
        return trechos.stream().map(TrechoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TrechoResponse buscarPorId(Long id) {
        return TrechoResponse.from(obter(id));
    }

    @Transactional
    public TrechoResponse criar(TrechoRequest req) {
        validar(req);
        TrechoRodovia trecho = new TrechoRodovia(req.quilometroInicial(), req.quilometroFinal(),
                req.nivelVegetacao(), req.tipoAmbiente(), req.possuiSensorIoT());
        return TrechoResponse.from(repository.save(trecho));
    }

    @Transactional
    public TrechoResponse atualizar(Long id, TrechoRequest req) {
        TrechoRodovia trecho = obter(id);
        validar(req);
        trecho.setQuilometroInicial(req.quilometroInicial());
        trecho.setQuilometroFinal(req.quilometroFinal());
        trecho.setNivelVegetacao(req.nivelVegetacao());
        trecho.setTipoAmbiente(req.tipoAmbiente());
        trecho.setPossuiSensorIoT(req.possuiSensorIoT());
        return TrechoResponse.from(repository.save(trecho));
    }

    @Transactional
    public void remover(Long id) {
        TrechoRodovia trecho = obter(id);
        if (intervencaoRepository.existsByTrechoAlvoId(id)) {
            throw new ConflitoException(
                    "Trecho " + id + " possui intervenções vinculadas e não pode ser removido.");
        }
        repository.delete(trecho);
    }

    private TrechoRodovia obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Trecho", id));
    }

    private void validar(TrechoRequest req) {
        if (req.quilometroInicial() < 0 || req.quilometroFinal() < 0) {
            throw new RegraNegocioException("Quilometragens não podem ser negativas.");
        }
        if (req.quilometroFinal() <= req.quilometroInicial()) {
            throw new RegraNegocioException("O quilometroFinal deve ser maior que o quilometroInicial.");
        }
        if (req.nivelVegetacao() < 0) {
            throw new RegraNegocioException("O nivelVegetacao deve ser maior ou igual a zero.");
        }
    }
}
