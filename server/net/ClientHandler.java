package server.net;

import common.model.*;
import common.net.NetCommand;
import server.core.ElectionManager;

import java.io.*;
import java.net.Socket;

//Thread que atende individualmente cada cliente.
//Usa Message (comando + dados) e interage com o ElectionManager.
 
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
                // Aguarda mensagem do cliente
                Object received = in.readObject();

                if (received instanceof Message msg) {
                    System.out.println("[SERVER] Mensagem recebida de cliente #" + clientId + ": " + msg.getCommand());

                    switch (msg.getCommand()) {
                        case REQUEST_ELECTION -> {
                            // Cliente pediu os dados da eleição
                            Election election = electionManager.getElection();
                            Message response = new Message(NetCommand.SEND_ELECTION, election, true, "Election sent");
                            out.writeObject(response);
                            out.flush();
                        }

                        case SEND_VOTE -> {
                            // Cliente enviou o voto
                            Vote vote = (Vote) msg.getPayload();
                            boolean success = electionManager.receiveVote(vote);
                            Message response = new Message(NetCommand.SEND_VOTE, null, success,
                                    success ? "Vote registered!" : "Invalid or duplicate vote!");
                            out.writeObject(response);
                            out.flush();
                        }

                        case DISCONNECT -> {
                            // Cliente pediu para disconectar do servidor
                            running = false;
                            Message response = new Message(NetCommand.DISCONNECT, null, true, "Disconnected");
                            out.writeObject(response);
                            out.flush();
                        }

                        default -> {
                            // Comando desconhecido
                            Message response = new Message(NetCommand.ERROR, null, false, "Unknown command");
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