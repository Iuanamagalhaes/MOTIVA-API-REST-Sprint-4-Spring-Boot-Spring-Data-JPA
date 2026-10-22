package br.com.motiva.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MECANIZADA")
public class RocadaMecanizada extends IntervencaoOperacional {
    private static final double NIVEL_POS_INTERVENCAO = 2.0;

    @Column(name = "TIPO_EQUIPAMENTO", length = 100)
    private String tipoEquipamento;

    protected RocadaMecanizada() {
    }

    public RocadaMecanizada(String descricao, TrechoRodovia trechoAlvo, String tipoEquipamento) {
        super(descricao, trechoAlvo);
        this.tipoEquipamento = tipoEquipamento;
    }

    public String getTipoEquipamento() {
        return tipoEquipamento;
    }

    public void setTipoEquipamento(String tipoEquipamento) {
        this.tipoEquipamento = tipoEquipamento;
    }

    @Override
    public TipoIntervencao getTipo() {
        return TipoIntervencao.MECANIZADA;
    }

    @Override
    public void executarServico() {
        getTrechoAlvo().setNivelVegetacao(NIVEL_POS_INTERVENCAO);
    }
}