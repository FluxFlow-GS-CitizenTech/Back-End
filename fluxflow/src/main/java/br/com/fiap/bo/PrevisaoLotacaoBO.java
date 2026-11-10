
package br.com.fiap.bo;

import br.com.fiap.dao.DemandaHistoricaDAO;
import br.com.fiap.dao.PrevisaoLotacaoDAO;
import br.com.fiap.to.PrevisaoLotacaoTO;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PrevisaoLotacaoBO {
    // Objetos DAO
    private final DemandaHistoricaDAO demandaDAO = new DemandaHistoricaDAO();
    private final PrevisaoLotacaoDAO previsaoDAO = new PrevisaoLotacaoDAO();

    // Gerar previsão de lotação
    // gerarPrevisao()
    public PrevisaoLotacaoTO gerarPrevisao(LocalDate dataPrevisao, Integer hora, Long idEstacao, Long idLinha) {
        // Faz a validação dos parâmetros antes de utilizá-los
        validarParametros(dataPrevisao, hora, idEstacao, idLinha);
        // Identificar dia da semana
        int diaSemana = dataPrevisao.getDayOfWeek().getValue();
        // Consultar média histórica
        Double mediaDemanda = demandaDAO.calcularMediaHistorica(idEstacao, idLinha, diaSemana, hora);
        // Verifica se a média calculada é nula
        if (mediaDemanda == null) {
            throw new IllegalArgumentException("Não existem dados históricos para essa combinação.");
        }
        // Consultar maior média histórica da estação e linha
        Double maiorMedia = demandaDAO.calcularMaiorMediaHistorica(idEstacao, idLinha);
        // Verifica se é nula ou menor que 0
        if (maiorMedia == null || maiorMedia <= 0) {
            throw new IllegalArgumentException("Não foi possível calcular a referência histórica.");
        }

        // Calcular índice entre 0 e 100
        int indice = (int) Math.round((mediaDemanda / maiorMedia) * 100);
        indice = Math.max(0, Math.min(100, indice));
        // Classificar índice
        String classificacao = classificarLotacao(indice);

        // Montar objeto da previsão
        PrevisaoLotacaoTO previsao = new PrevisaoLotacaoTO();
        // Preenchimento de objeto
        previsao.setDtPrevisao(dataPrevisao);
        previsao.setVlHoraReferencia(hora);
        previsao.setVlIndiceLotacao(indice);
        previsao.setDsClassificacao(classificacao);
        previsao.setDsModeloUtilizado("MEDIA_HISTORICA_NORMALIZADA_V1");
        previsao.setDtGeracao(LocalDateTime.now());
        previsao.setEstacaoIdEstacao(idEstacao);
        previsao.setLinhaIdLinha(idLinha);

        // Salvar previsão no banco
        return previsaoDAO.save(previsao);
    }

    // Classificar índice de lotação
    // classificarLotacao()
    private String classificarLotacao(int indice) {
        // Verificações para classificação
        if (indice <= 25) {
            return "BAIXA";
        }
        if (indice <= 50) {
            return "MODERADA";
        }
        if (indice <= 75) {
            return "ALTA";
        }
        return "MUITO_ALTA";
    }

    // Validar parâmetros recebidos
    // validarParametros()
    private void validarParametros(LocalDate data, Integer hora, Long idEstacao, Long idLinha) {
        // verifica se a data é nula
        if (data == null) {
            throw new IllegalArgumentException("A data da previsão é obrigatória.");
        }
        // Verificação de hora
        if (hora == null || hora < 0 || hora > 23) {
            throw new IllegalArgumentException("O horário deve estar entre 0 e 23.");
        }
        // Verificação de idEstacao
        if (idEstacao == null || idEstacao <= 0) {
            throw new IllegalArgumentException("O ID da estação é inválido.");
        }
        // Verificação de idLinha
        if (idLinha == null || idLinha <= 0) {
            throw new IllegalArgumentException("O ID da linha é inválido.");
        }
    }

    //findAll()
    public List<PrevisaoLotacaoTO> findAll() {
        return previsaoDAO.findAll();
    }

    // findById()
    public PrevisaoLotacaoTO findById(Long idPrevisao) {
        // Verifica se o idPrevisão é válido
        if (idPrevisao == null || idPrevisao <= 0) {
            throw new IllegalArgumentException("ID da previsão inválido.");
        }
        // Faz o select no banco
        PrevisaoLotacaoTO previsao = previsaoDAO.findById(idPrevisao);
        // Se o resultado for nulo
        if (previsao == null) {
            throw new IllegalArgumentException("Previsão não encontrada.");
        }
        // retorno de objeto
        return previsao;
    }

    // findByEstacao()
    public List<PrevisaoLotacaoTO> findByEstacao(Long idEstacao) {
        // Verifica se o idEstacao é válido
        if (idEstacao == null || idEstacao <= 0) {
            throw new IllegalArgumentException("ID da estação inválido.");
        }
        // Faz o select no banco
        return previsaoDAO.findByEstacao(idEstacao);
    }

    // findByData()
    public List<PrevisaoLotacaoTO> findByData(LocalDate data) {
        // Verifica se a data é nula
        if (data == null) {
            throw new IllegalArgumentException("Data da previsão não informada.");
        }
        // Faz o select no banco
        return previsaoDAO.findByData(Date.valueOf(data));
    }

}
