package br.com.fiap.bo;

import br.com.fiap.dao.EstacaoDAO;
import br.com.fiap.dao.LinhaDAO;
import br.com.fiap.dao.RotaDAO;

import br.com.fiap.to.*;

import java.util.*;

public class RotaBO {

    // Objetos DAO
    private final RotaDAO rotaDAO = new RotaDAO();
    private final EstacaoDAO estacaoDAO = new EstacaoDAO();
    private final LinhaDAO linhaDAO = new LinhaDAO();


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
