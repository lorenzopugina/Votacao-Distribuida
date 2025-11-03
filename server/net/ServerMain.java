package server.net; 

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import server.core.ElectionManager;

// inicia o servidor, abre porta TCP e cria threads para cada cliente.

public class ServerMain {

    private static final int PORT = 5000; // Porta do servidor
    private static AtomicInteger clientCounter = new AtomicInteger(0); // AtomicInteger é thread-safe
    private static ElectionManager electionManager = new ElectionManager();

    public static void main(String[] args) {
        System.out.println("[SERVER] Iniciando servidor de votação...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[SERVER] Aguardando conexões na porta " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                int clientId = clientCounter.incrementAndGet();
                System.out.println("[SERVER] Cliente #" + clientId + " conectado de " + clientSocket.getInetAddress());

                // Cria e inicia uma thread para atender o cliente
                ClientHandler handler = new ClientHandler(clientSocket, clientId, electionManager);
                handler.start();
            }

        } catch (IOException e) {
            System.err.println("[SERVER] Erro no servidor: " + e.getMessage());
        }
    }
}