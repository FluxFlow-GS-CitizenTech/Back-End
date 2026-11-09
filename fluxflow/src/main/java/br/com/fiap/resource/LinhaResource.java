package br.com.fiap.resource;

import br.com.fiap.bo.LinhaBO;
import br.com.fiap.to.LinhaTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fluxflow/linha")
public class LinhaResource {
    // Objeto BO
    private final LinhaBO linhaBO = new LinhaBO();

    // Verifica se existe uma sessão autenticada
    private HttpSession obterSessao(HttpServletRequest request) {
        // Armazena a sessão
        HttpSession session = request.getSession(false);
        // Se for nula, retorna mensagem de erro
        if (session == null || session.getAttribute("idUsuario") == null) {
            throw new IllegalArgumentException("Usuário não autenticado.");
        }
        // retorno de objeto
        return session;
    }

    // Verifica se o usuário possui perfil de administrador
    private void verificarAdmin(HttpSession session) {
        // Armazena o perfil de acesso da sessão
        Long perfil = (Long) session.getAttribute("perfilAcessoIdPerfil");
        // verifica se é nulo ou admin
        if (perfil == null || !perfil.equals(16L)) {
            throw new SecurityException("Acesso permitido somente para administradores.");
        }
    }

    // CREATE - Qualquer usuário logado pode cadastrar uma linha
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody LinhaTO linha, HttpServletRequest request) {
        // Captura a sessão
        HttpSession session = obterSessao(request);
        // Armazena o perfil de acesso da sessão
        Long perfil = (Long) session.getAttribute("perfilAcessoIdPerfil");
        // Verifica o perfil
        if (perfil == null || (!perfil.equals(16L) && !perfil.equals(17L))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Perfil sem permissão para cadastrar linhas.");
        }
        // Se permitido, cadastra a linha
        linhaBO.save(linha);

        return ResponseEntity.status(HttpStatus.CREATED).body(linha);
    }

    // READ - Consulta todas as linhas, mesmo sem autenticação
    @GetMapping
    public ResponseEntity<List<LinhaTO>> findAll() {
        return ResponseEntity.ok(linhaBO.findAll());
    }

    // READ - Consulta uma linha pelo ID, mesmo sem autenticação
    @GetMapping("/{id}")
    public ResponseEntity<LinhaTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(linhaBO.findById(id));
    }

    // UPDATE - Somente administradores podem atualizar
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody LinhaTO linha, HttpServletRequest request) {
        // Armazena a sessão e verifica se é adm
        HttpSession session = obterSessao(request);
        verificarAdmin(session);
        // Atualiza linha
        linha.setIdLinha(id);
        linhaBO.update(linha);

        return ResponseEntity.noContent().build();
    }

    // DELETE - Somente administradores podem excluir
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request) {
        // Armazena a sessão e verifica se é adm
        HttpSession session = obterSessao(request);
        verificarAdmin(session);
        // Exclui linha
        linhaBO.delete(id);

        return ResponseEntity.noContent().build();
    }

    // Tratamento de erros
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarNaoAutenticado(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> tratarSemPermissao(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}
