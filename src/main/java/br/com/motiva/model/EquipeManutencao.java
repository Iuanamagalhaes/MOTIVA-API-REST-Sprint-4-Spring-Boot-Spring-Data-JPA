package br.com.motiva.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "EQUIPE_MANUTENCAO")
public class EquipeManutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EQUIPE")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "QTD_INTEGRANTES", nullable = false)
    private int quantidadeIntegrantes;

    protected EquipeManutencao() {
    }

    public EquipeManutencao(String nome, int quantidadeIntegrantes) {
        this.nome = nome;
        this.quantidadeIntegrantes = quantidadeIntegrantes;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuantidadeIntegrantes() {
        return quantidadeIntegrantes;
    }

    public void setQuantidadeIntegrantes(int quantidadeIntegrantes) {
        this.quantidadeIntegrantes = quantidadeIntegrantes;
    }

    public void executarIntervencao(IntervencaoOperacional intervencao) {
        intervencao.iniciarIntervencao();
    }

    @Override
    public String toString() {
        return String.format("EquipeManutencao{id=%s, nome='%s', integrantes=%d}",
                id, nome, quantidadeIntegrantes);
    }
}
