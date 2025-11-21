package server.net;

import common.model.*;
import common.net.NetCommand;
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

        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            boolean running = true;

            while (running) {
                Object received = in.readObject();

                if (received instanceof Message msg) {
                    System.out.println("[SERVER] Mensagem recebida de cliente #" + clientId + ": " + msg.getCommand());

                    switch (msg.getCommand()) {
                        case REQUEST_ELECTION -> {
                            Election election = electionManager.getElection();
                            Message response = new Message(NetCommand.SEND_ELECTION, election, true, "Eleição enviada");
                            out.writeObject(response);
                            out.flush();
                        }

                        case SEND_VOTE -> {
                            Vote vote = (Vote) msg.getPayload();
                            boolean success = electionManager.receiveVote(vote);
                            Message response = new Message(NetCommand.SEND_VOTE, null, success,
                                    success ? "Voto registrado!" : "voto inválido ou duplicado!");
                            out.writeObject(response);
                            out.flush();
                        }

                        case DISCONNECT -> {
                            running = false;
                            Message response = new Message(NetCommand.DISCONNECT, null, true, "DIsconectado");
                            out.writeObject(response);
                            out.flush();
                        }

                        default -> {
                            Message response = new Message(NetCommand.ERROR, null, false, "Comando disconhecido");
                            out.writeObject(response);
                            out.flush();
                        }
                    }
                }
            }

        } catch (EOFException e) {
            System.out.println("[SERVER] Cliente #" + clientId + " desconectou.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[SERVER] Erro no cliente #" + clientId + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {}
            System.out.println("[SERVER] Conexão encerrada com cliente #" + clientId);
        }
    }
}