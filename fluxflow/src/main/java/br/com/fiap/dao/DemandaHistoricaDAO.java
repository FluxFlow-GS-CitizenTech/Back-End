
package br.com.fiap.dao;

import br.com.fiap.to.DemandaHistoricaTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DemandaHistoricaDAO {

    // Consultar todos os registros históricos
    // findAll()
    // READ / SELECT *
    public List<DemandaHistoricaTO> findAll() {
        // Comando sql a ser executado
        String sql = "SELECT ID_DEMANDA, DT_REFERENCIA, VL_HORAREFERENCIA, NUM_DIASEMANA, QTD_PASSAGEIROS, ESTACAO_ID_ESTACAO, FONTE_DADOS_ID_FONTE, LINHA_ID_LINHA FROM DEMANDA_HISTORICA ORDER BY DT_REFERENCIA, VL_HORAREFERENCIA ";
        // Nova lista de demandas
        List<DemandaHistoricaTO> lista = new ArrayList<>();
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Enquanto o sql retornar uma linha
            // ela é adicionada na lista
            while (rs.next()) {
                lista.add(mapearDemanda(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar demandas históricas: " + e.getMessage(), e);
        }
        // Retorno de lista
        return lista;
    }

    // Consultar um registro pelo ID
    // findById()
    // READ / SELECT * WHERE
    public DemandaHistoricaTO findById(Long idDemanda) {
        // Comando sql a ser executado
        String sql = "SELECT ID_DEMANDA, DT_REFERENCIA, VL_HORAREFERENCIA, NUM_DIASEMANA, QTD_PASSAGEIROS, ESTACAO_ID_ESTACAO, FONTE_DADOS_ID_FONTE, LINHA_ID_LINHA FROM DEMANDA_HISTORICA WHERE ID_DEMANDA = ?";
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, idDemanda);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                // Se executar retorna a demanda mapeada
                if (rs.next()) {
                    return mapearDemanda(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar demanda histórica: " + e.getMessage(), e);
        }
        return null;
    }

    // Consultar histórico de uma estação
    // findByEstacao()
    // READ / SELECT * WHERE
    public List<DemandaHistoricaTO> findByEstacao(Long idEstacao) {
        String sql = "SELECT ID_DEMANDA, DT_REFERENCIA, VL_HORAREFERENCIA, NUM_DIASEMANA, QTD_PASSAGEIROS, ESTACAO_ID_ESTACAO, FONTE_DADOS_ID_FONTE, LINHA_ID_LINHA FROM DEMANDA_HISTORICA WHERE ESTACAO_ID_ESTACAO = ? ORDER BY DT_REFERENCIA, VL_HORAREFERENCIA ";
        // Lista de demandas
        List<DemandaHistoricaTO> lista = new ArrayList<>();
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, idEstacao);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                // Enquanto o banco retornar uma linha
                // ela é adicionada a lista
                while (rs.next()) {
                    lista.add(mapearDemanda(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar histórico da estação: " + e.getMessage(), e);
        }
        // Retorno de lista
        return lista;
    }

    // Calcular média histórica de passageiros
    // calcularMediaHistorica()
    // SELECT AVG()
    public Double calcularMediaHistorica(Long idEstacao, Long idLinha, Integer diaSemana, Integer hora) {
        // Comando sql a ser executado
        String sql = "SELECT AVG(QTD_PASSAGEIROS) FROM DEMANDA_HISTORICA WHERE ESTACAO_ID_ESTACAO = ? AND LINHA_ID_LINHA = ? AND NUM_DIASEMANA = ? AND VL_HORAREFERENCIA = ?";
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, idEstacao);
            ps.setLong(2, idLinha);
            ps.setInt(3, diaSemana);
            ps.setInt(4, hora);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                // Se executar
                if (rs.next()) {
                    // Armazena e retorna a média se não for nula
                    double media = rs.getDouble(1);
                    return rs.wasNull() ? null : media;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular média histórica: " + e.getMessage(), e);
        }
        return null;
    }

    // Consultar a maior média histórica de uma estação e linha
    // calcularMaiorMediaHistorica()
    public Double calcularMaiorMediaHistorica(Long idEstacao, Long idLinha) {
        // Comando sql a ser executado
        String sql = "SELECT MAX(MEDIA_DEMANDA) FROM (SELECT AVG(QTD_PASSAGEIROS) AS MEDIA_DEMANDA FROM DEMANDA_HISTORICA WHERE ESTACAO_ID_ESTACAO = ? AND LINHA_ID_LINHA = ? GROUP BY NUM_DIASEMANA, VL_HORAREFERENCIA)";
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // substituindo ?
            ps.setLong(1, idEstacao);
            ps.setLong(2, idLinha);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                // Se executar
                if (rs.next()) {
                    // Armazena a maior média e retorna ela se não for nula
                    double maiorMedia = rs.getDouble(1);
                    return rs.wasNull() ? null : maiorMedia;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao calcular maior média histórica: " + e.getMessage(), e
            );
        }
        return null;
    }

    // Converter resultado do banco para TO
    // mapearDemanda()
    // Metodo de apoio a outros metodos
    private DemandaHistoricaTO mapearDemanda(ResultSet rs) throws SQLException {
        // Novo objeto demanda
        DemandaHistoricaTO demanda = new DemandaHistoricaTO();
        // Preenchimento de atributos
        demanda.setIdDemanda(rs.getLong("ID_DEMANDA"));
        demanda.setDtReferencia(rs.getDate("DT_REFERENCIA").toLocalDate());
        demanda.setVlHoraReferencia(rs.getInt("VL_HORAREFERENCIA"));
        demanda.setNumDiaSemana(rs.getInt("NUM_DIASEMANA"));
        int passageiros = rs.getInt("QTD_PASSAGEIROS");
        demanda.setQtdPassageiros(rs.wasNull() ? null : passageiros);
        demanda.setEstacaoIdEstacao(rs.getLong("ESTACAO_ID_ESTACAO"));
        demanda.setFonteDadosIdFonte(rs.getLong("FONTE_DADOS_ID_FONTE"));
        demanda.setLinhaIdLinha(rs.getLong("LINHA_ID_LINHA"));
        // Retorno de objeto
        return demanda;
    }
}
