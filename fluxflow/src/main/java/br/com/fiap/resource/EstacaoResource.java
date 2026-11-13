
package br.com.fiap.resource;

import br.com.fiap.bo.EstacaoBO;
import br.com.fiap.to.EstacaoTO;

import jakarta.servlet.http.*;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fluxflow/estacoes")
public class EstacaoResource {
    // Objeto BO
    private final EstacaoBO estacaoBO = new EstacaoBO();

    // Verifica se existe uma sessão autenticada
    private HttpSession obterSessao(HttpServletRequest request) {
        // Armazena a sessão
        HttpSession session = request.getSession(false);
        // Verifica se é nula
        if (session == null || session.getAttribute("idUsuario") == null) {
            throw new IllegalArgumentException("Usuário não autenticado.");
        }
        // retorna sessão
        return session;
    }

    // Verifica se o usuário é administrador
    private void verificarAdmin(HttpSession session) {
        // armazena o perfil da sessão
        Long perfil = (Long) session.getAttribute("perfilAcessoIdPerfil");
        // Verifica se é nulo ou comum
        if (perfil == null || !perfil.equals(16L)) {
            throw new SecurityException("Acesso permitido somente para administradores.");
        }
    }

    // Verifica se o usuário possui perfil comum ou administrador
    private void verificarUsuarioLogado(HttpSession session) {
        // Armazena o perfil da sessão
        Long perfil = (Long) session.getAttribute("perfilAcessoIdPerfil");
        // Verifica se é nulo ou diferente de adm e comum
        if (perfil == null || (!perfil.equals(16L) && !perfil.equals(17L))) {
            throw new SecurityException("Perfil sem permissão para realizar essa operação.");
        }
    }

    // CREATE - Usuário comum ou administrador
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody EstacaoTO estacao, HttpServletRequest request) {
        // Armazena sessão
        HttpSession session = obterSessao(request);
        // Verifica se é adm
        verificarAdmin(session);
        // Cria estação
        estacaoBO.save(estacao);

        return ResponseEntity.status(HttpStatus.CREATED).body(estacao);
    }

    // READ - Lista todas as estações, inclusive sem login
    @GetMapping
    public ResponseEntity<List<EstacaoTO>> findAll() {
        return ResponseEntity.ok(estacaoBO.findAll());
    }

    // READ - Busca uma estação pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<EstacaoTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(estacaoBO.findById(id));
    }

    // UPDATE - Somente administrador
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody EstacaoTO estacao, HttpServletRequest request) {
        // Armazena sessão
        HttpSession session = obterSessao(request);
        // Verifica se é adm
        verificarAdmin(session);
        // Armazena id
        estacao.setIdEstacao(id);
        // Atualiza estação
        estacaoBO.update(estacao);

        return ResponseEntity.noContent().build();
    }

    // DELETE - Somente administrador
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request) {
        // Armazena sessão
        HttpSession session = obterSessao(request);
        // Verifica se é adm
        verificarAdmin(session);
        // exclui estação
        estacaoBO.delete(id);

        return ResponseEntity.noContent().build();
    }

    // READ - Consulta as linhas vinculadas à estação
    @GetMapping("/{id}/linhas")
    public ResponseEntity<List<Long>> findLinhasByEstacao(@PathVariable Long id) {

        return ResponseEntity.ok(estacaoBO.findLinhasByEstacao(id));
    }

    // CREATE - Vincula uma linha à estação
    @PostMapping("/{idEstacao}/linhas/{idLinha}")
    public ResponseEntity<?> addLinha(@PathVariable Long idEstacao, @PathVariable Long idLinha, HttpServletRequest request) {
        // Armazena  sessão
        HttpSession session = obterSessao(request);
        // verifica se é adm
        verificarAdmin(session);
        // Faz o relacionamento
        estacaoBO.addLinha(idEstacao, idLinha);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // DELETE - Remove o vínculo entre estação e linha
    @DeleteMapping("/{idEstacao}/linhas/{idLinha}")
    public ResponseEntity<?> removeLinha(@PathVariable Long idEstacao, @PathVariable Long idLinha, HttpServletRequest request) {
        // Armazena sessão
        HttpSession session = obterSessao(request);
        // Verifica se é adm
        verificarAdmin(session);
        // Remove a linha
        estacaoBO.removeLinha(idEstacao, idLinha);

        return ResponseEntity.noContent().build();
    }

    // Trata requisições sem autenticação
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarNaoAutenticado(IllegalArgumentException e) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    // Trata requisições sem permissão
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> tratarSemPermissao(SecurityException e) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}
