package br.com.fiap.to;

import java.util.List;

public class RotaTO {
    // Atributos
    private List<TrechoTO> trechos;
    private int totalTrechos;
    private int totalEstacoes;
    private int totalBaldeacoes;
    private Integer tempoDeslocamento;
    private Integer tempoEspera;
    private Integer tempoBaldeacoes;
    private Integer tempoTotal;
    private Double indiceMedioLotacao;
    private Integer indiceMaximoLotacao;
    private String classificacaoLotacao;

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

    public Integer getTempoDeslocamento() {
        return tempoDeslocamento;
    }

    public void setTempoDeslocamento(Integer tempoDeslocamento) {
        this.tempoDeslocamento = tempoDeslocamento;
    }

    public Integer getTempoEspera() {
        return tempoEspera;
    }

    public void setTempoEspera(Integer tempoEspera) {
        this.tempoEspera = tempoEspera;
    }

    public Integer getTempoBaldeacoes() {
        return tempoBaldeacoes;
    }

    public void setTempoBaldeacoes(Integer tempoBaldeacoes) {
        this.tempoBaldeacoes = tempoBaldeacoes;
    }

    public Integer getTempoTotal() {
        return tempoTotal;
    }

    public void setTempoTotal(Integer tempoTotal) {
        this.tempoTotal = tempoTotal;
    }

    public Double getIndiceMedioLotacao() {
        return indiceMedioLotacao;
    }

    public void setIndiceMedioLotacao(Double indiceMedioLotacao) {
        this.indiceMedioLotacao = indiceMedioLotacao;
    }

    public Integer getIndiceMaximoLotacao() {
        return indiceMaximoLotacao;
    }

    public void setIndiceMaximoLotacao(Integer indiceMaximoLotacao) {
        this.indiceMaximoLotacao = indiceMaximoLotacao;
    }

    public String getClassificacaoLotacao() {
        return classificacaoLotacao;
    }

    public void setClassificacaoLotacao(String classificacaoLotacao) {
        this.classificacaoLotacao = classificacaoLotacao;
    }
}
