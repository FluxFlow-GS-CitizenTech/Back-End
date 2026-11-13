package br.com.fiap.bo;

import br.com.fiap.dao.EstacaoDAO;
import br.com.fiap.dao.LinhaDAO;
import br.com.fiap.dao.PrevisaoLotacaoDAO;
import br.com.fiap.dao.RotaDAO;

import br.com.fiap.to.*;

import java.time.LocalDate;
import java.util.*;

public class RotaBO {

    // Objetos DAO
    private final RotaDAO rotaDAO = new RotaDAO();
    private final EstacaoDAO estacaoDAO = new EstacaoDAO();
    private final LinhaDAO linhaDAO = new LinhaDAO();
    private final PrevisaoLotacaoDAO previsaoLotacaoDAO = new PrevisaoLotacaoDAO();
    // Instância responsável por gerar previsões de lotação.
    private final PrevisaoLotacaoBO previsaoLotacaoBO = new PrevisaoLotacaoBO();
    // Parâmetros utilizados para estimar o tempo da viagem (em minutos).
    private static final int TEMPO_POR_TRECHO = 2;
    private static final int TEMPO_ESPERA_POR_EMBARQUE = 1;
    private static final int TEMPO_POR_BALDEACAO = 4;


    /*
     * CONSTRUIR GRAFO
     *
     * Representa a rede metroferroviária como um grafo.
     *
     * Cada estação é um vértice e cada conexão entre duas estações
     * consecutivas é uma aresta.
     *
     * As conexões são bidirecionais, pois o passageiro pode viajar
     * nos dois sentidos da linha.
     *
     * O ID da linha é preservado em cada conexão para identificar
     * qual linha está sendo utilizada no deslocamento.
     */
    private Map<Long, List<ConexaoTO>> construirGrafo() {

        // Cada chave representa uma estação e sua lista contém
        // as conexões disponíveis a partir dela.
        Map<Long, List<ConexaoTO>> grafo = new HashMap<>();

        // Busca no banco todas as conexões entre estações consecutivas.
        List<ConexaoTO> conexoes = rotaDAO.findConexoes();

        for (ConexaoTO conexao : conexoes) {
            Long origem = conexao.getIdEstacaoOrigem();
            Long destino = conexao.getIdEstacaoDestino();
            // Adiciona a conexão no sentido original.
            grafo.computeIfAbsent(origem, k -> new ArrayList<>()).add(conexao);
            // Cria uma conexão inversa para permitir o deslocamento
            // no sentido contrário, mantendo a mesma linha.
            ConexaoTO inversa = new ConexaoTO(destino, origem, conexao.getIdLinha());
            grafo.computeIfAbsent(destino, k -> new ArrayList<>()).add(inversa);
        }
        // Retorno de map
        return grafo;
    }


