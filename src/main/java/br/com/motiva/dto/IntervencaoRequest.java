package br.com.motiva.dto;

import br.com.motiva.model.TipoIntervencao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IntervencaoRequest(
        @NotBlank(message = "descrição e obrigatória")
        @Size(max = 200, message = "descrição deve ter no máximo 200 caracteres")
        String descricao,

        @NotNull(message = "idTrechoAlvo é obrigatório")
        Long idTrechoAlvo,

        @NotNull(message = "tipoIntervencao é obrigatório (MECANIZADA, MANUAL ou PULVERIZACAO)")
        TipoIntervencao tipoIntervencao,

        @Size(max = 100, message = "tipoEquipamento deve ter no máximo 100 caracteres")
        String tipoEquipamento,

        Integer numeroPessoas,

        @Size(max = 100, message = "produtoQuimico deve ter no máximo 100 caracteres")
        String produtoQuimico,

        Double volumeLitros) {
}