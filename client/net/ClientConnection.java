package client.net;

import common.model.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*
    Classe responsável por gerenciar toda a comunicação TCP/IP
    do cliente com o servidor.
    
    Aqui ficam APENAS os métodos de conexão, envio e recebimento
    Toda a lógica de interface fica no ClientGUI
 */
public class ClientConnection {

    private final String serverAddress;
    private final int serverPort;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    //Construtor: recebe endereço IP/hostname e porta do servidor
    public ClientConnection(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    /*
        Estabelece a conexão com o servidor
        Cria o socket e os streams de objeto
    */
    public void connect() throws IOException {
        // 1. Criar o socket: new Socket(serverAddress, serverPort)
        socket = new Socket(serverAddress, serverPort);
        
        // 2. Criar ObjectOutputStream e ObjectInputStream
        // IMPORTANTE: O ObjectOutputStream DEVE ser criado primeiro para evitar deadlock
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        
        // 3. Lidar com IOException (lançada pelo método)
    }

    //Envia uma Message ao servidor
    public void send(Message msg) throws IOException {
        // usar out.writeObject(msg) e out.flush()
        if (out != null) {
            out.writeObject(msg);
            out.flush();
        } else {
            throw new IOException("Output stream is not initialized. Connection may be closed or not established.");
        }
        // capturar IOException (lançada pelo método)
    }

    //Recebe uma Message do servidor
    public Message receive() throws IOException, ClassNotFoundException {
        // usar in.readObject()
        // retornar o objeto como Message
        if (in != null) {
            Object received = in.readObject();
            if (received instanceof Message) {
                return (Message) received;
            }
            throw new ClassNotFoundException("Received object is not a Message type.");
        } else {
            throw new IOException("Input stream is not initialized. Connection may be closed or not established.");
        }
        // capturar IOException | ClassNotFoundException (lançadas pelo método)
    }

    //Fecha a conexão e os streams.
    public void close() {
        // fechar streams e socket
        // verificar null antes de fechar
        try {
            if (out != null) out.close();
        } catch (IOException ignored) {}
        
        try {
            if (in != null) in.close();
        } catch (IOException ignored) {}
        
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
        
        // Limpar referências
        out = null;
        in = null;
        socket = null;
    }

}
