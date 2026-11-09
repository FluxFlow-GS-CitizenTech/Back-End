package br.com.fiap.bo;

import br.com.fiap.dao.LinhaDAO;
import br.com.fiap.to.LinhaTO;

import java.util.List;

public class LinhaBO {
    // Objeto DAO
    private final LinhaDAO linhaDAO = new LinhaDAO();

    // save()
    public void save(LinhaTO linha) {
        // Verifica se o código já está cadastrado.
        if (linhaDAO.findByCodigo(linha.getDsCodigo()) != null) {
            throw new IllegalArgumentException("Já existe uma linha cadastrada com esse código.");
        }
        // Valida o status.
        validarStatus(linha.getStLinha());
        // Cadastra a linha.
        linhaDAO.save(linha);
    }

    // findAll()
    public List<LinhaTO> findAll() {
        return linhaDAO.findAll();
    }

    // findById()
    public LinhaTO findById(Long id) {
        // novo objeto
        LinhaTO linha = linhaDAO.findById(id);
        // verifica se é nulo
        if (linha == null) {
            throw new IllegalArgumentException("Linha não encontrada.");
        }
        // Retorno da linha
        return linha;
    }

    // update()
    public void update(LinhaTO linha) {
        // Verifica se a linha existe.
        LinhaTO existente = linhaDAO.findById(linha.getIdLinha());
        if (existente == null) {
            throw new IllegalArgumentException("Linha não encontrada.");
        }
        // Verifica se o código pertence a outra linha.
        LinhaTO codigoExistente = linhaDAO.findByCodigo(linha.getDsCodigo());
        if (codigoExistente != null && !codigoExistente.getIdLinha().equals(linha.getIdLinha())) {
            throw new IllegalArgumentException("Já existe outra linha cadastrada com esse código.");
        }
        // Valida o status.
        validarStatus(linha.getStLinha());
        // Atualiza os dados.
        linhaDAO.update(linha);
    }

    // delete()
    public void delete(Long id) {
        // Verifica se a linha existe.
        LinhaTO linha = linhaDAO.findById(id);
        if (linha == null) {
            throw new IllegalArgumentException("Linha não encontrada.");
        }
        // Exclui a linha.
        linhaDAO.delete(id);
    }

    // VALIDAR STATUS
    private void validarStatus(String status) {
        // verifia se o status está dentro do padrão
        if (!"T".equalsIgnoreCase(status) && !"F".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("O status deve ser T (ativo) ou F (inativo).");
        }
    }
}
