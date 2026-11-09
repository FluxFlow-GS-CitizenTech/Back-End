
package br.com.fiap.dao;

import br.com.fiap.to.EstacaoTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstacaoDAO {
    // Cadastra uma nova estação
    // save()
    // CREATE / INSERT
    public void save(EstacaoTO estacao) {
        // Comando sql a ser executado
        String sql = "INSERT INTO ESTACAO (ID_ESTACAO, NM_ESTACAO, ST_ESTACAO) VALUES (SEQU_ESTACAO.NEXTVAL, ?, ?)";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setString(1, estacao.getNmEstacao());
            ps.setString(2, estacao.getStEstacao());
            // Execução de comando
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar estação: " + e.getMessage(), e);
        }
    }

    // Lista todas as estações
    // findAll()
    // READ / SELECT *
    public List<EstacaoTO> findAll() {
        // Comando sql a ser executado
        String sql = "SELECT * FROM ESTACAO ORDER BY NM_ESTACAO";
        // nova lista de estações
        List<EstacaoTO> estacoes = new ArrayList<>();
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Enquanto o rs retornar uma estação
            // essa estação é adicionada à lista
            while (rs.next()) {
                estacoes.add(mapearEstacao(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar estações: " + e.getMessage(), e);
        }
        // Retorno da lista
        return estacoes;
    }

    // Busca uma estação pelo ID
    // findBtId()
    // READ / SELECT * WHERE
    public EstacaoTO findById(Long id) {
        // Comando sql a ser executado
        String sql = "SELECT * FROM ESTACAO WHERE ID_ESTACAO = ?";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, id);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                // Se a estação for encontrada
                if (rs.next()) {
                    return mapearEstacao(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar estação: " + e.getMessage(), e);
        }
        return null;
    }

    // Busca uma estação pelo nome
    // findBYNome()
    public EstacaoTO findByNome(String nome) {
        // Comando sql a ser executado
        String sql = "SELECT * FROM ESTACAO WHERE UPPER(NM_ESTACAO) = UPPER(?)";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setString(1, nome.trim());
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                // Se o comando for executado
                if (rs.next()) {
                    return mapearEstacao(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar estação pelo nome: " + e.getMessage(), e);
        }
        return null;
    }

    // Atualiza os dados de uma estação
    // update()
    // UPDATE
    public void update(EstacaoTO estacao) {
        // Comando sql a ser executado
        String sql = "UPDATE ESTACAO SET NM_ESTACAO = ?, ST_ESTACAO = ? WHERE ID_ESTACAO = ?";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setString(1, estacao.getNmEstacao());
            ps.setString(2, estacao.getStEstacao());
            ps.setLong(3, estacao.getIdEstacao());
            // Execução de comando
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar estação: " + e.getMessage(), e);
        }
    }

    // Exclui uma estação e seus vínculos com linhas
    // delete()
    // DELETE
    public void delete(Long id) {
        // Comando sql que exclui um registro da tabela associativa
        String sqlVinculos = "DELETE FROM LINHA_ESTACAO WHERE ESTACAO_ID_ESTACAO = ?";
        // Comando sql que exclui a estação
        String sqlEstacao = "DELETE FROM ESTACAO WHERE ID_ESTACAO = ?";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection()) {
            // Remove primeiro os vínculos
            try (PreparedStatement ps = connection.prepareStatement(sqlVinculos)) {
                // Substituindo ?
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            // Depois remove a estação
            try (PreparedStatement ps = connection.prepareStatement(sqlEstacao)) {
                // Substituindo ?
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir estação: " + e.getMessage(), e);
        }
    }

    // Lista as linhas vinculadas a uma estação
    public List<Long> findLinhasByEstacao(Long idEstacao) {
        // Comando sql a ser executado
        String sql = "SELECT LINHA_ID_LINHA FROM LINHA_ESTACAO WHERE ESTACAO_ID_ESTACAO = ? ORDER BY LINHA_ID_LINHA";
        // Nova lista de linhas
        List<Long> linhas = new ArrayList<>();
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, idEstacao);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                // Enquanto o objeto retornar uma linha
                // essa linha é adicionada na lista
                while (rs.next()) {
                    linhas.add(rs.getLong("LINHA_ID_LINHA"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar linhas da estação: " + e.getMessage(), e);
        }
        // Retorno de linhas
        return linhas;
    }

    // Adiciona uma linha a uma estação
    public void addLinha(Long idEstacao, Long idLinha) {
        // Comando sql a ser executado
        String sql = "INSERT INTO LINHA_ESTACAO (LINHA_ID_LINHA, ESTACAO_ID_ESTACAO) VALUES (?, ?)";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, idLinha);
            ps.setLong(2, idEstacao);
            // Execução de comando
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao vincular linha à estação: " + e.getMessage(), e);
        }
    }

    // Remove o vínculo entre uma estação e uma linha
    public void removeLinha(Long idEstacao, Long idLinha) {
        // Comando sql a ser executado
        String sql = "DELETE FROM LINHA_ESTACAO WHERE ESTACAO_ID_ESTACAO = ? AND LINHA_ID_LINHA = ?";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, idEstacao);
            ps.setLong(2, idLinha);
            // Execução de comando
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover vínculo da estação: " + e.getMessage(), e);
        }
    }

    // Converte o resultado do banco em EstacaoTO
    // Metodo para auxiliar outros metodos
    private EstacaoTO mapearEstacao(ResultSet rs) throws SQLException {
        // Novo objeto
        EstacaoTO estacao = new EstacaoTO();
        // Preenchimento de atributos
        estacao.setIdEstacao(rs.getLong("ID_ESTACAO"));
        estacao.setNmEstacao(rs.getString("NM_ESTACAO"));
        estacao.setStEstacao(rs.getString("ST_ESTACAO"));
        // retorno de objeto
        return estacao;
    }
}