    /*
     * BUSCAR MENOR ROTA
     *
     * Utiliza o algoritmo BFS (Breadth-First Search), ou busca em
     * largura, para encontrar um caminho com a menor quantidade
     * de conexões entre a estação de origem e a estação de destino.
     *
     * Neste momento, o critério de otimização é a quantidade de
     * trechos percorridos, e não a quantidade de baldeações.
     */
    public RotaTO buscarMenorRota(Long idOrigem, Long idDestino) {
        // Valida os IDs recebidos.
        if (idOrigem == null || idDestino == null) {
            throw new IllegalArgumentException("As estações de origem e destino são obrigatórias.");
        }
        // Confirma que as estações existem.
        EstacaoTO estacaoOrigem = estacaoDAO.findById(idOrigem);
        EstacaoTO estacaoDestino = estacaoDAO.findById(idDestino);
        if (estacaoOrigem == null || estacaoDestino == null) {
            throw new IllegalArgumentException("Uma ou ambas as estações não foram encontradas.");
        }
        // Caso a origem seja igual ao destino, retorna uma rota vazia.
        if (idOrigem.equals(idDestino)) {
            RotaTO rota = new RotaTO();
            rota.setTrechos(new ArrayList<>());
            rota.setTotalTrechos(0);
            rota.setTotalEstacoes(1);
            rota.setTotalBaldeacoes(0);
            return rota;
        }
        // Monta o grafo da rede metroferroviária.
        Map<Long, List<ConexaoTO>> grafo = construirGrafo();
        // Ordena os estados priorizando:
        // 1º Menos baldeações.
        // 2º Menos trechos.
        PriorityQueue<Map.Entry<EstadoRota, CustoRota>> fila = new PriorityQueue<>((a, b) -> {
                    int comparacao = Integer.compare(
                            a.getValue().baldeacoes,
                            b.getValue().baldeacoes
                    );
                    if (comparacao != 0) {
                        return comparacao;
                    }
                    return Integer.compare(a.getValue().trechos, b.getValue().trechos);
                });
        // Guarda o melhor custo encontrado para cada estado.
        Map<EstadoRota, CustoRota> melhoresCustos = new HashMap<>();
        // Armazena o caminho anterior de cada estado.
        Map<EstadoRota, PassoAnterior> predecessores = new HashMap<>();
        // Estado inicial: estação de origem, ainda sem linha utilizada.
        EstadoRota inicial = new EstadoRota(idOrigem, null);

        CustoRota custoInicial = new CustoRota(0, 0);

        melhoresCustos.put(inicial, custoInicial);

        fila.add(new AbstractMap.SimpleEntry<>(inicial, custoInicial));
        // Armazenará o estado de destino quando ele for encontrado.
        EstadoRota estadoFinal = null;

        while (!fila.isEmpty()) {
            // Retira o estado de menor custo.
            Map.Entry<EstadoRota, CustoRota> item = fila.poll();
            EstadoRota atual = item.getKey();
            CustoRota custoAtual = item.getValue();
            // Ignora entradas antigas da fila que já foram superadas.
            CustoRota melhorConhecido = melhoresCustos.get(atual);
            if (compararCustos(custoAtual, melhorConhecido) > 0) {
                continue;
            }
            // Ao retirar o destino da fila, encontramos seu menor custo.
            if (atual.getIdEstacao().equals(idDestino)) {
                estadoFinal = atual;
                break;
            }
            // Recupera as conexões disponíveis na estação atual.
            List<ConexaoTO> vizinhos = grafo.getOrDefault(atual.getIdEstacao(), Collections.emptyList());
            for (ConexaoTO conexao : vizinhos) {
                Long proximaEstacao = conexao.getIdEstacaoDestino();
                Long proximaLinha = conexao.getIdLinha();
                // Verifica se houve mudança de linha.
                int novaBaldeacao = 0;
                if (atual.getIdLinha() != null && !atual.getIdLinha().equals(proximaLinha)) {
                    novaBaldeacao = 1;
                }
                // Calcula o custo acumulado do novo caminho.
                CustoRota novoCusto = new CustoRota(custoAtual.baldeacoes + novaBaldeacao, custoAtual.trechos + 1);
                // O próximo estado considera estação E linha.
                EstadoRota proximo = new EstadoRota(proximaEstacao, proximaLinha);

                CustoRota custoAnterior = melhoresCustos.get(proximo);
                // Atualiza somente se encontramos um caminho melhor.
                if (custoAnterior == null || compararCustos(novoCusto, custoAnterior) < 0) {
                    melhoresCustos.put(proximo, novoCusto);
                    predecessores.put(proximo, new PassoAnterior(atual, conexao));
                    fila.add(new AbstractMap.SimpleEntry<>(proximo, novoCusto));
                }
            }
        }

        // Se nenhum estado de destino foi encontrado, não existe rota.
        if (estadoFinal == null) {
            throw new IllegalArgumentException("Não existe uma rota entre as estações informadas.");
        }
        // Reconstrói o caminho e transforma em uma resposta RotaTO.
        List<ConexaoTO> caminho = reconstruirRota(estadoFinal, predecessores);

        return montarRota(caminho);
    }


