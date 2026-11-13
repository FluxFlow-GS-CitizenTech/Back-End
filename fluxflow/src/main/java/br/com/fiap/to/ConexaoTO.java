package br.com.fiap.to;

public class ConexaoTO {
    // Atributos
    private Long idEstacaoOrigem;
    private Long idEstacaoDestino;
    private Long idLinha;

    // Construtores
    public ConexaoTO() {
    }

    public ConexaoTO(Long idEstacaoOrigem, Long idEstacaoDestino, Long idLinha) {
        this.idEstacaoOrigem = idEstacaoOrigem;
        this.idEstacaoDestino = idEstacaoDestino;
        this.idLinha = idLinha;
    }

    // Getter e Setter
    public Long getIdEstacaoOrigem() {
        return idEstacaoOrigem;
    }

    public void setIdEstacaoOrigem(Long idEstacaoOrigem) {
        this.idEstacaoOrigem = idEstacaoOrigem;
    }

    public Long getIdEstacaoDestino() {
        return idEstacaoDestino;
    }

    public void setIdEstacaoDestino(Long idEstacaoDestino) {
        this.idEstacaoDestino = idEstacaoDestino;
    }

    public Long getIdLinha() {
        return idLinha;
    }

    public void setIdLinha(Long idLinha) {
        this.idLinha = idLinha;
    }
}
