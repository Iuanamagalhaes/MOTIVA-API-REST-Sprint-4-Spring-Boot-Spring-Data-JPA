package br.com.motiva.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PULVERIZACAO")
public class Pulverizacao extends IntervencaoOperacional {
    private static final double FATOR_REDUCAO = 0.7;

    @Column(name = "PRODUTO_QUIMICO", length = 100)
    private String produtoQuimico;

    @Column(name = "VOLUME_LITROS")
    private Double volumeLitros;

    protected Pulverizacao() {
    }

    public Pulverizacao(String descricao, TrechoRodovia trechoAlvo,
                        String produtoQuimico, double volumeLitros) {
        super(descricao, trechoAlvo);
        this.produtoQuimico = produtoQuimico;
        this.volumeLitros = volumeLitros;
    }

    public String getProdutoQuimico() {
        return produtoQuimico;
    }

    public void setProdutoQuimico(String produtoQuimico) {
        this.produtoQuimico = produtoQuimico;
    }

    public Double getVolumeLitros() {
        return volumeLitros;
    }

    public void setVolumeLitros(Double volumeLitros) {
        this.volumeLitros = volumeLitros;
    }

    @Override
    public TipoIntervencao getTipo() {
        return TipoIntervencao.PULVERIZACAO;
    }

    @Override
    public void executarServico() {
        double nivelAtual = getTrechoAlvo().getNivelVegetacao();
        getTrechoAlvo().setNivelVegetacao(nivelAtual * FATOR_REDUCAO);
    }
}