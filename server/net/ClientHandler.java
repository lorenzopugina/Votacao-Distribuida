package server.net;

import common.model.*;
import common.net.NetCommand;
import common.net.NetProtocol;
import java.io.*;
import java.net.Socket;
import server.core.ElectionManager;

public class ClientHandler extends Thread {

    private final Socket clientSocket;
    private final int clientId;
    private final ElectionManager electionManager;

    public ClientHandler(Socket clientSocket, int clientId, ElectionManager electionManager) {
        this.clientSocket = clientSocket;
        this.clientId = clientId;
        this.electionManager = electionManager;
    }

    @Override
    public void run() {
        System.out.println("[SERVER] Cliente " + clientId + " sendo atendido...");

        try (
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
        ) {

            boolean running = true;

            while (running) {
                
                Message msg = NetProtocol.receive(in);

                if (msg == null) {
                    System.out.println("[SERVER] Cliente #" + clientId + " desconectou.");
                    break;
                }

                System.out.println("[SERVER] Mensagem recebida de cliente #" + clientId + ": " + msg.getCommand());

                switch (msg.getCommand()) {

                    case REQUEST_ELECTION -> {
                        Election election = electionManager.getElection();
                        Message response = new Message(
                                NetCommand.SEND_ELECTION,
                                election,
                                true,
                                "Eleição enviada"
                        );
                        NetProtocol.send(out, response);
                    }
                    case SEND_VOTE -> {
                        Vote vote = (Vote) msg.getPayload();
                        boolean success = electionManager.receiveVote(vote);

                        Message response = new Message(
                                NetCommand.SEND_VOTE,
                                null,
                                success,
                                success ? "Voto registrado!" : "voto inválido ou duplicado!"
                        );
                        NetProtocol.send(out, response);
                    }
                    case DISCONNECT -> {
                        running = false;
                        Message response = new Message(
                                NetCommand.DISCONNECT,
                                null,
                                true,
                                "Desconectado"
                        );
                        NetProtocol.send(out, response);
                    }

                    // UNKNOWN COMMAND
                    default -> {
                        Message response = new Message(
                                NetCommand.ERROR,
                                null,
                                false,
                                "Comando desconhecido"
                        );
                        NetProtocol.send(out, response);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("[SERVER] Erro no cliente #" + clientId + ": " + e.getMessage());

        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {}

            System.out.println("[SERVER] Conexão encerrada com cliente #" + clientId);
        }
    }
}
