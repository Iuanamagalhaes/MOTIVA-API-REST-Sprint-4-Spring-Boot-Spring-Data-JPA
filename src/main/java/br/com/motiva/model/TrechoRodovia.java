package br.com.motiva.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TRECHO_RODOVIA")
public class TrechoRodovia implements MonitoravelViaIoT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TRECHO")
    private Long id;

    @Column(name = "KM_INICIAL", nullable = false)
    private double quilometroInicial;

    @Column(name = "KM_FINAL", nullable = false)
    private double quilometroFinal;

    @Column(name = "NIVEL_VEGETACAO", nullable = false)
    private double nivelVegetacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_AMBIENTE", nullable = false, length = 20)
    private TipoAmbiente tipoAmbiente;

    @Convert(converter = SimNaoConverter.class)
    @Column(name = "POSSUI_SENSOR_IOT", nullable = false, length = 1)
    private boolean possuiSensorIoT;

    protected TrechoRodovia() {
    }

    public TrechoRodovia(double quilometroInicial, double quilometroFinal,
                         double nivelVegetacao, TipoAmbiente tipoAmbiente,
                         boolean possuiSensorIoT) {
        this.quilometroInicial = quilometroInicial;
        this.quilometroFinal = quilometroFinal;
        this.nivelVegetacao = nivelVegetacao;
        this.tipoAmbiente = tipoAmbiente;
        this.possuiSensorIoT = possuiSensorIoT;
    }

    public Long getId() {
        return id;
    }

    public double getQuilometroInicial() {
        return quilometroInicial;
    }

    public void setQuilometroInicial(double quilometroInicial) {
        this.quilometroInicial = quilometroInicial;
    }

    public double getQuilometroFinal() {
        return quilometroFinal;
    }

    public void setQuilometroFinal(double quilometroFinal) {
        this.quilometroFinal = quilometroFinal;
    }

    public double getNivelVegetacao() {
        return nivelVegetacao;
    }

    public void setNivelVegetacao(double nivelVegetacao) {
        this.nivelVegetacao = Math.max(0, nivelVegetacao);
    }

    public TipoAmbiente getTipoAmbiente() {
        return tipoAmbiente;
    }

    public void setTipoAmbiente(TipoAmbiente tipoAmbiente) {
        this.tipoAmbiente = tipoAmbiente;
    }

    public boolean isPossuiSensorIoT() {
        return possuiSensorIoT;
    }

    public void setPossuiSensorIoT(boolean possuiSensorIoT) {
        this.possuiSensorIoT = possuiSensorIoT;
    }

    public void registrarCrescimento(double taxaBase) {
        double taxaReal = taxaBase * tipoAmbiente.getFatorCrescimento();
        setNivelVegetacao(this.nivelVegetacao + taxaReal);
    }

    @Override
    public void transmitirDadosSensor(double leituraDoSensor) {
        if (isSensorAtivo() && leituraDoSensor >= 0) {
            setNivelVegetacao(leituraDoSensor);
        }
    }

    @Override
    public boolean isSensorAtivo() {
        return possuiSensorIoT;
    }

    @Override
    public String toString() {
        return String.format(
                "TrechoRodovia{id=%s, km=%.2f-%.2f, nivelVegetacao=%.2fcm, ambiente=%s, sensorIoT=%s}",
                id, quilometroInicial, quilometroFinal, nivelVegetacao,
                tipoAmbiente != null ? tipoAmbiente.getDescricao() : null, possuiSensorIoT ? "S" : "N");
    }
}