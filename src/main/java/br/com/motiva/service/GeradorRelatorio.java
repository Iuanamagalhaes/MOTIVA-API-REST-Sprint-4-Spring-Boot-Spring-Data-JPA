package br.com.motiva.service;

import br.com.motiva.dto.ItemRelatorioResponse;
import br.com.motiva.dto.RelatorioGeradoResponse;
import br.com.motiva.dto.RelatorioResponse;
import br.com.motiva.exception.RecursoNaoEncontradoException;
import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.model.Prioridade;
import br.com.motiva.model.RelatorioPrioridade;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.repository.RelatorioPrioridadeRepository;
import br.com.motiva.repository.TrechoRodoviaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class GeradorRelatorio {

    private final MotorPrioridade motor;
    private final TrechoRodoviaRepository trechoRepository;
    private final RelatorioPrioridadeRepository relatorioRepository;

    public GeradorRelatorio(MotorPrioridade motor,
                            TrechoRodoviaRepository trechoRepository,
                            RelatorioPrioridadeRepository relatorioRepository) {
        this.motor = motor;
        this.trechoRepository = trechoRepository;
        this.relatorioRepository = relatorioRepository;
    }

    @Transactional
    public RelatorioGeradoResponse gerarRelatorio() {
        List<TrechoRodovia> trechos = trechoRepository.findAll();
        if (trechos.isEmpty()) {
            throw new RegraNegocioException("Nao ha trechos cadastrados para gerar o relatorio.");
        }

        Map<Prioridade, Integer> contagem = new EnumMap<>(Prioridade.class);
        for (Prioridade p : Prioridade.values()) {
            contagem.put(p, 0);
        }

        List<ItemRelatorioResponse> itens = new ArrayList<>();
        for (TrechoRodovia trecho : motor.ordenarPorCriticidade(trechos)) {
            Prioridade prioridade = motor.classificarPrioridade(trecho);
            contagem.merge(prioridade, 1, Integer::sum);
            itens.add(new ItemRelatorioResponse(trecho.getId(), trecho.getQuilometroInicial(),
                    trecho.getQuilometroFinal(), trecho.getNivelVegetacao(), prioridade,
                    motor.sugerirTipoIntervencao(prioridade)));
        }

        int qtCritico = contagem.get(Prioridade.CRITICO);
        int qtAtencao = contagem.get(Prioridade.ATENCAO);
        int qtAlerta = contagem.get(Prioridade.ALERTA);
        int qtNormal = contagem.get(Prioridade.NORMAL);

        String resumo = String.format(
                "Relatório com %d trecho(s): %d crítico(s), %d em atenção, %d em alerta, %d normal(is).",
                trechos.size(), qtCritico, qtAtencao, qtAlerta, qtNormal);

        RelatorioPrioridade salvo = relatorioRepository.save(
                new RelatorioPrioridade(qtCritico, qtAtencao, qtAlerta, qtNormal, resumo));

        return new RelatorioGeradoResponse(RelatorioResponse.from(salvo), itens);
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponse> listarHistorico() {
        return relatorioRepository.findAllByOrderByDataGeracaoDesc().stream()
                .map(RelatorioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RelatorioResponse buscarPorId(Long id) {
        return relatorioRepository.findById(id)
                .map(RelatorioResponse::from)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relatório", id));
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponse> consultarPorPeriodo(LocalDate inicio, LocalDate fim) {
        if (fim.isBefore(inicio)) {
            throw new RegraNegocioException("A data 'fim' não pode ser anterior a data 'inicio'.");
        }
        return relatorioRepository
                .findByDataGeracaoBetweenOrderByDataGeracaoDesc(
                        inicio.atStartOfDay(), fim.atTime(LocalTime.of(23, 59, 59)))
                .stream().map(RelatorioResponse::from).toList();
    }
}