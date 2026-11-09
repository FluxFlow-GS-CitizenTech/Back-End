package br.com.fiap.to;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class UsuarioTO {
    // Atributos
    private Long idUsuario;
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 400, message = "O nome deve ter no máximo 400 caracteres.")
    private String nmUsuario;
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 600, message = "O e-mail deve ter no máximo 600 caracteres.")
    private String dsEmail;
    // Recebe a senha original somente na entrada da API.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres.")
    private String dsSenha;
    // Campo interno: armazena o hash gerado pela aplicação.
    @JsonIgnore
    private String dsSenhaHash;
    private LocalDate dtCadastro;
    private String stUsuario;
    private Long perfilAcessoIdPerfil;

    // Construtor Vazio
    public UsuarioTO(){
    }

    // Getter e Setter
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNmUsuario() {
        return nmUsuario;
    }

    public void setNmUsuario(String nmUsuario) {
        this.nmUsuario = nmUsuario;
    }

    public String getDsEmail() {
        return dsEmail;
    }

    public void setDsEmail(String dsEmail) {
        this.dsEmail = dsEmail;
    }

    public String getDsSenha() {
        return dsSenha;
    }

    public void setDsSenha(String dsSenha) {
        this.dsSenha = dsSenha;
    }

    public String getDsSenhaHash() {
        return dsSenhaHash;
    }

    public void setDsSenhaHash(String dsSenhaHash) {
        this.dsSenhaHash = dsSenhaHash;
    }

    public LocalDate getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(LocalDate dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public String getStUsuario() {
        return stUsuario;
    }

    public void setStUsuario(String stUsuario) {
        this.stUsuario = stUsuario;
    }

    public Long getPerfilAcessoIdPerfil() {
        return perfilAcessoIdPerfil;
    }

    public void setPerfilAcessoIdPerfil(Long perfilAcessoIdPerfil) {
        this.perfilAcessoIdPerfil = perfilAcessoIdPerfil;
    }
}
