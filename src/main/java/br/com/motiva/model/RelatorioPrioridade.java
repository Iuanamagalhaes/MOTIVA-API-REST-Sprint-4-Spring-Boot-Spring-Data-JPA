package br.com.motiva.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "RELATORIO_PRIORIDADE")
public class RelatorioPrioridade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RELATORIO")
    private Long id;

    @Column(name = "QT_CRITICO", nullable = false)
    private int qtCritico;

    @Column(name = "QT_ATENCAO", nullable = false)
    private int qtAtencao;

    @Column(name = "QT_ALERTA", nullable = false)
    private int qtAlerta;

    @Column(name = "QT_NORMAL", nullable = false)
    private int qtNormal;

    @Column(name = "RESUMO", length = 4000)
    private String resumo;

    @Column(name = "DATA_GERACAO", nullable = false, updatable = false)
    private LocalDateTime dataGeracao;

    protected RelatorioPrioridade() {
    }

    public RelatorioPrioridade(int qtCritico, int qtAtencao, int qtAlerta, int qtNormal, String resumo) {
        this.qtCritico = qtCritico;
        this.qtAtencao = qtAtencao;
        this.qtAlerta = qtAlerta;
        this.qtNormal = qtNormal;
        this.resumo = resumo;
    }

    @PrePersist
    void definirDataGeracao() {
        if (dataGeracao == null) {
            dataGeracao = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public int getQtCritico() {
        return qtCritico;
    }

    public int getQtAtencao() {
        return qtAtencao;
    }

    public int getQtAlerta() {
        return qtAlerta;
    }

    public int getQtNormal() {
        return qtNormal;
    }

    public String getResumo() {
        return resumo;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }
}
