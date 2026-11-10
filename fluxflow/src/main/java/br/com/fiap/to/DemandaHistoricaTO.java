
package br.com.fiap.to;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class DemandaHistoricaTO {
    // Atributos
    private Long idDemanda;
    @NotNull
    private LocalDate dtReferencia;
    @NotNull
    @Min(0)
    @Max(23)
    private Integer vlHoraReferencia;
    @NotNull
    @Min(1)
    @Max(7)
    private Integer numDiaSemana;
    @Min(0)
    private Integer qtdPassageiros;
    @NotNull
    private Long estacaoIdEstacao;
    @NotNull
    private Long fonteDadosIdFonte;
    @NotNull
    private Long linhaIdLinha;

    // Construtor vazio
    public DemandaHistoricaTO() {
    }

    // Getters e Setters
    public Long getIdDemanda() {
        return idDemanda;
    }

    public void setIdDemanda(Long idDemanda) {
        this.idDemanda = idDemanda;
    }

    public LocalDate getDtReferencia() {
        return dtReferencia;
    }

    public void setDtReferencia(LocalDate dtReferencia) {
        this.dtReferencia = dtReferencia;
    }

    public Integer getVlHoraReferencia() {
        return vlHoraReferencia;
    }

    public void setVlHoraReferencia(Integer vlHoraReferencia) {
        this.vlHoraReferencia = vlHoraReferencia;
    }

    public Integer getNumDiaSemana() {
        return numDiaSemana;
    }

    public void setNumDiaSemana(Integer numDiaSemana) {
        this.numDiaSemana = numDiaSemana;
    }

    public Integer getQtdPassageiros() {
        return qtdPassageiros;
    }

    public void setQtdPassageiros(Integer qtdPassageiros) {
        this.qtdPassageiros = qtdPassageiros;
    }

    public Long getEstacaoIdEstacao() {
        return estacaoIdEstacao;
    }

    public void setEstacaoIdEstacao(Long estacaoIdEstacao) {
        this.estacaoIdEstacao = estacaoIdEstacao;
    }

    public Long getFonteDadosIdFonte() {
        return fonteDadosIdFonte;
    }

    public void setFonteDadosIdFonte(Long fonteDadosIdFonte) {
        this.fonteDadosIdFonte = fonteDadosIdFonte;
    }

    public Long getLinhaIdLinha() {
        return linhaIdLinha;
    }

    public void setLinhaIdLinha(Long linhaIdLinha) {
        this.linhaIdLinha = linhaIdLinha;
    }
}
