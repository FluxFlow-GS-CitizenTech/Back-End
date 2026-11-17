
import json
from datetime import datetime

ARQUIVO = 'dados.json'

LINHAS = {
    '1': ['Jabaquara', 'Conceição', 'São Judas', 'Saúde', 'Praça da Árvore', 'Santa Cruz', 'Vila Mariana', 'Ana Rosa', 'Paraíso', 'Vergueiro', 'São Joaquim', 'Japão-Liberdade', 'Sé', 'São Bento', 'Luz', 'Tiradentes', 'Armênia', 'Portuguesa-Tietê', 'Carandiru', 'Santana', 'Jardim São Paulo-Ayrton Senna', 'Parada Inglesa', 'Tucuruvi'],
    '2': ['Vila Prudente', 'Tamanduateí', 'Sacomã', 'Alto do Ipiranga', 'Santos-Imigrantes', 'Chácara Klabin', 'Ana Rosa', 'Paraíso', 'Brigadeiro', 'Trianon-Masp', 'Consolação', 'Clínicas', 'Santuário Nossa Senhora de Fátima-Sumaré', 'Vila Madalena'],
    '3': ['Corinthians-Itaquera', 'Artur Alvim', 'Patriarca-Vila Ré', 'Guilhermina-Esperança', 'Vila Matilde', 'Penha', 'Carrão', 'Tatuapé', 'Belém', 'Bresser-Mooca', 'Brás', 'Pedro II', 'Sé', 'Anhangabaú', 'República', 'Santa Cecília', 'Marechal Deodoro', 'Palmeiras-Barra Funda'],
    '4': ['Luz', 'República', 'Higienópolis-Mackenzie', 'Paulista', 'Oscar Freire', 'Fradique Coutinho', 'Faria Lima', 'Pinheiros', 'Butantã', 'São Paulo-Morumbi', 'Vila Sônia']
}
NOMES = {'1': 'Azul', '2': 'Verde', '3': 'Vermelha', '4': 'Amarela'}

def carregar():
    try:
        arquivo = open(ARQUIVO, 'r', encoding='utf-8')
        dados = json.load(arquivo)
        arquivo.close()
        return dados
    except FileNotFoundError:
        return {'usuarios': [], 'comentarios': [], 'historico': [], 'ocorrencias': []}
    except (ValueError, OSError):
        print('Erro ao ler dados.json. Confira o arquivo antes de continuar.')
        return None

def salvar(dados):
    arquivo = open(ARQUIVO, 'w', encoding='utf-8')
    json.dump(dados, arquivo, ensure_ascii=False, indent=4)
    arquivo.close()

def data_atual():
    return datetime.now().strftime('%d/%m/%Y %H:%M')

def escolher_estacao():
    print('\nLINHAS')
    for linha in LINHAS:
        print(linha, '-', NOMES[linha])
    linha = input('Linha (0 para voltar): ')
    if linha not in LINHAS:
        return None
    for i in range(len(LINHAS[linha])):
        print(i + 1, '-', LINHAS[linha][i])
    try:
        numero = int(input('Número da estação: '))
        if 1 <= numero <= len(LINHAS[linha]):
            return LINHAS[linha][numero - 1]
    except ValueError:
        pass
    print('Estação inválida.')
    return None

def cadastrar(dados):
    print('\nCADASTRO')
    nome = input('Nome: ')
    email = input('Email ou usuario: ')
    senha = input('Senha: ')

    if nome == '' or email == '' or senha == '':
        print('Preencha os tres campos.')
        return

    for usuario in dados['usuarios']:
        if usuario['email'] == email:
            print('Esse usuario ja existe. Escolha outro.')
            return

    novo_usuario = {
        'nome': nome,
        'email': email,
        'senha': senha,
        'perfil': 'passageiro'
    }
    dados['usuarios'].append(novo_usuario)
    salvar(dados)
    print('Cadastro realizado com sucesso!')

def login(dados):
    email = input('Email ou usuario: ')
    senha = input('Senha: ')
    for usuario in dados['usuarios']:
        if usuario['email'] == email and usuario['senha'] == senha:
            print('Bem-vindo(a),', usuario['nome'])
            return usuario
    print('E-mail ou senha incorretos.')
    return None

def ver_mapa():
    print('\nMAPA DO METRO')
    print('O mapa visual ainda sera adicionado ao projeto.')

def lotacao(estacao):
    valor = 0
    for letra in estacao:
        valor += ord(letra)
    return valor % 101

