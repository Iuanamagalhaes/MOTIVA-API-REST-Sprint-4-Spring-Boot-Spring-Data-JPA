package br.com.motiva.service;

import br.com.motiva.model.IntervencaoOperacional;
import br.com.motiva.model.Prioridade;
import br.com.motiva.model.Pulverizacao;
import br.com.motiva.model.RocadaManual;
import br.com.motiva.model.RocadaMecanizada;
import br.com.motiva.model.TipoIntervencao;
import br.com.motiva.model.TrechoRodovia;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MotorPrioridade {
    private static final double LIMIAR_CRITICO = 40.0;
    private static final double LIMIAR_ATENCAO = 20.0;
    private static final double LIMIAR_ALERTA = 10.0;

    public Prioridade classificarPrioridade(TrechoRodovia trecho) {
        double nivel = trecho.getNivelVegetacao();
        if (nivel >= LIMIAR_CRITICO) return Prioridade.CRITICO;
        if (nivel >= LIMIAR_ATENCAO) return Prioridade.ATENCAO;
        if (nivel >= LIMIAR_ALERTA) return Prioridade.ALERTA;
        return Prioridade.NORMAL;
    }

    public TipoIntervencao sugerirTipoIntervencao(Prioridade prioridade) {
        return switch (prioridade) {
            case CRITICO -> TipoIntervencao.MECANIZADA;
            case ATENCAO -> TipoIntervencao.MANUAL;
            case ALERTA -> TipoIntervencao.PULVERIZACAO;
            case NORMAL -> null;
        };
    }

    public IntervencaoOperacional criarIntervencao(TrechoRodovia trecho, Prioridade prioridade) {
        return switch (prioridade) {
            case CRITICO -> new RocadaMecanizada(
                    "Roçada mecanizada - prioridade CRÍTICA", trecho, "Trator roçadeira");
            case ATENCAO -> new RocadaManual(
                    "Roçada manual - prioridade ATENÇÃO", trecho, 4);
            case ALERTA -> new Pulverizacao(
                    "Pulverização - prioridade ALERTA", trecho, "Herbicida seletivo", 10.0);
            case NORMAL -> null;
        };
    }

    public List<TrechoRodovia> ordenarPorCriticidade(List<TrechoRodovia> trechos) {
        List<TrechoRodovia> copia = new ArrayList<>(trechos);
        copia.sort(Comparator.comparingDouble(TrechoRodovia::getNivelVegetacao).reversed());
        return copia;
    }

    public List<IntervencaoOperacional> gerarIntervencoes(List<TrechoRodovia> trechos) {
        List<IntervencaoOperacional> intervencoes = new ArrayList<>();
        for (TrechoRodovia trecho : ordenarPorCriticidade(trechos)) {
            IntervencaoOperacional intervencao = criarIntervencao(trecho, classificarPrioridade(trecho));
            if (intervencao != null) {
                intervencoes.add(intervencao);
            }
        }
        return intervencoes;
    }
}