    /*
     * BUSCAR ROTA COM MENOS ESTAÇÕES
     *
     * Utiliza uma busca com fila para encontrar
     * o caminho com a menor quantidade de trechos entre a
     * estação de origem e a estação de destino.
     *
     * O primeiro critério é a quantidade de trechos.
     * Caso dois caminhos tenham a mesma quantidade de trechos,
     * o que tem menos baldeações vai ser escolhido.
     */
    public RotaTO buscarRotaMenosEstacoes(Long idOrigem, Long idDestino) {
        // Valida os IDs recebidos.
        if (idOrigem == null || idDestino == null) {
            throw new IllegalArgumentException("As estações de origem e destino são obrigatórias.");
        }
        // Confirma que as estações existem.
        EstacaoTO estacaoOrigem = estacaoDAO.findById(idOrigem);
        EstacaoTO estacaoDestino = estacaoDAO.findById(idDestino);
        if (estacaoOrigem == null || estacaoDestino == null) {
            throw new IllegalArgumentException("Uma ou ambas as estações não foram encontradas.");
        }
        // Caso a origem seja igual ao destino, retorna uma rota vazia.
        if (idOrigem.equals(idDestino)) {
            RotaTO rota = new RotaTO();
            rota.setTrechos(new ArrayList<>());
            rota.setTotalTrechos(0);
            rota.setTotalEstacoes(1);
            rota.setTotalBaldeacoes(0);
            return rota;
        }
        // Monta o grafo da rede metroferroviária.
        Map<Long, List<ConexaoTO>> grafo = construirGrafo();
        // Ordena os estados priorizando:
        // 1º Menos trechos.
        // 2º Menos baldeações.
        PriorityQueue<Map.Entry<EstadoRota, CustoRota>> fila = new PriorityQueue<>((a, b) -> {
                    int comparacao = Integer.compare(a.getValue().trechos, b.getValue().trechos);
                    if (comparacao != 0) {
                        return comparacao;
                    }
                    return Integer.compare(a.getValue().baldeacoes, b.getValue().baldeacoes);
                });
        // Guarda o melhor custo encontrado para cada estado.
        Map<EstadoRota, CustoRota> melhoresCustos = new HashMap<>();
        // Armazena o caminho anterior de cada estado.
        Map<EstadoRota, PassoAnterior> predecessores = new HashMap<>();
        // Estado inicial: estação de origem, ainda sem linha utilizada.
        EstadoRota inicial = new EstadoRota(idOrigem, null);
        // Custo inicial: nenhuma baldeação e nenhum trecho percorrido.
        CustoRota custoInicial = new CustoRota(0, 0);

        melhoresCustos.put(inicial, custoInicial);

        fila.add(new AbstractMap.SimpleEntry<>(inicial, custoInicial));
        // Armazenará o estado de destino quando ele for encontrado.
        EstadoRota estadoFinal = null;
        while (!fila.isEmpty()) {
            // Retira o estado de menor custo da fila.
            Map.Entry<EstadoRota, CustoRota> item = fila.poll();

            EstadoRota atual = item.getKey();
            CustoRota custoAtual = item.getValue();
            // Ignora entradas antigas que já foram superadas.
            CustoRota melhorConhecido = melhoresCustos.get(atual);
            if (compararCustosMenosEstacoes(custoAtual, melhorConhecido) > 0) {
                continue;
            }
            // Ao retirar o destino da fila, o menor custo.
            if (atual.getIdEstacao().equals(idDestino)) {
                estadoFinal = atual;
                break;
            }

            // Recupera as conexões disponíveis na estação atual.
            List<ConexaoTO> vizinhos = grafo.getOrDefault(atual.getIdEstacao(), Collections.emptyList());
            for (ConexaoTO conexao : vizinhos) {
                Long proximaEstacao = conexao.getIdEstacaoDestino();
                Long proximaLinha = conexao.getIdLinha();
                // Verifica se houve mudança de linha.
                int novaBaldeacao = 0;
                if (atual.getIdLinha() != null && !atual.getIdLinha().equals(proximaLinha)) {
                    novaBaldeacao = 1;
                }
                // Calcula o custo acumulado do novo caminho.
                CustoRota novoCusto = new CustoRota(custoAtual.baldeacoes + novaBaldeacao, custoAtual.trechos + 1);
                // O próximo estado considera estação e linha.
                EstadoRota proximo = new EstadoRota(proximaEstacao, proximaLinha);

                CustoRota custoAnterior = melhoresCustos.get(proximo);
                // Atualiza somente se encontramos um caminho melhor.
                if (custoAnterior == null || compararCustosMenosEstacoes(novoCusto, custoAnterior) < 0) {
                    melhoresCustos.put(proximo, novoCusto);

                    predecessores.put(proximo, new PassoAnterior(atual, conexao));

                    fila.add(new AbstractMap.SimpleEntry<>(proximo, novoCusto));
                }
            }
        }

        // Se nenhum estado de destino foi encontrado, não existe rota.
        if (estadoFinal == null) {
            throw new IllegalArgumentException("Não existe uma rota entre as estações informadas.");
        }
        // Reconstrói o caminho encontrado.
        List<ConexaoTO> caminho = reconstruirRota(estadoFinal, predecessores);
        // Converte o caminho para o objeto de resposta da API.
        return montarRota(caminho);
    }


