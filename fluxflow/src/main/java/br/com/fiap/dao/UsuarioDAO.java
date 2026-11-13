
package br.com.fiap.dao;

import br.com.fiap.to.UsuarioTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // save()
    // CREATE / INSERT
    public void save(UsuarioTO usuario) {
        // comando sql a ser executado
        String sql = "INSERT INTO USUARIO_FF (ID_USUARIO, NM_USUARIO, DS_EMAIL, DS_SENHAHASH, DT_CADASTRO, ST_USUARIO, PERFIL_ACESSO_ID_PERFIL) VALUES (SEQU_USUARIO_FF.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ? por atributos da classe TO
            ps.setString(1, usuario.getNmUsuario());
            ps.setString(2, usuario.getDsEmail());
            ps.setString(3, usuario.getDsSenhaHash());
            ps.setDate(4, Date.valueOf(usuario.getDtCadastro()));
            ps.setString(5, usuario.getStUsuario());
            ps.setLong(6, usuario.getPerfilAcessoIdPerfil());
            // Execução no banco
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar usuário: " + e.getMessage(), e);
        }
    }

    // findAll()
    // READ / SELECT *
    public List<UsuarioTO> findAll() {
        // comando sql a ser executado
        String sql = "SELECT * FROM USUARIO_FF ORDER BY ID_USUARIO";
        // lista de usuarios
        List<UsuarioTO> usuarios = new ArrayList<>();
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Enquanto tiver usuarios na consulta
            while (rs.next()) {
                // adiciona usuario na lista
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        }
        // retorno de lista
        return usuarios;
    }

    // findById()
    // READ / SELECT * WHERE
    public UsuarioTO findById(Long id) {
        // Comando sql a ser executado
        String sql = "SELECT * FROM USUARIO_FF WHERE ID_USUARIO = ?";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, id);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                // Se encontrar algum usuario
                if (rs.next()) {
                    // retorna o resultado do metodo mapearUsuario() passando o objeto rs
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário: " + e.getMessage(), e);
        }
        return null;
    }

    // findByEmail()
    // Metodo usado para validar login
    public UsuarioTO findByEmail(String email) {
        // comando SQL a ser executado
        String sql = "SELECT * FROM USUARIO_FF WHERE LOWER(DS_EMAIL) = LOWER(?)";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setString(1, email);
            // try-with-resources
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // retorna o resultado do metodo mapearUsuario() passando o objeto rs
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário pelo e-mail: " + e.getMessage(), e);
        }
        return null;
    }

    // update()
    // UPDATE / PUT
    public void update(UsuarioTO usuario) {
        // Comando sql a ser executado
        String sql = "UPDATE USUARIO_FF SET NM_USUARIO = ?, DS_EMAIL = ?, DS_SENHAHASH = ?, ST_USUARIO = ?, PERFIL_ACESSO_ID_PERFIL = ? WHERE ID_USUARIO = ?";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setString(1, usuario.getNmUsuario());
            ps.setString(2, usuario.getDsEmail());
            ps.setString(3, usuario.getDsSenhaHash());
            ps.setString(4, usuario.getStUsuario());
            ps.setLong(5, usuario.getPerfilAcessoIdPerfil());
            ps.setLong(6, usuario.getIdUsuario());
            // Execução de comando
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    // delete()
    // DELETE
    public void delete(Long id) {
        // Comando sql a ser executado
        String sql = "DELETE FROM USUARIO_FF WHERE ID_USUARIO = ?";
        // try-with-resources
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            // Substituindo ?
            ps.setLong(1, id);
            // Execução de comando
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir usuário: " + e.getMessage(), e);
        }
    }

    // Metodo para mapear e retornar um usuário
    // Metodo de apoio a outros metodos
    private UsuarioTO mapearUsuario(ResultSet rs) throws SQLException {
        // novo objeto
        UsuarioTO usuario = new UsuarioTO();
        // Preenchimento de atributos do objeto
        usuario.setIdUsuario(rs.getLong("ID_USUARIO"));
        usuario.setNmUsuario(rs.getString("NM_USUARIO"));
        usuario.setDsEmail(rs.getString("DS_EMAIL"));
        usuario.setDsSenhaHash(rs.getString("DS_SENHAHASH"));
        Date dataCadastro = rs.getDate("DT_CADASTRO");
        // validação
        if (dataCadastro != null) {
            usuario.setDtCadastro(dataCadastro.toLocalDate());
        }
        usuario.setStUsuario(rs.getString("ST_USUARIO"));
        usuario.setPerfilAcessoIdPerfil(rs.getLong("PERFIL_ACESSO_ID_PERFIL"));
        // retorno de objeto
        return usuario;
    }
}
