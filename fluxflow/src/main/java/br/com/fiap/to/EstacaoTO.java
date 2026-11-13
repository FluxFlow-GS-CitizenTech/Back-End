
package br.com.fiap.to;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class EstacaoTO {
    // Atributos
    private Long idEstacao;
    @NotBlank(message = "O nome da estação é obrigatório.")
    @Size(max = 400, message = "O nome deve ter no máximo 400 caracteres.")
    private String nmEstacao;
    private BigDecimal dsLatitude;
    private BigDecimal dsLongitude;
    @NotBlank(message = "O status da estação é obrigatório.")
    @Size(max = 4, message = "O status deve ter no máximo 4 caracteres.")
    private String stEstacao;

    // Construtor vazio
    public EstacaoTO() {
    }

    // Getter e Setter
    public Long getIdEstacao() {
        return idEstacao;
    }

    public void setIdEstacao(Long idEstacao) {
        this.idEstacao = idEstacao;
    }

    public String getNmEstacao() {
        return nmEstacao;
    }

    public void setNmEstacao(String nmEstacao) {
        this.nmEstacao = nmEstacao;
    }

    public BigDecimal getDsLatitude() {
        return dsLatitude;
    }

    public void setDsLatitude(BigDecimal dsLatitude) {
        this.dsLatitude = dsLatitude;
    }

    public BigDecimal getDsLongitude() {
        return dsLongitude;
    }

    public void setDsLongitude(BigDecimal dsLongitude) {
        this.dsLongitude = dsLongitude;
    }

    public String getStEstacao() {
        return stEstacao;
    }

    public void setStEstacao(String stEstacao) {
        this.stEstacao = stEstacao;
    }
}
