package br.com.fiap.dao;

import br.com.fiap.to.LinhaTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LinhaDAO {

    // CADASTRAR LINHA
    // save()
    // CREATE / INSERT
    public void save(LinhaTO linha) {
        // Comando sql a ser executado
        String sql = "INSERT INTO LINHA (ID_LINHA, DS_CODIGO, NM_LINHA, DS_COR, TP_OPERADOR, ST_LINHA) VALUES (SEQU_LINHA.NEXTVAL, ?, ?, ?, ?, ?)";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setString(1, linha.getDsCodigo());
            ps.setString(2, linha.getNmLinha());
            ps.setString(3, linha.getDsCor());
            ps.setString(4, linha.getTpOperador());
            ps.setString(5, linha.getStLinha());
            // Execução de comando
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar linha: " + e.getMessage(), e);
        }
    }

    // LISTAR TODAS AS LINHAS
    // findAll()
    // READ / SELECT *
    public List<LinhaTO> findAll() {
        // Comando sql a ser executado
        String sql = "SELECT * FROM LINHA ORDER BY ID_LINHA";
        // Nova lista de linhas
        List<LinhaTO> linhas = new ArrayList<>();
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Enquanto o objeto rs retornar linhas da consulta
            // Adiciona essa linha a lista
            while (rs.next()) {
                linhas.add(mapearLinha(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar linhas: " + e.getMessage(), e);
        }
        // Retorno de lista
        return linhas;
    }

    // BUSCAR LINHA POR ID
    // findById()
    // READ / SELECT * WHERE
    public LinhaTO findById(Long id) {
        // Comando sql a ser executado
        String sql = "SELECT * FROM LINHA WHERE ID_LINHA = ?";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, id);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                // Se retornar uma linha
                if (rs.next()) {
                    return mapearLinha(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar linha: " + e.getMessage(), e);
        }
        return null;
    }

    // ATUALIZAR LINHA
    // update()
    // UPDATE
    public void update(LinhaTO linha) {
        // Comando sql a ser executado
        String sql = "UPDATE LINHA SET DS_CODIGO = ?, NM_LINHA = ?, DS_COR = ?, TP_OPERADOR = ?, ST_LINHA = ? WHERE ID_LINHA = ?";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setString(1, linha.getDsCodigo());
            ps.setString(2, linha.getNmLinha());
            ps.setString(3, linha.getDsCor());
            ps.setString(4, linha.getTpOperador());
            ps.setString(5, linha.getStLinha());
            ps.setLong(6, linha.getIdLinha());
            // Execução de comando
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar linha: " + e.getMessage(), e);
        }
    }

    // EXCLUIR LINHA
    // delete()
    // DELETE
    public void delete(Long id) {
        // Comnado sql a ser executado
        String sql = "DELETE FROM LINHA WHERE ID_LINHA = ?";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, id);
            // Execução de comando
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir linha: " + e.getMessage(), e);
        }
    }

    // MAPEAR RESULTADO DO BANCO PARA LINHATO
    // Metodo de auxílio para outros metodos
    private LinhaTO mapearLinha(ResultSet rs) throws SQLException {
        // novo objeto linha
        LinhaTO linha = new LinhaTO();
        // Preenchimento de atributos
        linha.setIdLinha(rs.getLong("ID_LINHA"));
        linha.setDsCodigo(rs.getString("DS_CODIGO"));
        linha.setNmLinha(rs.getString("NM_LINHA"));
        linha.setDsCor(rs.getString("DS_COR"));
        linha.setTpOperador(rs.getString("TP_OPERADOR"));
        linha.setStLinha(rs.getString("ST_LINHA"));
        // Retorno de objeto
        return linha;
    }

    // BUSCAR LINHA PELO CÓDIGO
    // Metodo de auxílio para outros metodos
    public LinhaTO findByCodigo(String codigo) {
        // Comando sql a ser executado
        String sql = "SELECT * FROM LINHA WHERE UPPER(DS_CODIGO) = UPPER(?)";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setString(1, codigo.trim());
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearLinha(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar linha pelo código: " + e.getMessage(), e);
        }
        return null;
    }
}
