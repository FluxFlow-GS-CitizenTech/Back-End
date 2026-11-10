
package br.com.fiap.bo;

import br.com.fiap.dao.StatusOperacionalDAO;
import br.com.fiap.to.StatusOperacionalTO;

import java.time.LocalDateTime;
import java.util.List;

public class StatusOperacionalBO {
    // Objeto DAO
    private final StatusOperacionalDAO dao = new StatusOperacionalDAO();

    // save()
    public StatusOperacionalTO save(StatusOperacionalTO status) {
        // Valida o status da sessão
        validarStatus(status);
        // Validação de datas
        if (status.getDtInicio().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data de início não pode ser futura.");
        }
        if (status.getDtFim() != null && status.getDtFim().isBefore(status.getDtInicio())) {
            throw new IllegalArgumentException("A data de término não pode ser anterior ao início.");
        }

        // Save status
        return dao.save(status);
    }

    // findAll()
    public List<StatusOperacionalTO> findAll() {
        return dao.findAll();
    }

    // findBId()
    public StatusOperacionalTO findById(Long id) {
        // Objeto DAO
        StatusOperacionalTO status = dao.findById(id);
        // Verifca se é nulo
        if (status == null) {
            throw new IllegalArgumentException("Status operacional não encontrado.");
        }
        // Retorno de objeto
        return status;
    }

    // update()
    public StatusOperacionalTO update(StatusOperacionalTO status) {
        // Acha pelo id
        findById(status.getIdStatus());
        // Valida
        validarStatus(status);
        // Validação de datas
        if (status.getDtInicio().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data de início não pode ser futura.");
        }
        if (status.getDtFim() != null && status.getDtFim().isBefore(status.getDtInicio())) {
            throw new IllegalArgumentException("A data de término não pode ser anterior ao início.");
        }

        // Update status
        return dao.update(status);
    }

    // delete()
    public void delete(Long id) {
        findById(id);
        dao.delete(id);
    }

    // validarStatus()
    private void validarStatus(StatusOperacionalTO status) {

        // Verifica se é nulo
        if (status == null) {
            throw new IllegalArgumentException("Os dados do status operacional são obrigatórios.");
        }
        // Verifica se o tipo de ocorrência é válido
        if (status.getTpOcorrencia() == null || !List.of("ATRASO", "INTERRUPCAO", "FALHA_TECNICA", "MANUTENCAO").contains(status.getTpOcorrencia().trim().toUpperCase())) {
            throw new IllegalArgumentException("Tipo de ocorrência inválido.");
        }
        // Verifica se a origem é válida
        if (status.getDsOrigem() == null || !List.of("MANUAL", "EXTERNA", "SIMULADA").contains(status.getDsOrigem().trim().toUpperCase())) {
            throw new IllegalArgumentException("Origem da informação inválida.");
        }
        // Verifica se a situação está em branco
        if (status.getDsSituacao() == null || status.getDsSituacao().isBlank()) {
            throw new IllegalArgumentException("A situação da ocorrência é obrigatória.");
        }
        // Verifica se a descrisção é nula
        if (status.getDsStatus() == null || status.getDsStatus().isBlank()) {
            throw new IllegalArgumentException("A descrição da ocorrência é obrigatória.");
        }
        // Verifica se a data de inicio é nula
        if (status.getDtInicio() == null) {
            throw new IllegalArgumentException("A data de início é obrigatória.");
        }
        // Verifica se a FK de LINHA é nula
        if (status.getLinhaIdLinha() == null) {
            throw new IllegalArgumentException("A linha relacionada é obrigatória.");
        }
    }
}
