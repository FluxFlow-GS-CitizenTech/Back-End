package br.com.fiap.to;

import java.math.BigDecimal;

public class EstacaoRotaTO {
    // Atributos
    private Long idEstacao;
    private String nmEstacao;
    private BigDecimal dsLatitude;
    private BigDecimal dsLongitude;

    // Construtor Vazio
    public EstacaoRotaTO() {
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
}
