
package br.com.fiap.dao;

import br.com.fiap.to.StatusOperacionalTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatusOperacionalDAO {

    // save()
    // CREATE / INSERT
    public StatusOperacionalTO save(StatusOperacionalTO status) {
        // Comando sql a ser executado
        String sql = "INSERT INTO STATUS_OPERACIONAL (ID_STATUS, TP_OCORRENCIA, DS_STATUS, DS_SITUACAO, DT_INICIO, DT_FIM, DS_ORIGEM, LINHA_ID_LINHA, ESTACAO_ID_ESTACAO, USUARIO_FF_ID_USUARIO, FONTE_DADOS_ID_FONTE) VALUES (SEQU_STATUS.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID_STATUS"})) {
            // Substituindo ?
            ps.setString(1, status.getTpOcorrencia());
            ps.setString(2, status.getDsStatus());
            ps.setString(3, status.getDsSituacao());
            ps.setTimestamp(4, Timestamp.valueOf(status.getDtInicio()));
            // Verifica se a data de fim não é nula
            if (status.getDtFim() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(status.getDtFim()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            ps.setString(6, status.getDsOrigem());
            ps.setLong(7, status.getLinhaIdLinha());
            // Verifica se as FKs são nulas
            if (status.getEstacaoIdEstacao() != null) {
                ps.setLong(8, status.getEstacaoIdEstacao());
            } else {
                ps.setNull(8, Types.NUMERIC);
            }
            if (status.getUsuarioFfIdUsuario() != null) {
                ps.setLong(9, status.getUsuarioFfIdUsuario());
            } else {
                ps.setNull(9, Types.NUMERIC);
            }
            if (status.getFonteDadosIdFonte() != null) {
                ps.setLong(10, status.getFonteDadosIdFonte());
            } else {
                ps.setNull(10, Types.NUMERIC);
            }

            // Execução de comando
            ps.executeUpdate();
            // try-with-resources
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    status.setIdStatus(rs.getLong(1));
                }
            }
            // retorno de objeto
            return status;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar status operacional: " + e.getMessage(), e);
        }
    }

    // findAll()
    // READ / SELECT *
    public List<StatusOperacionalTO> findAll() {
        // Comando sql a ser executado
        String sql = "SELECT * FROM STATUS_OPERACIONAL ORDER BY DT_INICIO DESC";
        // Nova lista de status
        List<StatusOperacionalTO> lista = new ArrayList<>();
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Enquanto ouver linhas do select
            // Adiciona essa linha na lista
            while (rs.next()) {
                lista.add(mapearStatus(rs));
            }
            // Retorno da lista
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar status operacionais: " + e.getMessage(), e);
        }
    }

    // findById()
    // READ / SELECT * WHERE
    public StatusOperacionalTO findById(Long id) {
        // Comando sql a ser executado
        String sql = "SELECT * FROM STATUS_OPERACIONAL WHERE ID_STATUS = ?";
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, id);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearStatus(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar status operacional: " + e.getMessage(), e);
        }
    }

    // update()
    // UPDATE
    public StatusOperacionalTO update(StatusOperacionalTO status) {
        // Comando sql a ser executado
        String sql = "UPDATE STATUS_OPERACIONAL SET TP_OCORRENCIA = ?,DS_STATUS = ?, DS_SITUACAO = ?, DT_INICIO = ?, DT_FIM = ?, DS_ORIGEM = ?, LINHA_ID_LINHA = ?, ESTACAO_ID_ESTACAO = ?, USUARIO_FF_ID_USUARIO = ?, FONTE_DADOS_ID_FONTE = ? WHERE ID_STATUS = ?";
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Substituindo ?
            ps.setString(1, status.getTpOcorrencia());
            ps.setString(2, status.getDsStatus());
            ps.setString(3, status.getDsSituacao());
            ps.setTimestamp(4, Timestamp.valueOf(status.getDtInicio()));
            if (status.getDtFim() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(status.getDtFim()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            ps.setString(6, status.getDsOrigem());
            ps.setLong(7, status.getLinhaIdLinha());
            if (status.getEstacaoIdEstacao() != null) {
                ps.setLong(8, status.getEstacaoIdEstacao());
            } else {
                ps.setNull(8, Types.NUMERIC);
            }
            if (status.getUsuarioFfIdUsuario() != null) {
                ps.setLong(9, status.getUsuarioFfIdUsuario());
            } else {
                ps.setNull(9, Types.NUMERIC);
            }
            if (status.getFonteDadosIdFonte() != null) {
                ps.setLong(10, status.getFonteDadosIdFonte());
            } else {
                ps.setNull(10, Types.NUMERIC);
            }
            ps.setLong(11, status.getIdStatus());
            // Execução de comando
            int linhasAfetadas = ps.executeUpdate();
            // Se não executar
            if (linhasAfetadas == 0) {
                return null;
            }
            // Retorno de onjeto
            return status;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status operacional: " + e.getMessage(), e);
        }
    }

    // delete()
    // DELETE
    public void delete(Long id) {
        // Comando sql a ser executado
        String sql = "DELETE FROM STATUS_OPERACIONAL WHERE ID_STATUS = ?";
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, id);
            // Execução de comando
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir status operacional: " + e.getMessage(), e);
        }
    }

    // mapearStatus()
    // Metodo de apoio a outros metodos
    private StatusOperacionalTO mapearStatus(ResultSet rs) throws SQLException {
        // Objeto TO
        StatusOperacionalTO status = new StatusOperacionalTO();
        // Preenchimento de atributos
        status.setIdStatus(rs.getLong("ID_STATUS"));
        status.setTpOcorrencia(rs.getString("TP_OCORRENCIA"));
        status.setDsStatus(rs.getString("DS_STATUS"));
        status.setDsSituacao(rs.getString("DS_SITUACAO"));

        Timestamp inicio = rs.getTimestamp("DT_INICIO");
        if (inicio != null) {
            status.setDtInicio(inicio.toLocalDateTime());
        }
        Timestamp fim = rs.getTimestamp("DT_FIM");
        if (fim != null) {
            status.setDtFim(fim.toLocalDateTime());
        }
        status.setDsOrigem(rs.getString("DS_ORIGEM"));
        status.setLinhaIdLinha(rs.getLong("LINHA_ID_LINHA"));
        long idEstacao = rs.getLong("ESTACAO_ID_ESTACAO");
        if (!rs.wasNull()) {
            status.setEstacaoIdEstacao(idEstacao);
        }
        long idUsuario = rs.getLong("USUARIO_FF_ID_USUARIO");
        if (!rs.wasNull()) {
            status.setUsuarioFfIdUsuario(idUsuario);
        }
        long idFonte = rs.getLong("FONTE_DADOS_ID_FONTE");
        if (!rs.wasNull()) {
            status.setFonteDadosIdFonte(idFonte);
        }

        // Retorno de objeto
        return status;
    }
}
