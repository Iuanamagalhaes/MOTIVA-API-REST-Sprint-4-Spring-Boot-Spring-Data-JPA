package br.com.motiva.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "INTERVENCAO_OPERACIONAL")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO_INTERVENCAO", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class IntervencaoOperacional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INTERVENCAO")
    private Long id;

    @Column(name = "DESCRICAO", nullable = false, length = 200)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_TRECHO_ALVO", nullable = false)
    private TrechoRodovia trechoAlvo;

    @Column(name = "TIPO_INTERVENCAO", insertable = false, updatable = false)
    private String tipoIntervencao;

    @Column(name = "DATA_INTERVENCAO", nullable = false, updatable = false)
    private LocalDateTime dataIntervencao;

    protected IntervencaoOperacional() {
    }

    protected IntervencaoOperacional(String descricao, TrechoRodovia trechoAlvo) {
        this.descricao = descricao;
        this.trechoAlvo = trechoAlvo;
    }

    @PrePersist
    void definirDataIntervencao() {
        if (dataIntervencao == null) {
            dataIntervencao = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TrechoRodovia getTrechoAlvo() {
        return trechoAlvo;
    }

    public void setTrechoAlvo(TrechoRodovia trechoAlvo) {
        this.trechoAlvo = trechoAlvo;
    }

    public LocalDateTime getDataIntervencao() {
        return dataIntervencao;
    }

    public abstract TipoIntervencao getTipo();

    public abstract void executarServico();

    public final void iniciarIntervencao() {
        executarServico();
    }
}
