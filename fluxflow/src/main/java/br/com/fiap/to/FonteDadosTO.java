
package br.com.fiap.to;

import jakarta.validation.constraints.*;

public class FonteDadosTO {
    // Atributos
    private Long idFonte;
    @NotBlank
    @Size(max = 255)
    private String nmFonte;
    @NotBlank
    @Size(max = 80)
    private String tpFonte;
    private String dsUrlFonte;
    private String dsFonte;
    @NotBlank
    @Size(max = 1)
    private String stFonte;

    // Construtor Vazio
    public FonteDadosTO() {
    }

    // Getter e Setter
    public Long getIdFonte() {
        return idFonte;
    }

    public void setIdFonte(Long idFonte) {
        this.idFonte = idFonte;
    }

    public String getNmFonte() {
        return nmFonte;
    }

    public void setNmFonte(String nmFonte) {
        this.nmFonte = nmFonte;
    }

    public String getTpFonte() {
        return tpFonte;
    }

    public void setTpFonte(String tpFonte) {
        this.tpFonte = tpFonte;
    }

    public String getDsUrlFonte() {
        return dsUrlFonte;
    }

    public void setDsUrlFonte(String dsUrlFonte) {
        this.dsUrlFonte = dsUrlFonte;
    }

    public String getDsFonte() {
        return dsFonte;
    }

    public void setDsFonte(String dsFonte) {
        this.dsFonte = dsFonte;
    }

    public String getStFonte() {
        return stFonte;
    }

    public void setStFonte(String stFonte) {
        this.stFonte = stFonte;
    }
}
