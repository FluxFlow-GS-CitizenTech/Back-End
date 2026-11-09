package br.com.fiap.to;

import jakarta.validation.constraints.*;

public class LoginTO {
    // Atributos
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    private String dsEmail;
    @NotBlank(message = "A senha é obrigatória.")
    @Size(max = 100, message = "A senha deve ter no máximo 100 caracteres.")
    private String dsSenha;

    // Construtor Vazio
    public LoginTO(){
    }

    // Getter e Setter
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
}
