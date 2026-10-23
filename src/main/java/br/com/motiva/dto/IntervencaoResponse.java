package br.com.motiva.dto;

import br.com.motiva.model.IntervencaoOperacional;
import br.com.motiva.model.Pulverizacao;
import br.com.motiva.model.RocadaManual;
import br.com.motiva.model.RocadaMecanizada;
import br.com.motiva.model.TipoIntervencao;

import java.time.LocalDateTime;

public record IntervencaoResponse(
        Long id,
        String descricao,
        Long idTrechoAlvo,
        TipoIntervencao tipoIntervencao,
        String tipoEquipamento,
        Integer numeroPessoas,
        String produtoQuimico,
        Double volumeLitros,
        LocalDateTime dataIntervencao) {

    public static IntervencaoResponse from(IntervencaoOperacional i) {
        String equipamento = null;
        Integer pessoas = null;
        String produto = null;
        Double volume = null;

        if (i instanceof RocadaMecanizada rm) {
            equipamento = rm.getTipoEquipamento();
        } else if (i instanceof RocadaManual rman) {
            pessoas = rman.getNumeroDePessoas();
        } else if (i instanceof Pulverizacao p) {
            produto = p.getProdutoQuimico();
            volume = p.getVolumeLitros();
        }

        return new IntervencaoResponse(i.getId(), i.getDescricao(), i.getTrechoAlvo().getId(),
                i.getTipo(), equipamento, pessoas, produto, volume, i.getDataIntervencao());
    }
}