package br.com.motiva.model;

public enum TipoAmbiente {

    UMIDO(1.8, "Umido"),
    SECO(0.7, "Seco"),
    TROPICAL(1.4, "Tropical"),
    PADRAO(1.0, "Padrao");

    private final double fatorCrescimento;
    private final String descricao;

    TipoAmbiente(double fatorCrescimento, String descricao) {
        this.fatorCrescimento = fatorCrescimento;
        this.descricao = descricao;
    }

    public double getFatorCrescimento() {
        return fatorCrescimento;
    }

    public String getDescricao() {
        return descricao;
    }
}