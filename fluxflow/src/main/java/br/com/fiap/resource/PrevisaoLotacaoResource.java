
package br.com.fiap.resource;

import br.com.fiap.bo.PrevisaoLotacaoBO;
import br.com.fiap.to.PrevisaoLotacaoTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/fluxflow/previsoes-lotacao")
public class PrevisaoLotacaoResource {

    // Objeto BO
    private final PrevisaoLotacaoBO bo = new PrevisaoLotacaoBO();

    // Verifica se existe uma sessão autenticada
    private HttpSession obterSessao(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);

        if (sessao == null || sessao.getAttribute("idUsuario") == null) {
            throw new SecurityException("Usuário não autenticado.");
        }

        return sessao;
    }

    // Verifica se o usuário possui perfil de administrador
    private void verificarAdmin(HttpServletRequest request) {
        HttpSession sessao = obterSessao(request);

        Long perfil = (Long) sessao.getAttribute("perfilAcessoIdPerfil");

        if (perfil == null || perfil != 16L) {
            throw new SecurityException("Acesso permitido somente para administradores.");
        }
    }

    // Gera uma previsão de lotação
    @PostMapping
    public ResponseEntity<PrevisaoLotacaoTO> gerarPrevisao(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPrevisao, @RequestParam Integer hora, @RequestParam Long idEstacao, @RequestParam Long idLinha, HttpServletRequest request) {

        verificarAdmin(request);

        PrevisaoLotacaoTO resultado = bo.gerarPrevisao(dataPrevisao, hora, idEstacao, idLinha);

        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // Retorna todas as previsões
    @GetMapping
    public ResponseEntity<List<PrevisaoLotacaoTO>> findAll() {
        return ResponseEntity.ok(bo.findAll());
    }

    // Retorna uma previsão específica
    @GetMapping("/{id}")
    public ResponseEntity<PrevisaoLotacaoTO> findById(@PathVariable Long id) {

        return ResponseEntity.ok(bo.findById(id));
    }

    // Retorna previsões de uma estação
    @GetMapping("/estacao/{idEstacao}")
    public ResponseEntity<List<PrevisaoLotacaoTO>> findByEstacao(@PathVariable Long idEstacao) {

        return ResponseEntity.ok(bo.findByEstacao(idEstacao));
    }

    // Retorna previsões de uma data
    @GetMapping("/data/{data}")
    public ResponseEntity<List<PrevisaoLotacaoTO>> findByData(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        return ResponseEntity.ok(bo.findByData(data));
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
