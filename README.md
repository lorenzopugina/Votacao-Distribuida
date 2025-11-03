# Arquitetura
MVC Simples - Commom, server e client para separar visões e responsabbilidades

Modelo (Model - pacote common.model)

    Election – contém pergunta e opções
    Vote – associa CPF e opção escolhida
    Message – classe serializável para enviar pacotes via rede

Controle (Controller - lógica de rede e do servidor, pacotes net)

    NetControl, NetProtocol e NetCommand (do professor) já servem como base
    ClientHandler (thread para cada cliente conectado) - tambem do prof
    ElectionManager (controla votos no servidor)

Visão (View - interfaces Swing, pacotes ui)

    GUI do servidor mostrando gráficos em tempo real
    GUI do cliente com campo CPF + botões de voto


# Responsabilidades (Resumo)
Servidor

    Carrega eleição (pergunta + opções)
    Abre porta TCP
    Para cada cliente conectado cria uma thread (ClientHandler)
    Recebe voto, valida CPF, salva
    Atualiza interface em tempo real

Cliente

    Conecta ao IP/porta do servidor
    Recebe dados da eleição
    Interface GUI exibe opções
    Usuário vota
    Cliente envia voto e encerra

# Fluxo das classes

comum
    Message representa mensagens trocadas entre cliente e servidor, indicando comandos, resultados e status da conexão
    Vote representa o voto de um usuário (CPF + opção escolhida)
    Election contém a pergunta e as opções de voto disponíveis
    NetCommand: Enum que lista os comandos possíveis da rede (ex: REQUEST_ELECTION, SEND_VOTE, DISCONNECT)
    NetControl: Classe base que controla a conexão com sockets e streams
    NetManagement fornece utilitários ou controle geral da rede (gerencia fluxo de conexão, interrupções)
    NetProtocol define métodos para construir, enviar e interpretar mensagens (Message) de forma padronizada

Cliente
    ClientMain inicia ClientGUI
    ClientGUI Mostra tela de CPF, opções e botão de voto e usa ClientConnection para conectar ao servidor
    ClientConnection envia/recebe objetos Message (Faz a conexão TCP com o servidor, envia e recebe objetos Message)

servidor 
    ServerMain inicia o servidor, abre porta TCP
    ClientHandler: thread que atende cada cliente individualmente
    ElectionManager: regra de negócio do servidor: armazena eleição, registra votos, impede duplicidade e valida CPF(com a ajuda do CPFValidator)
    ServerGUI moostra resultados em tempo real e opções de controle (iniciar, encerrar, visualizar votos)

geral
    O servidor inicia (ServerMain) e cria eleição no ElectionManager
    O cliente inicia (ClientMain) e abre GUI (ClientGUI)
    ClientGUI chama ClientConnection.connect()
    ClientConnection envia Message(NetCommand.REQUEST_ELECTION)
    ClientHandler recebe, consulta ElectionManager e envia de volta Message com Election
    O cliente recebe a eleição e exibe na interface
    Usuário vota: vote é enviado via Message(NetCommand.SEND_VOTE, vote)
    ClientHandler chama ElectionManager.receiveVote(vote)
    Resultados são atualizados e enviados ao ServerGUI em tempo real