    public RotaTO buscarRotaMaisRapida(Long idOrigem, Long idDestino) {
        // Valida os IDs recebidos.
        if (idOrigem == null || idDestino == null) {
            throw new IllegalArgumentException("As estações de origem e destino são obrigatórias.");
        }
        // Confirma que as estações existem.
        EstacaoTO estacaoOrigem = estacaoDAO.findById(idOrigem);
        EstacaoTO estacaoDestino = estacaoDAO.findById(idDestino);
        if (estacaoOrigem == null || estacaoDestino == null) {
            throw new IllegalArgumentException("Uma ou ambas as estações não foram encontradas.");
        }
        // Caso a origem seja igual ao destino, retorna uma rota vazia.
        if (idOrigem.equals(idDestino)) {
            RotaTO rota = new RotaTO();
            rota.setTrechos(new ArrayList<>());
            rota.setTotalTrechos(0);
            rota.setTotalEstacoes(1);
            rota.setTotalBaldeacoes(0);
            // Não existe deslocamento, espera ou baldeação.
            rota.setTempoDeslocamento(0);
            rota.setTempoEspera(0);
            rota.setTempoBaldeacoes(0);
            rota.setTempoTotal(0);

            return rota;
        }
        // Monta o grafo da rede metroferroviária.
        Map<Long, List<ConexaoTO>> grafo = construirGrafo();
        // Ordena os estados priorizando o menor tempo estimado.
        PriorityQueue<Map.Entry<EstadoRota, CustoRota>> fila = new PriorityQueue<>((a, b) -> compararCustosPorTempo(a.getValue(), b.getValue()));
        // Guarda o melhor custo encontrado para cada estado.
        Map<EstadoRota, CustoRota> melhoresCustos = new HashMap<>();
        // Armazena o caminho anterior de cada estado.
        Map<EstadoRota, PassoAnterior> predecessores = new HashMap<>();
        // Estado inicial: estação de origem, ainda sem linha utilizada.
        EstadoRota inicial = new EstadoRota(idOrigem, null);

        CustoRota custoInicial = new CustoRota(0, 0);

        melhoresCustos.put(inicial, custoInicial);
        fila.add(new AbstractMap.SimpleEntry<>(inicial, custoInicial));
        // Armazenará o estado de destino quando ele for encontrado.
        EstadoRota estadoFinal = null;

        while (!fila.isEmpty()) {
            // Retira o estado com menor tempo estimado.
            Map.Entry<EstadoRota, CustoRota> item = fila.poll();
            EstadoRota atual = item.getKey();
            CustoRota custoAtual = item.getValue();
            // Ignora entradas antigas da fila que já foram superadas.
            CustoRota melhorConhecido = melhoresCustos.get(atual);
            if (compararCustosPorTempo(custoAtual, melhorConhecido) > 0) {
                continue;
            }
            // Ao retirar o destino da fila, encontramos seu menor custo.
            if (atual.getIdEstacao().equals(idDestino)) {
                estadoFinal = atual;
                break;
            }
            // Recupera as conexões disponíveis na estação atual.
            List<ConexaoTO> vizinhos = grafo.getOrDefault(atual.getIdEstacao(), Collections.emptyList());
            for (ConexaoTO conexao : vizinhos) {

                Long proximaEstacao = conexao.getIdEstacaoDestino();
                Long proximaLinha = conexao.getIdLinha();
                // Verifica se houve mudança de linha.
                int novaBaldeacao = 0;

                if (atual.getIdLinha() != null && !atual.getIdLinha().equals(proximaLinha)) {
                    novaBaldeacao = 1;
                }
                // Calcula o novo custo acumulado.
                CustoRota novoCusto = new CustoRota(custoAtual.baldeacoes + novaBaldeacao, custoAtual.trechos + 1);
                // O próximo estado considera estação E linha.
                EstadoRota proximo = new EstadoRota(proximaEstacao, proximaLinha);

                CustoRota custoAnterior = melhoresCustos.get(proximo);
                // Atualiza somente se encontramos um caminho mais rápido.
                if (custoAnterior == null || compararCustosPorTempo(novoCusto, custoAnterior) < 0) {
                    melhoresCustos.put(proximo, novoCusto);
                    predecessores.put(proximo, new PassoAnterior(atual, conexao));
                    fila.add(new AbstractMap.SimpleEntry<>(proximo, novoCusto));
                }
            }
        }
        // Se nenhum estado de destino foi encontrado, não existe rota.
        if (estadoFinal == null) {
            throw new IllegalArgumentException("Não existe uma rota entre as estações informadas.");
        }
        // Reconstrói o caminho encontrado.
        List<ConexaoTO> caminho = reconstruirRota(estadoFinal, predecessores);
        // Monta a resposta com os dados e tempos estimados.
        return montarRota(caminho);
    }