def nivel_lotacao(valor):
    if valor <= 25:
        return 'Baixa'
    if valor <= 50:
        return 'Moderada'
    if valor <= 75:
        return 'Alta'
    return 'Muito alta'

def montar_rede():
    rede = {}
    for linha in LINHAS:
        estacoes = LINHAS[linha]
        for nome in estacoes:
            rede[(nome, linha)] = []
        for i in range(len(estacoes) - 1):
            a = (estacoes[i], linha)
            b = (estacoes[i + 1], linha)
            rede[a].append((b, 2))
            rede[b].append((a, 2))
    for a in rede:
        for b in rede:
            if a != b and a[0] == b[0] and a[1] != b[1]:
                rede[a].append((b, 4))
    rede[('Consolação', '2')].append((('Paulista', '4'), 5))
    rede[('Paulista', '4')].append((('Consolação', '2'), 5))
    return rede


def buscar_rota(rede, origem, destino, criterio):
    custos = {}
    anteriores = {}
    visitados = []
    for parada in rede:
        custos[parada] = 999999
        anteriores[parada] = None
        if parada[0] == origem:
            custos[parada] = 0
    fim = None
    while True:
        atual = None
        for parada in rede:
            if parada not in visitados:
                if atual is None or custos[parada] < custos[atual]:
                    atual = parada
        if atual is None or custos[atual] == 999999:
            break
        if atual[0] == destino:
            fim = atual
            break
        visitados.append(atual)
        for vizinho, minutos in rede[atual]:
            custo = minutos
            if criterio == 'lotacao':
                custo += lotacao(vizinho[0]) // 15
            novo = custos[atual] + custo
            if novo < custos[vizinho]:
                custos[vizinho] = novo
                anteriores[vizinho] = atual
    if fim is None:
        return None
    caminho = []
    while fim is not None:
        caminho.insert(0, fim)
        fim = anteriores[fim]
    return caminho

def tempo_rota(rede, caminho):
    minutos = 1 
    for i in range(len(caminho) - 1):
        for vizinho, tempo in rede[caminho[i]]:
            if vizinho == caminho[i + 1]:
                minutos += tempo
                break
    return minutos

def mostrar_rota(rede, caminho, dados):
    print('\nROTA ESTIMADA (simulação)')
    baldeacoes = 0
    soma = 0
    for i in range(len(caminho)):
        estacao, linha = caminho[i]
        print(str(i + 1) + '.', estacao, '- Linha', linha, NOMES[linha])
        soma += lotacao(estacao)
        if i > 0 and caminho[i - 1][1] != linha:
            baldeacoes += 1
            print('   >> BALDEAÇÃO')
    print('Tempo estimado:', tempo_rota(rede, caminho), 'minutos')
    print('Baldeações:', baldeacoes)
    media = soma // len(caminho)
    print('Lotação estimada:', nivel_lotacao(media), '(' + str(media) + '/100)')
    nomes = []
    for estacao, linha in caminho:
        if estacao not in nomes:
            nomes.append(estacao)
    print('Ocorrências operacionais simuladas:')
    ocorrencias = 0
    for ocorrencia in dados['ocorrencias']:
        if ocorrencia['estacao'] in nomes and ocorrencia['status'] == 'Em andamento':
            print('-', ocorrencia['estacao'] + ':', ocorrencia['texto'])
            ocorrencias += 1
    if ocorrencias == 0:
        print('Nenhuma ocorrência ativa na rota.')
    print('Comentários sobre a rota:')
    quantidade = 0
    for comentario in dados['comentarios']:
        if comentario['estacao'] in nomes:
            print('-', comentario['estacao'] + ':', comentario['texto'])
            quantidade += 1
    if quantidade == 0:
        print('Nenhum comentário encontrado.')

def acompanhar(rede, caminho, dados):
    print('\nACOMPANHAMENTO MANUAL - não utiliza GPS')
    for i in range(len(caminho)):
        estacao, linha = caminho[i]
        print('\nVocê está em:', estacao, '- Linha', linha, NOMES[linha])
        print('Lotação estimada:', nivel_lotacao(lotacao(estacao)))
        if i + 1 < len(caminho):
            proxima = caminho[i + 1]
            print('Próxima parada:', proxima[0])
            print('Tempo restante estimado:', tempo_rota(rede, caminho[i:]), 'minutos')
        for comentario in dados['comentarios']:
            if comentario['estacao'] == estacao:
                print('Comentário:', comentario['texto'])
        if i + 1 < len(caminho):
            opcao = input('ENTER para avançar / 0 para interromper: ')
            if opcao == '0':
                print('Acompanhamento encerrado.')
                return False
    print('Você chegou ao destino!')
    return True

