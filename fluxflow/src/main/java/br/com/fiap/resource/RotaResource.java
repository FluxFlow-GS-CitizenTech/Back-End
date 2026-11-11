package br.com.fiap.resource;

import br.com.fiap.bo.RotaBO;
import br.com.fiap.to.RotaTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("fluxflow/rotas")
public class RotaResource {
    // Objeto BO
    private final RotaBO rotaBO = new RotaBO();

    @GetMapping
    public ResponseEntity<?> buscarRota(@RequestParam Long origem, @RequestParam Long destino, @RequestParam(defaultValue = "BALDEACOES") String criterio) {
        try {
            RotaTO rota;
            // Normaliza o critério para evitar problemas com letras minúsculas.
            switch (criterio.toUpperCase()) {
                // Prioriza a menor quantidade de baldeações.
                case "BALDEACOES":
                    rota = rotaBO.buscarMenorRota(origem, destino);
                    break;
                // Prioriza a menor quantidade de estações.
                case "ESTACOES":
                    rota = rotaBO.buscarRotaMenosEstacoes(origem, destino);
                    break;
                // Critério informado não reconhecido.
                default:
                    return ResponseEntity.badRequest()
                            .body("Critério inválido. Utilize BALDEACOES ou ESTACOES.");
            }
            return ResponseEntity.ok(rota);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao calcular a rota.");
        }
    }
}