    // Busca a rota que apresenta a menor média de lotação.
    public RotaTO buscarRotaMenorLotacao(Long idOrigem, Long idDestino, Date data, Integer hora) {
        // Valida os parâmetros recebidos.
        if (idOrigem == null || idDestino == null || data == null || hora == null) {
            throw new IllegalArgumentException("Origem, destino, data e horário são obrigatórios.");
        }
        if (hora < 0 || hora > 23) {
            throw new IllegalArgumentException("O horário deve estar entre 0 e 23.");
        }
        // Confirma que as estações existem.
        if (estacaoDAO.findById(idOrigem) == null || estacaoDAO.findById(idDestino) == null) {
            throw new IllegalArgumentException("Uma ou ambas as estações não foram encontradas.");
        }
        // Caso a origem seja igual ao destino, não há trechos para avaliar.
        if (idOrigem.equals(idDestino)) {
            RotaTO rota = new RotaTO();
            rota.setTrechos(new ArrayList<>());
            rota.setTotalTrechos(0);
            rota.setTotalEstacoes(1);
            rota.setTotalBaldeacoes(0);
            rota.setIndiceMedioLotacao(0.0);
            rota.setIndiceMaximoLotacao(0);
            rota.setClassificacaoLotacao("Baixa");
            return rota;
        }
        // Monta o grafo da rede metroferroviária.
        Map<Long, List<ConexaoTO>> grafo = construirGrafo();
        // Prepara todas as previsões antes de iniciar a busca.
        Map<String, Integer> indicesLotacao = carregarIndicesLotacao(grafo, data, hora);
        // Estruturas utilizadas para armazenar a melhor rota encontrada.
        List<ConexaoTO> melhorCaminho = new ArrayList<>();
        // Guarda os melhores valores encontrados durante a busca.
        double[] menorMedia = {Double.MAX_VALUE};
        int[] menorBaldeacoes = {Integer.MAX_VALUE};
        int[] menorTrechos = {Integer.MAX_VALUE};
        // Controla as estações já visitadas.
        Set<Long> visitadas = new HashSet<>();
        visitadas.add(idOrigem);
        // Inicia a busca em profundidade.
        // A busca começa sem linha anterior, sem baldeações e sem trechos.
        buscarCaminhosPorLotacao(idOrigem, idDestino, grafo, indicesLotacao, visitadas, new ArrayList<>(), 0, null, 0, melhorCaminho, menorMedia, menorBaldeacoes, menorTrechos);
        // Verifica se alguma rota foi encontrada.
        if (melhorCaminho.isEmpty()) {
            throw new IllegalArgumentException("Não foi encontrada uma rota entre as estações informadas.");
        }
        // Monta o objeto de resposta.
        RotaTO rota = montarRota(melhorCaminho);
        // Calcula os indicadores de lotação da rota selecionada.
        int somaLotacao = 0;
        int maiorLotacao = 0;
        for (ConexaoTO conexao : melhorCaminho) {
            // Recupera o índice diretamente da memória.
            String chave = conexao.getIdEstacaoDestino() + "-" + conexao.getIdLinha();

            Integer indice = indicesLotacao.get(chave);

            somaLotacao += indice;

            if (indice > maiorLotacao) {
                maiorLotacao = indice;
            }
        }
        // Calcula a média de lotação dos trechos.
        double mediaLotacao = (double) somaLotacao / melhorCaminho.size();
        // Classifica a lotação média.
        String classificacao;
        if (mediaLotacao <= 25) {
            classificacao = "Baixa";
        } else if (mediaLotacao <= 50) {
            classificacao = "Moderada";
        } else if (mediaLotacao <= 75) {
            classificacao = "Alta";
        } else {
            classificacao = "Muito alta";
        }
        // Preenche os indicadores no objeto de resposta.
        rota.setIndiceMedioLotacao(mediaLotacao);
        rota.setIndiceMaximoLotacao(maiorLotacao);
        rota.setClassificacaoLotacao(classificacao);
        return rota;
    }


