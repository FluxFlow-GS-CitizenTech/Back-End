
package br.com.fiap.resource;

import br.com.fiap.bo.UsuarioBO;
import br.com.fiap.to.LoginTO;
import br.com.fiap.to.UsuarioTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("fluxflow/usuario")
public class UsuarioResource {
    // Objeto de apoio
    private final UsuarioBO usuarioBO = new UsuarioBO();

    // CADASTRAR USUÁRIO
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody UsuarioTO usuario) {
        try {
            UsuarioTO usuarioCadastrado = usuarioBO.save(usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCadastrado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // REALIZAR LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginTO login, HttpServletRequest request) {
        try {
            UsuarioTO usuario = usuarioBO.login(login);
            // Evita manter uma sessão anterior após novo login.
            HttpSession sessaoAnterior = request.getSession(false);
            if (sessaoAnterior != null) {
                sessaoAnterior.invalidate();
            }
            // Cria uma nova sessão e armazena o ID do usuário e seu perfil.
            HttpSession session = request.getSession(true);

            session.setAttribute("idUsuario", usuario.getIdUsuario());
            session.setAttribute("perfilAcessoIdPerfil", usuario.getPerfilAcessoIdPerfil());

            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // REALIZAR LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // Captura a sessão se for existente
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.noContent().build();
        }
        // invalida a sessão
        session.invalidate();

        return ResponseEntity.noContent().build();
    }

    // LISTAR USUÁRIOS
    @GetMapping
    public ResponseEntity<List<UsuarioTO>> findAll() {
        return ResponseEntity.ok(usuarioBO.findAll());
    }

    // BUSCAR USUÁRIO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        // Armazenamento de retorno
        UsuarioTO usuario = usuarioBO.findById(id);
        // Verifica se é nulo
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuario);
    }

    // ATUALIZAR O PRÓPRIO USUÁRIO
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UsuarioTO usuario, HttpSession session) {
        // Armazena o id do usuario que iniciou a sessão
        Long idUsuarioLogado = (Long) session.getAttribute("idUsuario");
        // Verifica se existe usuário autenticado.
        if (idUsuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("É necessário realizar login.");
        }
        // Permite alterar somente o próprio cadastro.
        if (!idUsuarioLogado.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não pode alterar outro usuário.");
        }

        try {
            // Verifica se o usuário existe
            UsuarioTO usuarioExistente = usuarioBO.findById(id);
            if (usuarioExistente == null) {
                return ResponseEntity.notFound().build();
            }
            // Armazena id
            usuario.setIdUsuario(id);
            // Preserva os campos que o usuário não pode alterar.
            usuario.setStUsuario(usuarioExistente.getStUsuario());
            usuario.setPerfilAcessoIdPerfil(usuarioExistente.getPerfilAcessoIdPerfil());
            // chamada de metodo
            usuarioBO.update(usuario);

            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // EXCLUIR O PRÓPRIO USUÁRIO
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        // Armazena o id do usuario que iniciou a sessão
        Long idUsuarioLogado = (Long) session.getAttribute("idUsuario");
        // Verifica se existe usuário autenticado.
        if (idUsuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("É necessário realizar login.");
        }
        // Permite apenas a exclusão de si mesmo
        if (!idUsuarioLogado.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Você não pode excluir outro usuário.");
        }
        try {
            usuarioBO.delete(id);
            // Encerra a sessão após excluir a própria conta.
            session.invalidate();

            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
