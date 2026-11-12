
package br.com.fiap.resource;

import br.com.fiap.bo.RotaBO;
import br.com.fiap.to.RotaTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("fluxflow/rotas")
public class RotaResource {
    // Objeto BO
    private final RotaBO rotaBO = new RotaBO();

    @GetMapping
    public ResponseEntity<?> buscarRota(@RequestParam Long origem, @RequestParam Long destino, @RequestParam(defaultValue = "BALDEACOES") String criterio, @RequestParam(required = false) String data, @RequestParam(required = false) Integer hora) {
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
                // Prioriza o menor tempo.
                case "TEMPO":
                    rota = rotaBO.buscarRotaMaisRapida(origem, destino);
                    break;
                case "LOTACAO":
                    // Verifica se a data e o horário foram informados.
                    if (data == null || hora == null) {
                        return ResponseEntity.badRequest().body("Para o critério LOTACAO, informe os parâmetros data e hora.");
                    }
                    // Converte a data recebida para LocalDate.
                    LocalDate dataPrevisao;
                    try {
                        dataPrevisao = LocalDate.parse(data);
                    } catch (DateTimeParseException e) {
                        return ResponseEntity.badRequest().body("Data inválida. Utilize o formato AAAA-MM-DD.");
                    }
                    // Valida o horário informado.
                    if (hora < 0 || hora > 23) {
                        return ResponseEntity.badRequest().body("Hora inválida. Informe um valor entre 0 e 23.");
                    }
                    // Busca a rota com a menor média de lotação.
                    rota = rotaBO.buscarRotaMenorLotacao(origem, destino, java.sql.Date.valueOf(dataPrevisao), hora);
                    break;
                // Critério informado não reconhecido.
                default:
                    return ResponseEntity.badRequest().body("Critério inválido. Utilize BALDEACOES, ESTACOES, TEMPO ou LOTACAO.");
            }
            return ResponseEntity.ok(rota);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao calcular a rota.");
        }
    }
}