    /*
     * BUSCAR CAMINHOS POR LOTAÇÃO
     *
     * Percorre os caminhos possíveis utilizando DFS (busca em profundidade).
     *
     * A seleção da melhor rota considera:
     * 1º Menor média de lotação.
     * 2º Menor quantidade de baldeações.
     * 3º Menor quantidade de trechos.
     */
    private void buscarCaminhosPorLotacao(Long estacaoAtual, Long idDestino, Map<Long, List<ConexaoTO>> grafo, Map<String, Integer> indicesLotacao, Set<Long> visitadas, List<ConexaoTO> caminhoAtual, int somaLotacao, Long linhaAnterior, int baldeacoes, List<ConexaoTO> melhorCaminho, double[] menorMedia, int[] menorBaldeacoes, int[] menorTrechos) {
        // Quando chegamos ao destino, avaliamos a rota completa.
        if (estacaoAtual.equals(idDestino)) {
            // Calcula a média de lotação dos trechos percorridos.
            double media = (double) somaLotacao / caminhoAtual.size();
            // Recupera a quantidade de trechos da rota atual.
            int quantidadeTrechos = caminhoAtual.size();
            // Compara a rota atual com a melhor rota encontrada.
            boolean rotaMelhor = false;
            // Primeiro critério: menor média de lotação.
            if (media < menorMedia[0] - 0.000001) {
                rotaMelhor = true;
            // Segundo critério: menor quantidade de baldeações.
            } else if (Math.abs(media - menorMedia[0]) < 0.000001 && baldeacoes < menorBaldeacoes[0]) {
                rotaMelhor = true;
            // Terceiro critério: menor quantidade de trechos.
            } else if (Math.abs(media - menorMedia[0]) < 0.000001 && baldeacoes == menorBaldeacoes[0] && quantidadeTrechos < menorTrechos[0]) {
                rotaMelhor = true;
            }
            // Atualiza os dados caso a rota atual seja melhor.
            if (rotaMelhor) {
                menorMedia[0] = media;
                menorBaldeacoes[0] = baldeacoes;
                menorTrechos[0] = quantidadeTrechos;

                melhorCaminho.clear();
                melhorCaminho.addAll(caminhoAtual);
            }
            return;
        }
        // Recupera as conexões disponíveis na estação atual.
        List<ConexaoTO> vizinhos = grafo.getOrDefault(estacaoAtual, Collections.emptyList());
        // Analisa cada conexão disponível.
        for (ConexaoTO conexao : vizinhos) {
            Long proximaEstacao = conexao.getIdEstacaoDestino();
            Long proximaLinha = conexao.getIdLinha();
            // Não permite visitar novamente uma estação no mesmo caminho.
            if (visitadas.contains(proximaEstacao)) {
                continue;
            }
            // Recupera o índice de lotação da próxima estação e linha.
            String chave = proximaEstacao + "-" + proximaLinha;

            Integer indiceLotacao = indicesLotacao.get(chave);
            // Ignora conexões sem índice de lotação disponível.
            if (indiceLotacao == null) {
                continue;
            }
            // Verifica se houve mudança de linha.
            int novaBaldeacao = 0;
            if (linhaAnterior != null && !linhaAnterior.equals(proximaLinha)) {
                novaBaldeacao = 1;
            }
            // Adiciona a próxima estação e conexão ao caminho atual.
            visitadas.add(proximaEstacao);
            caminhoAtual.add(conexao);
            // Continua a busca acumulando lotação e baldeações.
            buscarCaminhosPorLotacao(proximaEstacao, idDestino, grafo, indicesLotacao, visitadas, caminhoAtual, somaLotacao + indiceLotacao, proximaLinha, baldeacoes + novaBaldeacao, melhorCaminho, menorMedia, menorBaldeacoes, menorTrechos);
            // Remove os elementos adicionados para explorar outro caminho.
            caminhoAtual.remove(caminhoAtual.size() - 1);
            visitadas.remove(proximaEstacao);
        }
    }



    // Carrega as previsões de lotação necessárias antes de iniciar o DFS.
    private Map<String, Integer> carregarIndicesLotacao(Map<Long, List<ConexaoTO>> grafo, Date data, Integer hora) {
        // Armazena os índices utilizando estação de destino e linha como chave.
        Map<String, Integer> indicesLotacao = new HashMap<>();
        // Evita consultar mais de uma vez a mesma combinação.
        Set<String> combinacoesVerificadas = new HashSet<>();
        // Percorre todas as conexões existentes no grafo.
        for (List<ConexaoTO> conexoes : grafo.values()) {
            for (ConexaoTO conexao : conexoes) {
                Long idEstacao = conexao.getIdEstacaoDestino();
                Long idLinha = conexao.getIdLinha();
                // Cria uma chave única para a combinação estação e linha.
                String chave = idEstacao + "-" + idLinha;
                // Ignora combinações que já foram verificadas.
                if (!combinacoesVerificadas.add(chave)) {
                    continue;
                }
                // Consulta a previsão existente no Oracle.
                Integer indice = previsaoLotacaoDAO.findIndiceLotacao(idEstacao, idLinha, (java.sql.Date) data, hora
                );
                // Se não existir previsão, gera uma utilizando os dados históricos.
                if (indice == null) {
                    PrevisaoLotacaoTO previsao = previsaoLotacaoBO.gerarPrevisao(((java.sql.Date) data).toLocalDate(), hora, idEstacao, idLinha);
                    indice = previsao.getVlIndiceLotacao();
                }
                // Armazena o índice em memória.
                indicesLotacao.put(chave, indice);
            }
        }
        // Retorna todas as previsões carregadas.
        return indicesLotacao;
    }


