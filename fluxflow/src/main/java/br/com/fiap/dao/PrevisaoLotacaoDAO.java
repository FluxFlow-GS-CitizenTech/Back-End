
package br.com.fiap.dao;

import br.com.fiap.to.PrevisaoLotacaoTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrevisaoLotacaoDAO {

    // Cadastrar previsão
    // save()
    // CREATE / INSERT
    public PrevisaoLotacaoTO save(PrevisaoLotacaoTO previsao) {
        // Comando sql a ser executado
        String sql = "INSERT INTO PREVISAO_LOTACAO (ID_PREVISAO, DT_PREVISAO, VL_HORAREFERENCIA, VL_INDICELOTACAO, DS_CLASSIFICACAO, DS_MODELOUTILIZADO, DT_GERACAO, ESTACAO_ID_ESTACAO, LINHA_ID_LINHA) VALUES (SEQU_PREVISAO.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID_PREVISAO"})) {
            // Substituindo ?
            ps.setDate(1, Date.valueOf(previsao.getDtPrevisao()));
            ps.setInt(2, previsao.getVlHoraReferencia());
            ps.setInt(3, previsao.getVlIndiceLotacao());
            ps.setString(4, previsao.getDsClassificacao());
            ps.setString(5, previsao.getDsModeloUtilizado());
            ps.setTimestamp(6, Timestamp.valueOf(previsao.getDtGeracao()));
            ps.setLong(7, previsao.getEstacaoIdEstacao());
            ps.setLong(8, previsao.getLinhaIdLinha());
            // Execução de comando
            ps.executeUpdate();
            // try-with-resources
            try (ResultSet rs = ps.getGeneratedKeys()) {
                // Se o getGeneratedKeys exeutar, armazena o idPrevisão
                if (rs.next()) {
                    previsao.setIdPrevisao(rs.getLong(1));
                }
            }
            // retorno de objeto
            return previsao;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar previsão de lotação: " + e.getMessage(), e);
        }
    }

    // Consultar todas as previsões
    // findAll()
    // READ / SELECT *
    public List<PrevisaoLotacaoTO> findAll() {
        // Comando sql a ser executado
        String sql = "SELECT * FROM PREVISAO_LOTACAO ORDER BY DT_PREVISAO DESC, VL_HORAREFERENCIA ";
        // Lista de previsões
        List<PrevisaoLotacaoTO> lista = new ArrayList<>();
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Enquando o banco retornar linha
            // essa linha é adicionada a lista
            while (rs.next()) {
                lista.add(mapearPrevisao(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar previsões: " + e.getMessage(), e);
        }
        // Retorno da lista
        return lista;
    }

    // Consultar previsão pelo ID
    // finsById()
    // READ / SELECT * WHERE
    public PrevisaoLotacaoTO findById(Long idPrevisao) {
        // Comando sql a ser executado
        String sql = "SELECT * FROM PREVISAO_LOTACAO WHERE ID_PREVISAO = ?";
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, idPrevisao);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                // Se executar retorna o objeto mapeado
                if (rs.next()) {
                    return mapearPrevisao(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar previsão: " + e.getMessage(), e);
        }
        return null;
    }

    // Consultar previsões de uma estação
    // findByEstacao()
    // READ / SELECT WHERE
    public List<PrevisaoLotacaoTO> findByEstacao(Long idEstacao) {
        // Comando sql a ser executado
        String sql = "SELECT * FROM PREVISAO_LOTACAO WHERE ESTACAO_ID_ESTACAO = ?ORDER BY DT_PREVISAO DESC, VL_HORAREFERENCIA ";
        // Lista de previsões
        List<PrevisaoLotacaoTO> lista = new ArrayList<>();
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, idEstacao);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                // Enquanto o banco retornar linha
                // essa linha é adicionada a lista
                while (rs.next()) {
                    lista.add(mapearPrevisao(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar previsões da estação: " + e.getMessage(), e);
        }
        // Retorno da lista
        return lista;
    }

    // Consultar previsões de uma data
    // findByData()
    // READ / SELECT WHERE
    public List<PrevisaoLotacaoTO> findByData(Date data) {
        // Comando sql a ser executado
        String sql = "SELECT * FROM PREVISAO_LOTACAO WHERE DT_PREVISAO = ?ORDER BY VL_HORAREFERENCIA ";
        // Lista de previsões
        List<PrevisaoLotacaoTO> lista = new ArrayList<>();
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Substituindo ?
            ps.setDate(1, data);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                // Enquanto o banco retornar uma linha
                // essa linha é adicionada a lista
                while (rs.next()) {
                    lista.add(mapearPrevisao(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao consultar previsões por data: " + e.getMessage(), e
            );
        }
        // retorno da lista
        return lista;
    }

    // Converter resultado do banco para TO
    // mapearPrevisao()
    // Metodo de apoio a outros metodos
    private PrevisaoLotacaoTO mapearPrevisao(ResultSet rs) throws SQLException {
        // Objeto previsão
        PrevisaoLotacaoTO previsao = new PrevisaoLotacaoTO();
        // Preenchimento de atributos
        previsao.setIdPrevisao(rs.getLong("ID_PREVISAO"));
        previsao.setDtPrevisao(rs.getDate("DT_PREVISAO").toLocalDate());
        previsao.setVlHoraReferencia(rs.getInt("VL_HORAREFERENCIA"));
        previsao.setVlIndiceLotacao(rs.getInt("VL_INDICELOTACAO"));
        previsao.setDsClassificacao(rs.getString("DS_CLASSIFICACAO"));
        previsao.setDsModeloUtilizado(rs.getString("DS_MODELOUTILIZADO"));
        previsao.setDtGeracao(rs.getTimestamp("DT_GERACAO").toLocalDateTime());
        previsao.setEstacaoIdEstacao(rs.getLong("ESTACAO_ID_ESTACAO"));
        previsao.setLinhaIdLinha(rs.getLong("LINHA_ID_LINHA"));
        // Retorno de objeto
        return previsao;
    }
}
