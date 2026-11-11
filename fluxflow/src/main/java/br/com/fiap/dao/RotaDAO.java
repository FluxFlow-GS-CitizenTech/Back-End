
package br.com.fiap.dao;

import br.com.fiap.to.ConexaoTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RotaDAO {

    // Busca as conexões entre estações consecutivas de cada linha
    // findConexoes()
    // READ / SUBSELECT
    public List<ConexaoTO> findConexoes() {
        // Comando sql a ser executado
        String sql = "SELECT ID_LINHA, ID_ESTACAO_ATUAL, ID_ESTACAO_PROXIMA FROM (SELECT ID_LINHA, ID_ESTACAO AS ID_ESTACAO_ATUAL, LEAD(ID_ESTACAO) OVER (PARTITION BY ID_LINHA ORDER BY DS_ORDEM ) AS ID_ESTACAO_PROXIMA FROM LINHA_ESTACAO) WHERE ID_ESTACAO_PROXIMA IS NOT NULL";
        // Nova lista de conexoes
        List<ConexaoTO> conexoes = new ArrayList<>();
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Enquanto o banco retornar uma linha
            while (rs.next()) {
                // Objeto TO
                ConexaoTO conexao = new ConexaoTO();
                // Preenchimento de atributos
                conexao.setIdLinha(rs.getLong("ID_LINHA"));
                conexao.setIdEstacaoOrigem(rs.getLong("ID_ESTACAO_ATUAL"));
                conexao.setIdEstacaoDestino(rs.getLong("ID_ESTACAO_PROXIMA"));
                // Adição de objeto a lista
                conexoes.add(conexao);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar conexões entre estações: " + e.getMessage(), e);
        }
        // Retorno da lista
        return conexoes;
    }
}
