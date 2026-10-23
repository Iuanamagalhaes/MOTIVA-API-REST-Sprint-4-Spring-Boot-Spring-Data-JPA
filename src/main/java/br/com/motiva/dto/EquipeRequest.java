package br.com.motiva.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EquipeRequest(
        @NotBlank(message = "nome é obrigatório")
        @Size(max = 100, message = "nome deve ter no máximo 100 caracteres")
        String nome,

        @NotNull(message = "quantidadeIntegrantes é obrigatório")
        @Positive(message = "quantidadeIntegrantes deve ser maior que zero")
        @Max(value = 99999, message = "quantidadeIntegrantes deve ser no máximo 99999")
        Integer quantidadeIntegrantes) {
}