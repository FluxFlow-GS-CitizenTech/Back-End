package br.com.fiap.to;

import jakarta.validation.constraints.*;

public class LinhaTO {
    // Atributos
    private Long idLinha;
    @NotBlank(message = "O código da linha é obrigatório.")
    @Size(max = 80, message = "O código deve ter no máximo 80 caracteres.")
    private String dsCodigo;
    @NotBlank(message = "O nome da linha é obrigatório.")
    @Size(max = 400, message = "O nome deve ter no máximo 400 caracteres.")
    private String nmLinha;
    @NotBlank(message = "A cor da linha é obrigatória.")
    @Size(max = 120, message = "A cor deve ter no máximo 120 caracteres.")
    private String dsCor;
    @NotBlank(message = "O operador da linha é obrigatório.")
    @Size(max = 80, message = "O operador deve ter no máximo 80 caracteres.")
    private String tpOperador;
    @NotBlank(message = "O status da linha é obrigatório.")
    @Size(max = 4, message = "O status deve ter no máximo 4 caracteres.")
    private String stLinha;

    // Construtor Vazio
    public LinhaTO() {
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

    public String getTpOperador() {
        return tpOperador;
    }

    public void setTpOperador(String tpOperador) {
        this.tpOperador = tpOperador;
    }

    public String getStLinha() {
        return stLinha;
    }

    public void setStLinha(String stLinha) {
        this.stLinha = stLinha;
    }
}