def planejar(dados, usuario, rede):
    print('\nORIGEM')
    origem = escolher_estacao()
    if origem is None:
        return
    print('\nDESTINO')
    destino = escolher_estacao()
    if destino is None:
        return
    if origem == destino:
        print('Origem e destino não podem ser iguais.')
        return
    rapida = buscar_rota(rede, origem, destino, 'tempo')
    if rapida is None:
        print('Não foi encontrada uma rota.')
        return
    escolhida = rapida
    mostrar_rota(rede, rapida, dados)
    opcao = input('\nVer alternativa com menor lotação? (s/n): ').lower()
    if opcao == 's':
        alternativa = buscar_rota(rede, origem, destino, 'lotacao')
        if alternativa is not None and alternativa != rapida:
            mostrar_rota(rede, alternativa, dados)
            if input('Escolher alternativa? (s/n): ').lower() == 's':
                escolhida = alternativa
        else:
            print('Não há alternativa diferente com esse critério.')
    registro = {'email': usuario['email'], 'origem': origem, 'destino': destino,
                'data': data_atual(), 'tempo': tempo_rota(rede, escolhida)}
    dados['historico'].append(registro)
    salvar(dados)
    if input('Iniciar acompanhamento? (s/n): ').lower() == 's':
        acompanhar(rede, escolhida, dados)
    return escolhida

def consultar_comentarios(dados):
    print('\nCONSULTAR COMENTÁRIOS')
    print('1 - Todos | 2 - Por estação | 3 - Por linha | 4 - Por tipo')
    opcao = input('Escolha: ')
    filtro = None
    if opcao == '2':
        filtro = escolher_estacao()
        campo = 'estacao'
    elif opcao == '3':
        filtro = input('Linha (1, 2, 3 ou 4): ')
        campo = 'linha'
    elif opcao == '4':
        filtro = input('Tipo (lotação, atraso, interrupção, acessibilidade, segurança, outro): ').lower()
        campo = 'tipo'
    elif opcao == '1':
        campo = None
    else:
        return
    quantidade = 0
    for comentario in dados['comentarios']:
        if campo is None or comentario[campo] == filtro:
            print(comentario['data'], '-', comentario['estacao'], '-', comentario['tipo'])
            print(comentario['nome'] + ':', comentario['texto'])
            quantidade += 1
    if quantidade == 0:
        print('Nenhum comentário encontrado.')

def publicar_comentario(dados, usuario):
    print('\nPUBLICAR COMENTÁRIO')
    estacao = escolher_estacao()
    if estacao is None:
        return
    linhas = []
    for linha in LINHAS:
        if estacao in LINHAS[linha]:
            linhas.append(linha)
    print('Linhas disponíveis:')
    for numero_linha in linhas:
        print(numero_linha, '-', NOMES[numero_linha])
    linha = input('Linha: ')
    if linha not in linhas:
        print('Linha inválida para esta estação.')
        return
    tipos = ['lotação', 'atraso', 'interrupção', 'acessibilidade', 'segurança', 'outro']
    for i in range(len(tipos)):
        print(i + 1, '-', tipos[i])
    try:
        numero = int(input('Tipo: '))
        tipo = None
        if 1 <= numero <= len(tipos):
            tipo = tipos[numero - 1]
    except ValueError:
        tipo = None
    if tipo is None:
        print('Tipo inválido.')
        return
    texto = input('Comentário: ')
    if len(texto) < 3:
        print('Escreva um comentário maior.')
        return
    dados['comentarios'].append({'nome': usuario['nome'], 'email': usuario['email'],
                                 'estacao': estacao, 'linha': linha, 'tipo': tipo,
                                 'texto': texto, 'data': data_atual()})
    salvar(dados)
    print('Comentário publicado!')

