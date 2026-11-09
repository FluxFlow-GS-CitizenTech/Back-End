
package br.com.fiap.bo;

import br.com.fiap.dao.EstacaoDAO;
import br.com.fiap.to.EstacaoTO;

import java.util.List;

public class EstacaoBO {
    // Objeto DAO
    private final EstacaoDAO estacaoDAO = new EstacaoDAO();

    // Cadastra uma nova estação
    // save()
    public void save(EstacaoTO estacao) {
        // Verifica se a estação já existe
        if (estacaoDAO.findByNome(estacao.getNmEstacao()) != null) {
            throw new IllegalArgumentException("Já existe uma estação cadastrada com esse nome.");
        }
        // Valida o status
        validarStatus(estacao.getStEstacao());
        // Cria estação
        estacaoDAO.save(estacao);
    }

    // Lista todas as estações
    // findAll()
    public List<EstacaoTO> findAll() {
        return estacaoDAO.findAll();
    }

    // Busca uma estação pelo ID
    // findById()
    public EstacaoTO findById(Long id) {
        // Objeto TO
        EstacaoTO estacao = estacaoDAO.findById(id);
        // Verifica de não é nulo
        if (estacao == null) {
            throw new IllegalArgumentException("Estação não encontrada.");
        }
        // retorna a estação
        return estacao;
    }

    // Atualiza uma estação
    // update()
    public void update(EstacaoTO estacao) {
        // Objeto TO
        // valida se a estação existe no banco
        EstacaoTO existente = estacaoDAO.findById(estacao.getIdEstacao());
        if (existente == null) {
            throw new IllegalArgumentException("Estação não encontrada.");
        }
        // Verifica se o nome já existe no banco
        EstacaoTO nomeExistente = estacaoDAO.findByNome(estacao.getNmEstacao());
        if (nomeExistente != null && !nomeExistente.getIdEstacao().equals(estacao.getIdEstacao())) {
            throw new IllegalArgumentException("Já existe outra estação cadastrada com esse nome.");
        }
        // Valida o status
        validarStatus(estacao.getStEstacao());
        // Atualiza estação
        estacaoDAO.update(estacao);
    }

    // Exclui uma estação
    // delete()
    public void delete(Long id) {
        // Objeto TO
        // Verifica se exixte no banco
        EstacaoTO estacao = estacaoDAO.findById(id);
        if (estacao == null) {
            throw new IllegalArgumentException("Estação não encontrada.");
        }
        // Exclui a estação
        estacaoDAO.delete(id);
    }

    // Lista os IDs das linhas vinculadas à estação
    // findLinhasByEstacao()
    public List<Long> findLinhasByEstacao(Long idEstacao) {
        // Acha a estação
        findById(idEstacao);
        // retorna as linhas relacionadas
        return estacaoDAO.findLinhasByEstacao(idEstacao);
    }

    // Vincula uma linha à estação
    // addLinha()
    public void addLinha(Long idEstacao, Long idLinha) {
        // Acha a estação
        findById(idEstacao);
        // Verifica se a linha existe
        LinhaBO linhaBO = new LinhaBO();
        linhaBO.findById(idLinha);

        // Verifica se o vínculo já existe
        if (estacaoDAO.findLinhasByEstacao(idEstacao).contains(idLinha)) {
            throw new IllegalArgumentException("Essa linha já está vinculada à estação.");
        }
        // Popula a tabela associativa
        estacaoDAO.addLinha(idEstacao, idLinha);
    }

    // Remove o vínculo entre uma linha e uma estação
    // removeLinha()
    public void removeLinha(Long idEstacao, Long idLinha) {
        // Acha a estação
        findById(idEstacao);
        // Verifica se a linha existe
        LinhaBO linhaBO = new LinhaBO();
        linhaBO.findById(idLinha);
        // Verifica se há vínculo
        if (!estacaoDAO.findLinhasByEstacao(idEstacao).contains(idLinha)) {
            throw new IllegalArgumentException("Essa linha não está vinculada à estação.");
        }
        // Remove o vínculo da tabela associativa
        estacaoDAO.removeLinha(idEstacao, idLinha);
    }

    // Valida o status da estação
    // validarStatus()
    private void validarStatus(String status) {
        // Verifica se o status é diferente de T ou F
        if (!"T".equalsIgnoreCase(status) && !"F".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("O status deve ser T (ativo) ou F (inativo).");
        }
    }
}
