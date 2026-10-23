package br.com.motiva.dto;

import br.com.motiva.model.TipoAmbiente;
import br.com.motiva.model.TrechoRodovia;

public record TrechoResponse(
        Long id,
        double quilometroInicial,
        double quilometroFinal,
        double nivelVegetacao,
        TipoAmbiente tipoAmbiente,
        boolean possuiSensorIoT) {

    public static TrechoResponse from(TrechoRodovia t) {
        return new TrechoResponse(t.getId(), t.getQuilometroInicial(), t.getQuilometroFinal(),
                t.getNivelVegetacao(), t.getTipoAmbiente(), t.isPossuiSensorIoT());
    }
}