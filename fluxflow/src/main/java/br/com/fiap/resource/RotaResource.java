
package br.com.fiap.resource;

import br.com.fiap.bo.RotaBO;
import br.com.fiap.to.RotaTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("fluxflow/rotas")
public class RotaResource {
    // Objeto de apoio
    private final RotaBO rotaBO = new RotaBO();

    // BUSCAR MENOR ROTA ENTRE DUAS ESTAÇÕES
    @GetMapping
    public ResponseEntity<?> buscarRota(@RequestParam Long origem, @RequestParam Long destino) {
        try {
            // Calcula a rota entre as estações informadas
            RotaTO rota = rotaBO.buscarMenorRota(origem, destino);

            return ResponseEntity.ok(rota);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao calcular a rota.");
        }
    }
}
