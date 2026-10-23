package br.com.motiva.dto;

import br.com.motiva.model.Prioridade;
import br.com.motiva.model.TipoIntervencao;

public record ItemRelatorioResponse(
        Long idTrecho,
        double quilometroInicial,
        double quilometroFinal,
        double nivelVegetacao,
        Prioridade prioridade,
        TipoIntervencao intervencaoSugerida) {
}
