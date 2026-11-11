package br.com.fiap.to;

public class LinhaRotaTO {
    // Atributos
    private Long idLinha;
    private String dsCodigo;
    private String nmLinha;
    private String dsCor;

    // Construtor Vazio
    public LinhaRotaTO() {
    }

    // Getter e Setter
    public Long getIdLinha() {
        return idLinha;
    }

    public void setIdLinha(Long idLinha) {
        this.idLinha = idLinha;
    }

    public String getDsCodigo() {
        return dsCodigo;
    }

    public void setDsCodigo(String dsCodigo) {
        this.dsCodigo = dsCodigo;
    }

    public String getNmLinha() {
        return nmLinha;
    }

    public void setNmLinha(String nmLinha) {
        this.nmLinha = nmLinha;
    }

    public String getDsCor() {
        return dsCor;
    }

    public void setDsCor(String dsCor) {
        this.dsCor = dsCor;
    }
}
