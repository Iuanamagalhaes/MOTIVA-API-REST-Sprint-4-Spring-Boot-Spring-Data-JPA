package br.com.motiva.dto;

import br.com.motiva.model.RelatorioPrioridade;

import java.time.LocalDateTime;

public record RelatorioResponse(
        Long id,
        int qtCritico,
        int qtAtencao,
        int qtAlerta,
        int qtNormal,
        String resumo,
        LocalDateTime dataGeracao) {

    public static RelatorioResponse from(RelatorioPrioridade r) {
        return new RelatorioResponse(r.getId(), r.getQtCritico(), r.getQtAtencao(), r.getQtAlerta(),
                r.getQtNormal(), r.getResumo(), r.getDataGeracao());
    }
}