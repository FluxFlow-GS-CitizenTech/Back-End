
package br.com.fiap.dao;

import java.sql.*;

public class FonteDadosDAO {

    // Metodo que recebe o ID de uma fonte e verifica se está cadastrada no banco
    public boolean existe(Long idFonte) {
        // Comando sql a ser executado
        String sql = "SELECT COUNT(*) FROM FONTE_DADOS WHERE ID_FONTE = ?";
        // try-with-resources
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, idFonte);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar fonte de dados: " + e.getMessage(), e);
        }
        return false;
    }
}
