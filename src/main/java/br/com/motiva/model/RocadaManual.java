package br.com.motiva.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MANUAL")
public class RocadaManual extends IntervencaoOperacional {
    private static final double NIVEL_POS_INTERVENCAO = 5.0;

    @Column(name = "NUMERO_PESSOAS")
    private Integer numeroDePessoas;

    protected RocadaManual() {
    }

    public RocadaManual(String descricao, TrechoRodovia trechoAlvo, int numeroDePessoas) {
        super(descricao, trechoAlvo);
        this.numeroDePessoas = numeroDePessoas;
    }

    public Integer getNumeroDePessoas() {
        return numeroDePessoas;
    }

    public void setNumeroDePessoas(Integer numeroDePessoas) {
        this.numeroDePessoas = numeroDePessoas;
    }

    @Override
    public TipoIntervencao getTipo() {
        return TipoIntervencao.MANUAL;
    }

    @Override
    public void executarServico() {
        getTrechoAlvo().setNivelVegetacao(NIVEL_POS_INTERVENCAO);
    }
}