
package br.com.fiap.bo;

import br.com.fiap.dao.*;
import br.com.fiap.to.StatusOperacionalTO;

import java.time.LocalDateTime;
import java.util.List;

public class StatusOperacionalBO {
    // Objetos DAO
    private final StatusOperacionalDAO dao = new StatusOperacionalDAO();
    private final LinhaDAO linhaDAO = new LinhaDAO();
    private final EstacaoDAO estacaoDAO = new EstacaoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final FonteDadosDAO fonteDadosDAO = new FonteDadosDAO();

    // save()
    public StatusOperacionalTO save(StatusOperacionalTO status) {
        // Valida o status da sessão
        validarStatus(status);
        normalizarDados(status);
        validarOrigem(status);
        validarRelacionamentos(status);
        // Validação de datas
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
        // Verifica se é nulo
        if (status == null) {
            throw new IllegalArgumentException("Os dados do status operacional são obrigatórios.");
        }
        // Acha pelo id
        findById(status.getIdStatus());
        // Valida
        validarStatus(status);
        normalizarDados(status);
        validarOrigem(status);
        validarRelacionamentos(status);
        // Validação de datas
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
        // Verifica se a situação é válida
        if (!List.of("EM_ANDAMENTO", "RESOLVIDA", "CANCELADA").contains(status.getDsSituacao().trim().toUpperCase())) {
            throw new IllegalArgumentException("Situação da ocorrência inválida.");
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

    // validarRelacionamentos
    private void validarRelacionamentos(StatusOperacionalTO status) {
        // Valida a linha
        if (linhaDAO.findById(status.getLinhaIdLinha()) == null) {
            throw new IllegalArgumentException("Linha informada não encontrada.");
        }

        // Valida a estação, caso tenha sido informada
        if (status.getEstacaoIdEstacao() != null) {

            if (estacaoDAO.findById(status.getEstacaoIdEstacao()) == null) {
                throw new IllegalArgumentException("Estação informada não encontrada.");
            }

            // Verifica se a estação pertence à linha informada
            List<Long> linhasDaEstacao = estacaoDAO.findLinhasByEstacao(status.getEstacaoIdEstacao());

            if (!linhasDaEstacao.contains(status.getLinhaIdLinha())) {
                throw new IllegalArgumentException("A estação informada não pertence à linha selecionada.");
            }
        }


        // Valida o usuário, caso tenha sido informado
        if (status.getUsuarioFfIdUsuario() != null) {

            if (usuarioDAO.findById(status.getUsuarioFfIdUsuario()) == null) {
                throw new IllegalArgumentException("Usuário responsável não encontrado.");
            }
        }

        // Valida a fonte, caso tenha sido informada
        if (status.getFonteDadosIdFonte() != null) {

            if (!fonteDadosDAO.existe(status.getFonteDadosIdFonte())) {
                throw new IllegalArgumentException("Fonte de dados não encontrada.");
            }
        }
    }

    // validarOrigem()
    private void validarOrigem(StatusOperacionalTO status) {
        // Armazena a origem do objeto
        String origem = status.getDsOrigem().trim().toUpperCase();
        // Verificação
        switch (origem) {
            // MANUAL
            case "MANUAL":
                // ID_USUARIO não pode ser null
                if (status.getUsuarioFfIdUsuario() == null) {
                    throw new IllegalArgumentException("Registros MANUAL precisam de um usuário responsável.");
                }
                // ID_FONTE não pode ser nulo
                if (status.getFonteDadosIdFonte() != null) {
                    throw new IllegalArgumentException("Registros MANUAL não podem possuir fonte de dados.");
                }
                break;
            // EXTERNA
            case "EXTERNA":
                // ID_FONTE não pode ser nulo
                if (status.getFonteDadosIdFonte() == null) {
                    throw new IllegalArgumentException("Registros EXTERNA precisam de uma fonte de dados.");
                }
                // ID_USUARIO não pode ser null
                if (status.getUsuarioFfIdUsuario() != null) {
                    throw new IllegalArgumentException("Registros EXTERNA não podem possuir usuário responsável.");
                }
                break;
            // SIMULADA
            case "SIMULADA":
                // ID_FONTE não pode ser nulo e ID_USUARIO não pode ser null
                if (status.getUsuarioFfIdUsuario() != null || status.getFonteDadosIdFonte() != null) {
                    throw new IllegalArgumentException("Registros SIMULADA não podem possuir usuário ou fonte de dados.");
                }
                break;
            default:
                throw new IllegalArgumentException("Origem inválida.");
        }
    }

    // normalizarDados()
    private void normalizarDados(StatusOperacionalTO status) {
        // Normalização
        status.setTpOcorrencia(status.getTpOcorrencia().trim().toUpperCase());
        status.setDsOrigem(status.getDsOrigem().trim().toUpperCase());
        status.setDsSituacao(status.getDsSituacao().trim().toUpperCase());
    }
}
