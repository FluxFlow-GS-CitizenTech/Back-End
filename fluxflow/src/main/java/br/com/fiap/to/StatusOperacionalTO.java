
package br.com.fiap.to;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class StatusOperacionalTO {
    // Atributos
    private Long idStatus;
    @NotBlank(message = "O tipo da ocorrência é obrigatório.")
    @Size(max = 80, message = "O tipo da ocorrência deve ter no máximo 80 caracteres.")
    private String tpOcorrencia;
    @NotBlank(message = "A descrição do status é obrigatória.")
    @Size(max = 400, message = "A descrição deve ter no máximo 400 caracteres.")
    private String dsStatus;
    @NotBlank(message = "A situação é obrigatória.")
    @Size(max = 80, message = "A situação deve ter no máximo 80 caracteres.")
    private String dsSituacao;
    @NotNull(message = "A data de início é obrigatória.")
    private LocalDateTime dtInicio;
    private LocalDateTime dtFim;
    @NotBlank(message = "A origem da informação é obrigatória.")
    @Size(max = 80, message = "A origem deve ter no máximo 80 caracteres.")
    private String dsOrigem;
    @NotNull(message = "A linha é obrigatória.")
    private Long linhaIdLinha;
    private Long estacaoIdEstacao;
    private Long usuarioFfIdUsuario;
    private Long fonteDadosIdFonte;

    // Construtor Vazio
    public StatusOperacionalTO() {
    }

    // Getter e Setter
    public Long getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(Long idStatus) {
        this.idStatus = idStatus;
    }

    public String getTpOcorrencia() {
        return tpOcorrencia;
    }

    public void setTpOcorrencia(String tpOcorrencia) {
        this.tpOcorrencia = tpOcorrencia;
    }

    public String getDsStatus() {
        return dsStatus;
    }

    public void setDsStatus(String dsStatus) {
        this.dsStatus = dsStatus;
    }

    public String getDsSituacao() {
        return dsSituacao;
    }

    public void setDsSituacao(String dsSituacao) {
        this.dsSituacao = dsSituacao;
    }

    public LocalDateTime getDtInicio() {
        return dtInicio;
    }

    public void setDtInicio(LocalDateTime dtInicio) {
        this.dtInicio = dtInicio;
    }

    public LocalDateTime getDtFim() {
        return dtFim;
    }

    public void setDtFim(LocalDateTime dtFim) {
        this.dtFim = dtFim;
    }

    public String getDsOrigem() {
        return dsOrigem;
    }

    public void setDsOrigem(String dsOrigem) {
        this.dsOrigem = dsOrigem;
    }

    public Long getLinhaIdLinha() {
        return linhaIdLinha;
    }

    public void setLinhaIdLinha(Long linhaIdLinha) {
        this.linhaIdLinha = linhaIdLinha;
    }

    public Long getEstacaoIdEstacao() {
        return estacaoIdEstacao;
    }

    public void setEstacaoIdEstacao(Long estacaoIdEstacao) {
        this.estacaoIdEstacao = estacaoIdEstacao;
    }

    public Long getUsuarioFfIdUsuario() {
        return usuarioFfIdUsuario;
    }

    public void setUsuarioFfIdUsuario(Long usuarioFfIdUsuario) {
        this.usuarioFfIdUsuario = usuarioFfIdUsuario;
    }

    public Long getFonteDadosIdFonte() {
        return fonteDadosIdFonte;
    }

    public void setFonteDadosIdFonte(Long fonteDadosIdFonte) {
        this.fonteDadosIdFonte = fonteDadosIdFonte;
    }
}
