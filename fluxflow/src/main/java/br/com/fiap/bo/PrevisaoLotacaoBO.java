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
        // Faz a validação dos parâmetros antes de utilizá-los.
        validarParametros(dataPrevisao, hora, idEstacao, idLinha);
        // Identifica o dia da semana (segunda = 1, domingo = 7).
        int diaSemana = dataPrevisao.getDayOfWeek().getValue();
        // Consulta a média histórica da estação, linha, dia e horário.
        Double mediaDemanda = demandaDAO.calcularMediaHistorica(idEstacao, idLinha, diaSemana, hora);
        // Verifica se existem dados históricos para a combinação.
        if (mediaDemanda == null) {
            throw new IllegalArgumentException("Não existem dados históricos para essa combinação.");
        }
        // Consulta a maior demanda média histórica de toda a rede.
        Double maiorDemandaRede = demandaDAO.calcularMaiorDemandaRede();
        // Verifica se a referência global é válida.
        if (maiorDemandaRede == null || maiorDemandaRede <= 0) {
            throw new IllegalArgumentException("Não foi possível calcular a referência histórica da rede.");
        }
        // Calcula o índice proporcional à maior demanda da rede.
        int indice = (int) Math.round((mediaDemanda / maiorDemandaRede) * 100);
        // Garante que o índice permaneça entre 0 e 100.
        indice = Math.max(0, Math.min(100, indice));
        // Classifica o índice calculado.
        String classificacao = classificarLotacao(indice);
        // Monta o objeto da previsão.
        PrevisaoLotacaoTO previsao = new PrevisaoLotacaoTO();
        // Preenche os atributos da previsão.
        previsao.setDtPrevisao(dataPrevisao);
        previsao.setVlHoraReferencia(hora);
        previsao.setVlIndiceLotacao(indice);
        previsao.setDsClassificacao(classificacao);
        previsao.setDsModeloUtilizado("DEMANDA_RELATIVA_REDE_V2");
        previsao.setDtGeracao(LocalDateTime.now());
        previsao.setEstacaoIdEstacao(idEstacao);
        previsao.setLinhaIdLinha(idLinha);
        // Salva a previsão no banco de dados.
        return previsao;
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
        return "MUITO ALTA";
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
