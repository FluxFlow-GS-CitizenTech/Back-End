package br.com.fiap.to;

public class TrechoTO {
    // Atributos
    private EstacaoRotaTO origem;
    private EstacaoRotaTO destino;
    private LinhaRotaTO linha;

    // Construtor Vazio
    public TrechoTO() {
    }

    // Getter e Setter
    public EstacaoRotaTO getOrigem() {
        return origem;
    }

    public void setOrigem(EstacaoRotaTO origem) {
        this.origem = origem;
    }

    public EstacaoRotaTO getDestino() {
        return destino;
    }

    public void setDestino(EstacaoRotaTO destino) {
        this.destino = destino;
    }

    public LinhaRotaTO getLinha() {
        return linha;
    }

    public void setLinha(LinhaRotaTO linha) {
        this.linha = linha;
    }
}
