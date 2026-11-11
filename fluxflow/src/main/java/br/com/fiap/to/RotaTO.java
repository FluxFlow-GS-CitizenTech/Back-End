package br.com.fiap.to;

import java.util.List;

public class RotaTO {
    // Atributos
    private List<TrechoTO> trechos;
    private int totalTrechos;
    private int totalEstacoes;
    private int totalBaldeacoes;

    // Construtor Vazio
    public RotaTO() {
    }

    // Getter e Setter
    public List<TrechoTO> getTrechos() {
        return trechos;
    }

    public void setTrechos(List<TrechoTO> trechos) {
        this.trechos = trechos;
    }

    public int getTotalTrechos() {
        return totalTrechos;
    }

    public void setTotalTrechos(int totalTrechos) {
        this.totalTrechos = totalTrechos;
    }

    public int getTotalEstacoes() {
        return totalEstacoes;
    }

    public void setTotalEstacoes(int totalEstacoes) {
        this.totalEstacoes = totalEstacoes;
    }

    public int getTotalBaldeacoes() {
        return totalBaldeacoes;
    }

    public void setTotalBaldeacoes(int totalBaldeacoes) {
        this.totalBaldeacoes = totalBaldeacoes;
    }
}
