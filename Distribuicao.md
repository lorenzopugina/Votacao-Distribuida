# Maria Clara Pessoa 1 – Modelo de Dados

Pacote: common.model
Responsabilidades:

    Classe Election: atributos String question, List<String> options
    Classe Vote: atributos String cpf, int optionIndex
    Classe Message: atributos NetCommand command, Object payload
    Garantir que todas implementem Serializable
    Criar construtores, getters, setters
    Objetivo final: fornecer objetos para trafegar pela rede.


# Pessoa 2 – Protocolo de Comunicação

Pacote: common.net
Responsabilidades:

    Classe NetCommand: definir comandos (ex: SEND_ELECTION, RECEIVE_VOTE, END)
    Classe NetProtocol: métodos para construir mensagens
    Adaptar classes já existentes (NetControl, NetManagement) para enviar/receber objetos Message
    Objetivo final: padronizar como servidor e cliente trocam mensagens.


# Pessoa 3 – Core do Servidor

Pacote: server.core
Responsabilidades:

    ElectionManager: armazenar eleição, registrar votos, impedir duplicidade (HashSet de CPF)
    CPFValidator: validar CPF com algoritmo simples
    Métodos principais: loadElection(), receiveVote(Vote v), getResults()
    Objetivo final: lógica principal da votação.


# Pessoa 4 – Threads e Conexão do Servidor

Pacote: server.net
Responsabilidades:

    ServerMain: inicializar servidor, carregar eleição, abrir porta TCP
    ClientHandler: herda Thread, trata cada cliente individualmente
    Receber Message com comando, delegar para ElectionManager, devolver resposta
    Objetivo final: permitir múltiplos clientes simultâneos.


# Pessoa 5 – Cliente (Conexão e UI)

Pacotes: client.net, client.ui
Responsabilidades:

ClientMain: inicia GUI
ClientConnection: gerencia conexão TCP, envia e recebe Message
ClientGUI: interface gráfica com tela de CPF + opções de voto
Objetivo final: permitir ao usuário votar de forma simples.


# Pessoa 6 – Interface do Servidor

Pacote: server.ui
Responsabilidades:

    ServerGUI: botões "Carregar Eleição", "Iniciar Servidor", "Encerrar"
    Mostrar resultados em tempo real usando tabela ou gráfico Swing
    Usar Observer ou polling para atualizar interface conforme votos chegam
    Objetivo final: visualização em tempo real do processo de votação.
