package br.com.motiva.dto;

import br.com.motiva.model.TipoAmbiente;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record TrechoRequest(
        @NotNull(message = "quilometroInicial e obrigatório")
        @PositiveOrZero(message = "quilometroInicial não pode ser negativo")
        Double quilometroInicial,

        @NotNull(message = "quilometroFinal e obrigatório")
        @PositiveOrZero(message = "quilometroFinal não pode ser negativo")
        Double quilometroFinal,

        @NotNull(message = "nivelVegetacao e obrigatório")
        @PositiveOrZero(message = "nivelVegetacao não pode ser negativo")
        Double nivelVegetacao,

        @NotNull(message = "tipoAmbiente e obrigatório (UMIDO, SECO, TROPICAL ou PADRAO)")
        TipoAmbiente tipoAmbiente,

        @NotNull(message = "possuiSensorIoT e obrigatório")
        Boolean possuiSensorIoT) {
}
