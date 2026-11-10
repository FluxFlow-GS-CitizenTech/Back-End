
package br.com.fiap.resource;

import br.com.fiap.bo.StatusOperacionalBO;
import br.com.fiap.to.StatusOperacionalTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fluxflow/status-operacional")
public class StatusOperacionalResource {
    // Objeto BO
    private final StatusOperacionalBO bo = new StatusOperacionalBO();

    // obterSessão()
    // Armazena a sessão iniciada através do login
    private HttpSession obterSessao(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);

        if (sessao == null || sessao.getAttribute("idUsuario") == null) {
            throw new SecurityException("Usuário não autenticado.");
        }

        return sessao;
    }

    // verificaAdmin()
    // Faz a verificação da sessão para ver se é adm
    private void verificarAdmin(HttpServletRequest request) {
        HttpSession sessao = obterSessao(request);

        Long perfil = (Long) sessao.getAttribute("perfilAcessoIdPerfil");

        if (perfil == null || perfil != 16L) {
            throw new SecurityException("Acesso permitido somente para administradores.");
        }
    }

    // save()
    // Cadastra um Status Operacional
    @PostMapping
    public ResponseEntity<StatusOperacionalTO> save(@Valid @RequestBody StatusOperacionalTO status, HttpServletRequest request) {

        verificarAdmin(request);

        String origem = status.getDsOrigem();

        if (origem != null && origem.trim().equalsIgnoreCase("MANUAL")) {
            HttpSession sessao = obterSessao(request);
            Long idUsuario = (Long) sessao.getAttribute("idUsuario");
            // O responsável é sempre o administrador logado.
            status.setUsuarioFfIdUsuario(idUsuario);
        }

        StatusOperacionalTO resultado = bo.save(status);

        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // findAll()
    // Retorna uma lista de todos os Status
    @GetMapping
    public ResponseEntity<List<StatusOperacionalTO>> findAll() {
        return ResponseEntity.ok(bo.findAll());
    }

    // findById()
    // Retorna um status específico
    @GetMapping("/{id}")
    public ResponseEntity<StatusOperacionalTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bo.findById(id));
    }

    // update()
    // Atualiza um status se for admin
    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @Valid @RequestBody StatusOperacionalTO status, HttpServletRequest request) {

        verificarAdmin(request);

        status.setIdStatus(id);

        String origem = status.getDsOrigem();

        if (origem != null && origem.trim().equalsIgnoreCase("MANUAL")) {
            HttpSession sessao = obterSessao(request);
            Long idUsuario = (Long) sessao.getAttribute("idUsuario");

            // O responsável é sempre o administrador logado.
            status.setUsuarioFfIdUsuario(idUsuario);
        }

        bo.update(status);

        return ResponseEntity.noContent().build();
    }



    // delete()
    // Exclui um status se for adm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, HttpServletRequest request) {

        verificarAdmin(request);

        bo.delete(id);

        return ResponseEntity.noContent().build();
    }

    // Tratamento de segurança e erros
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> tratarSeguranca(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarArgumento(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
