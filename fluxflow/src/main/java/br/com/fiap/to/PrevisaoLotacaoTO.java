
package br.com.fiap.to;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PrevisaoLotacaoTO {
    // Atributos
    private Long idPrevisao;
    @NotNull
    private LocalDate dtPrevisao;
    @NotNull
    @Min(0)
    @Max(23)
    private Integer vlHoraReferencia;
    @NotNull
    @Min(0)
    @Max(100)
    private Integer vlIndiceLotacao;
    @NotBlank
    @Size(max = 80)
    private String dsClassificacao;
    @NotBlank
    @Size(max = 200)
    private String dsModeloUtilizado;
    @NotNull
    private LocalDateTime dtGeracao;
    @NotNull
    private Long estacaoIdEstacao;
    @NotNull
    private Long linhaIdLinha;

    // Construtor vazio
    public PrevisaoLotacaoTO() {
    }

    // Getters e Setters
    public Long getIdPrevisao() {
        return idPrevisao;
    }

    public void setIdPrevisao(Long idPrevisao) {
        this.idPrevisao = idPrevisao;
    }

    public LocalDate getDtPrevisao() {
        return dtPrevisao;
    }

    public void setDtPrevisao(LocalDate dtPrevisao) {
        this.dtPrevisao = dtPrevisao;
    }

    public Integer getVlHoraReferencia() {
        return vlHoraReferencia;
    }

    public void setVlHoraReferencia(Integer vlHoraReferencia) {
        this.vlHoraReferencia = vlHoraReferencia;
    }

    public Integer getVlIndiceLotacao() {
        return vlIndiceLotacao;
    }

    public void setVlIndiceLotacao(Integer vlIndiceLotacao) {
        this.vlIndiceLotacao = vlIndiceLotacao;
    }

    public String getDsClassificacao() {
        return dsClassificacao;
    }

    public void setDsClassificacao(String dsClassificacao) {
        this.dsClassificacao = dsClassificacao;
    }

    public String getDsModeloUtilizado() {
        return dsModeloUtilizado;
    }

    public void setDsModeloUtilizado(String dsModeloUtilizado) {
        this.dsModeloUtilizado = dsModeloUtilizado;
    }

    public LocalDateTime getDtGeracao() {
        return dtGeracao;
    }

    public void setDtGeracao(LocalDateTime dtGeracao) {
        this.dtGeracao = dtGeracao;
    }

    public Long getEstacaoIdEstacao() {
        return estacaoIdEstacao;
    }

    public void setEstacaoIdEstacao(Long estacaoIdEstacao) {
        this.estacaoIdEstacao = estacaoIdEstacao;
    }

    public Long getLinhaIdLinha() {
        return linhaIdLinha;
    }

    public void setLinhaIdLinha(Long linhaIdLinha) {
        this.linhaIdLinha = linhaIdLinha;
    }
}