def minha_conta(dados, usuario):
    while True:
        print('\nMINHA CONTA\n1 - Ver perfil\n2 - Alterar nome\n3 - Histórico de viagens\n0 - Voltar')
        opcao = input('Escolha: ')
        if opcao == '1':
            print('Nome:', usuario['nome'], '| E-mail:', usuario['email'])
        elif opcao == '2':
            nome = input('Novo nome: ')
            if len(nome) >= 2:
                usuario['nome'] = nome
                salvar(dados)
                print('Nome alterado!')
            else:
                print('Nome muito curto.')
        elif opcao == '3':
            encontrou = False
            for viagem in dados['historico']:
                if viagem['email'] == usuario['email']:
                    print(viagem['data'], '-', viagem['origem'], 'até', viagem['destino'],
                          '-', viagem['tempo'], 'min')
                    encontrou = True
            if not encontrou:
                print('Sem viagens registradas.')
        elif opcao == '0':
            return

def administrar(dados):
    while True:
        print('\nADMINISTRAÇÃO\n1 - Cadastrar ocorrência\n2 - Ver ocorrências\n3 - Remover comentário\n4 - Resolver ocorrência\n0 - Voltar')
        opcao = input('Escolha: ')
        if opcao == '1':
            estacao = escolher_estacao()
            if estacao is None:
                continue
            texto = input('Descreva a ocorrência simulada: ')
            if texto:
                dados['ocorrencias'].append({'estacao': estacao, 'texto': texto,
                                             'status': 'Em andamento', 'data': data_atual()})
                salvar(dados)
        elif opcao == '2' or opcao == '4':
            for i in range(len(dados['ocorrencias'])):
                o = dados['ocorrencias'][i]
                print(i + 1, '-', o['estacao'], '-', o['texto'], '-', o['status'])
            if opcao == '4':
                try:
                    numero = int(input('Número para resolver (0 cancela): '))
                    if 1 <= numero <= len(dados['ocorrencias']):
                        dados['ocorrencias'][numero - 1]['status'] = 'Resolvida'
                        salvar(dados)
                except ValueError:
                    print('Número inválido.')
        elif opcao == '3':
            for i in range(len(dados['comentarios'])):
                print(i + 1, '-', dados['comentarios'][i]['texto'])
            try:
                numero = int(input('Número para remover (0 cancela): '))
                if 1 <= numero <= len(dados['comentarios']):
                    dados['comentarios'].pop(numero - 1)
                    salvar(dados)
                    print('Comentário removido.')
            except ValueError:
                print('Número inválido.')
        elif opcao == '0':
            return

def menu_usuario(dados, usuario, rede):
    ultima_rota = None
    while True:
        print('\n===== FLUXFLOW =====')
        print('1 - Visualizar mapa\n2 - Planejar rota\n3 - Acompanhar viagem')
        print('4 - Consultar comentários\n5 - Publicar comentário\n6 - Minha conta')
        if usuario['perfil'] == 'admin':
            print('7 - Administração')
        print('0 - Fazer logout')
        opcao = input('Escolha: ')
        if opcao == '1':
            ver_mapa()
        elif opcao == '2':
            rota = planejar(dados, usuario, rede)
            if rota is not None:
                ultima_rota = rota
        elif opcao == '3':
            if ultima_rota is None:
                print('Primeiro planeje uma rota na opção 2.')
            else:
                acompanhar(rede, ultima_rota, dados)
        elif opcao == '4':
            consultar_comentarios(dados)
        elif opcao == '5':
            publicar_comentario(dados, usuario)
        elif opcao == '6':
            minha_conta(dados, usuario)
        elif opcao == '7' and usuario['perfil'] == 'admin':
            administrar(dados)
        elif opcao == '0':
            print('Logout realizado.')
            return
        else:
            print('Opção inválida.')

def main():
    dados = carregar()
    if dados is None:
        return

    existe_admin = False
    for usuario in dados['usuarios']:
        if usuario['email'] == 'admin@fluxflow.com':
            existe_admin = True
    if not existe_admin:
        dados['usuarios'].append({
            'nome': 'Administrador', 'email': 'admin@fluxflow.com',
            'senha': 'admin12345', 'perfil': 'admin'
        })
        salvar(dados)
    rede = montar_rede()
    while True:
        print('\n===== BEM-VINDO AO FLUXFLOW =====')
        print('1 - Cadastro\n2 - Login\n0 - Sair')
        opcao = input('Escolha: ')
        if opcao == '1':
            cadastrar(dados)
        elif opcao == '2':
            usuario = login(dados)
            if usuario is not None:
                menu_usuario(dados, usuario, rede)
        elif opcao == '0':
            print('Até logo!')
            break
        else:
            print('Opção inválida.')


main()
