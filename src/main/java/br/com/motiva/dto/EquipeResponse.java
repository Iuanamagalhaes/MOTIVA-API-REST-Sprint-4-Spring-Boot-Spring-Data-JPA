package br.com.motiva.dto;

import br.com.motiva.model.EquipeManutencao;

public record EquipeResponse(Long id, String nome, int quantidadeIntegrantes) {

    public static EquipeResponse from(EquipeManutencao e) {
        return new EquipeResponse(e.getId(), e.getNome(), e.getQuantidadeIntegrantes());
    }
}