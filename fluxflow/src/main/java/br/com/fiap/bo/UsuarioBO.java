package br.com.fiap.bo;

import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.to.LoginTO;
import br.com.fiap.to.UsuarioTO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;

public class UsuarioBO {
    // Atributos / Objetos
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    // passwordEnconder para proteção de senha em Hash
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // save()
    public UsuarioTO save(UsuarioTO usuario) {
        // Verifica se o e-mail já está cadastrado.
        if (usuarioDAO.findByEmail(usuario.getDsEmail()) != null) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }
        // Gera o hash da senha.
        usuario.setDsSenhaHash(passwordEncoder.encode(usuario.getDsSenha()));
        // Define os dados iniciais.
        usuario.setDtCadastro(LocalDate.now());
        if (usuario.getStUsuario() == null) {
            usuario.setStUsuario("T");
        }
        // Salva no banco.
        usuarioDAO.save(usuario);
        // Remove a senha original antes de retornar.
        usuario.setDsSenha(null);
        usuario.setDsSenhaHash(null);
        // retorno de objeto
        return usuario;
    }

    // login()
    public UsuarioTO login(LoginTO login) {
        // usa o metodo findByEmail passando o email que veio no login
        UsuarioTO usuario = usuarioDAO.findByEmail(login.getDsEmail());
        // Verifica se o usuario existe
        if (usuario == null) {
            throw new IllegalArgumentException("E-mail ou senha inválidos.");
        }
        // Verifica se o Status do usuario é verdadeiro
        if (!"T".equalsIgnoreCase(usuario.getStUsuario())) {
            throw new IllegalArgumentException("Usuário inativo.");
        }
        // Valida a senha
        boolean senhaValida = passwordEncoder.matches(login.getDsSenha(), usuario.getDsSenhaHash());
        // Se o metodo matches() retornar falso
        if (!senhaValida) {
            throw new IllegalArgumentException("E-mail ou senha inválidos.");
        }
        // Não retorna informações sensíveis.
        usuario.setDsSenha(null);
        usuario.setDsSenhaHash(null);
        // retorno de objeto
        return usuario;
    }

    // findAll()
    public List<UsuarioTO> findAll() {
        // nova lista de usuarios
        List<UsuarioTO> usuarios = usuarioDAO.findAll();
        // For each que pega usuário por usuario e reseta a senha
        for (UsuarioTO usuario : usuarios) {
            usuario.setDsSenha(null);
            usuario.setDsSenhaHash(null);
        }
        // retorno de objeto
        return usuarios;
    }

    // findById()
    public UsuarioTO findById(Long id) {
        // novo objeto
        UsuarioTO usuario = usuarioDAO.findById(id);
        // Reseta a senha se o usuário existir
        if (usuario != null) {
            usuario.setDsSenha(null);
            usuario.setDsSenhaHash(null);
        }
        // retorno do objeto
        return usuario;
    }

    // update()
    public void update(UsuarioTO usuario) {
        // Verifica se o usuário existe
        UsuarioTO existente = usuarioDAO.findById(usuario.getIdUsuario());
        if (existente == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        // Verifica se o email informado para alteração já existe no banco
        UsuarioTO emailExistente = usuarioDAO.findByEmail(usuario.getDsEmail());
        if (emailExistente != null && !emailExistente.getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }
        // Preserva o hash atual se nenhuma nova senha foi informada.
        if (usuario.getDsSenha() != null && !usuario.getDsSenha().isBlank()) {
            // Armzena o hash da nova senha
            usuario.setDsSenhaHash(passwordEncoder.encode(usuario.getDsSenha()));
        } else {
            usuario.setDsSenhaHash(existente.getDsSenhaHash());
        }
        // chamada de metodo
        usuarioDAO.update(usuario);
    }

    // delete()
    public void delete(Long id) {
        // Verifica se o usuario existe
        UsuarioTO usuario = usuarioDAO.findById(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        // chamada de metodo
        usuarioDAO.delete(id);
    }
}