    /*
     * COMPARAR CUSTOS - MENOS ESTAÇÕES
     *
     * Compara dois caminhos considerando:
     * 1º Menor quantidade de trechos.
     * 2º Menor quantidade de baldeações.
     *
     * Retorna:
     * - Valor negativo: primeiro caminho é melhor.
     * - Zero: os caminhos possuem o mesmo custo.
     * - Valor positivo: segundo caminho é melhor.
     */
    private int compararCustosMenosEstacoes(CustoRota a, CustoRota b) {
        // Primeiro compara a quantidade de trechos.
        int comparacao = Integer.compare(a.trechos, b.trechos);
        if (comparacao != 0) {
            return comparacao;
        }
        // Se empatar, compara a quantidade de baldeações.
        return Integer.compare(a.baldeacoes, b.baldeacoes);
    }


    /*
     * Compara dois caminhos considerando o tempo total estimado.
     *
     * Retorna:
     * - Negativo: caminho A é mais rápido.
     * - Positivo: caminho B é mais rápido.
     * - Zero: os caminhos possuem o mesmo tempo estimado.
     */
    private int compararCustosPorTempo(CustoRota a, CustoRota b) {
        // Calcula o tempo total estimado do caminho A.
        int tempoA = (a.trechos * TEMPO_POR_TRECHO) + ((a.baldeacoes + 1) * TEMPO_ESPERA_POR_EMBARQUE) + (a.baldeacoes * TEMPO_POR_BALDEACAO);
        // Calcula o tempo total estimado do caminho B.
        int tempoB = (b.trechos * TEMPO_POR_TRECHO) + ((b.baldeacoes + 1) * TEMPO_ESPERA_POR_EMBARQUE) + (b.baldeacoes * TEMPO_POR_BALDEACAO);
        // Retorno
        return Integer.compare(tempoA, tempoB);
    }


    /*
     * RECONSTRUIR ROTA
     *
     * O BFS registra os predecessores durante a busca.
     * Este metodo utiliza esses registros para recuperar o caminho
     * entre a origem e o destino.
     *
     * A reconstrução começa no destino e percorre o caminho inverso.
     * Ao final, a lista é invertida para apresentar a ordem correta.
     */
    private List<ConexaoTO> reconstruirRota(EstadoRota estadoFinal, Map<EstadoRota, PassoAnterior> predecessores) {
        List<ConexaoTO> rota = new ArrayList<>();
        EstadoRota atual = estadoFinal;
        // Percorre o caminho do destino até a origem.
        while (predecessores.containsKey(atual)) {
            PassoAnterior passo = predecessores.get(atual);
            // Adiciona a conexão utilizada para chegar ao estado atual.
            rota.add(passo.conexao);
            // Retrocede para o estado anterior.
            atual = passo.estadoAnterior;
        }
        // Inverte para apresentar a rota da origem ao destino.
        Collections.reverse(rota);

        return rota;
    }


