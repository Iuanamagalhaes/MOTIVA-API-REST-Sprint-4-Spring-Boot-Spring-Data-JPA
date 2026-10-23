package br.com.motiva.dto;

import java.util.List;

public record RelatorioGeradoResponse(
        RelatorioResponse relatorio,
        List<ItemRelatorioResponse> itens) {
}