    /*
     * MONTAR ROTA
     *
     * Converte a lista de conexões encontrada pelo BFS em um RotaTO.
     *
     * Para cada trecho, consulta os DAOs para recuperar:
     * - Nome e ID da estação de origem;
     * - Nome e ID da estação de destino;
     * - Código, nome, ID e cor da linha utilizada.
     *
     * Também calcula os totais de trechos, estações e baldeações.
     */
    private RotaTO montarRota(List<ConexaoTO> conexoes) {
        RotaTO rota = new RotaTO();
        List<TrechoTO> trechos = new ArrayList<>();
        for (ConexaoTO conexao : conexoes) {
            // Busca os dados completos das estações
            EstacaoTO origem = estacaoDAO.findById(conexao.getIdEstacaoOrigem());
            EstacaoTO destino = estacaoDAO.findById(conexao.getIdEstacaoDestino());
            // Busca os dados da linha
            LinhaTO linha = linhaDAO.findById(conexao.getIdLinha());
            // Monta a estação de origem
            EstacaoRotaTO estacaoOrigem = new EstacaoRotaTO();
            estacaoOrigem.setIdEstacao(origem.getIdEstacao());
            estacaoOrigem.setNmEstacao(origem.getNmEstacao());
            estacaoOrigem.setDsLatitude(origem.getDsLatitude());
            estacaoOrigem.setDsLongitude(origem.getDsLongitude());
            // Monta a estação de destino
            EstacaoRotaTO estacaoDestino = new EstacaoRotaTO();
            estacaoDestino.setIdEstacao(destino.getIdEstacao());
            estacaoDestino.setNmEstacao(destino.getNmEstacao());
            estacaoDestino.setDsLatitude(destino.getDsLatitude());
            estacaoDestino.setDsLongitude(destino.getDsLongitude());
            // Monta os dados da linha
            LinhaRotaTO linhaRota = new LinhaRotaTO();
            linhaRota.setIdLinha(linha.getIdLinha());
            linhaRota.setDsCodigo(linha.getDsCodigo());
            linhaRota.setNmLinha(linha.getNmLinha());
            linhaRota.setDsCor(linha.getDsCor());
            // Monta o trecho
            TrechoTO trecho = new TrechoTO();
            trecho.setOrigem(estacaoOrigem);
            trecho.setDestino(estacaoDestino);
            trecho.setLinha(linhaRota);
            trechos.add(trecho);
        }
        // Preenche os dados gerais da rota
        rota.setTrechos(trechos);
        rota.setTotalTrechos(trechos.size());
        rota.setTotalEstacoes(trechos.size() + 1);
        // Conta as baldeações
        int baldeacoes = 0;
        for (int i = 1; i < trechos.size(); i++) {
            Long linhaAnterior = trechos.get(i - 1).getLinha().getIdLinha();
            Long linhaAtual = trechos.get(i).getLinha().getIdLinha();
            if (!linhaAnterior.equals(linhaAtual)) {
                baldeacoes++;
            }
        }

        rota.setTotalBaldeacoes(baldeacoes);

        // Calcula os tempos estimados da viagem (em minutos).
        // Tempo total dentro dos trens: 2 minutos por trecho.
        int tempoDeslocamento = trechos.size() * TEMPO_POR_TRECHO;
        // Considera o embarque inicial e um novo embarque após cada baldeação.
        int quantidadeEmbarques = baldeacoes + 1;
        int tempoEspera = quantidadeEmbarques * TEMPO_ESPERA_POR_EMBARQUE;
        // Tempo gasto nas transferências entre linhas.
        int tempoBaldeacoes = baldeacoes * TEMPO_POR_BALDEACAO;
        // Soma os componentes para obter o tempo total estimado.
        int tempoTotal = tempoDeslocamento + tempoEspera + tempoBaldeacoes;
        // Adiciona os tempos calculados ao objeto de resposta.
        rota.setTempoDeslocamento(tempoDeslocamento);
        rota.setTempoEspera(tempoEspera);
        rota.setTempoBaldeacoes(tempoBaldeacoes);
        rota.setTempoTotal(tempoTotal);
        // Retorno de objeto
        return rota;
    }


    private int compararCustos(CustoRota a, CustoRota b) {
        // Primeiro compara a quantidade de baldeações.
        int comparacao = Integer.compare(a.baldeacoes, b.baldeacoes);
        if (comparacao != 0) {
            return comparacao;
        }
        // Se empatar, compara a quantidade de trechos.
        return Integer.compare(a.trechos, b.trechos);
    }

    // Representa uma estação alcançada utilizando uma determinada linha.
    // A mesma estação pode aparecer em estados diferentes quando
    // o passageiro chega por linhas distintas.
    private static class EstadoRota {
        // Atributos
        private final Long idEstacao;
        private final Long idLinha;
        // Construtor com passagem de parâmetro
        public EstadoRota(Long idEstacao, Long idLinha) {
            this.idEstacao = idEstacao;
            this.idLinha = idLinha;
        }
        // Getter e setter
        public Long getIdEstacao() {
            return idEstacao;
        }

        public Long getIdLinha() {
            return idLinha;
        }

        // Dois estados são iguais somente quando possuem
        // a mesma estação e a mesma linha.
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof EstadoRota)) return false;

            EstadoRota outro = (EstadoRota) obj;

            return Objects.equals(idEstacao, outro.idEstacao) && Objects.equals(idLinha, outro.idLinha);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idEstacao, idLinha);
        }
    }


    // Armazena o custo acumulado de um caminho.
    // Primeiro comparamos baldeações e depois trechos.
    private static class CustoRota {
        // Atributos
        private final int baldeacoes;
        private final int trechos;
        // Construtor com passagem de parâmetro
        public CustoRota(int baldeacoes, int trechos) {
            this.baldeacoes = baldeacoes;
            this.trechos = trechos;
        }
    }


    // Registra de qual estado viemos e qual conexão foi utilizada.
    private static class PassoAnterior {
        // Atributos
        private final EstadoRota estadoAnterior;
        private final ConexaoTO conexao;
        // Construtor com passagem de parâmetro
        public PassoAnterior(EstadoRota estadoAnterior, ConexaoTO conexao) {
            this.estadoAnterior = estadoAnterior;
            this.conexao = conexao;
        }
    }
}
