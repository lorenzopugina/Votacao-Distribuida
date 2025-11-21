package client.net;

import common.model.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection {

    private final String serverAddress;
    private final int serverPort;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientConnection(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void send(Message msg) throws IOException {
        if (out != null) {
            out.writeObject(msg);
            out.flush();
        } else {
            throw new IOException("Output stream Não está inicializado. A conexão pode estar fechada ou não ter sido estabelecida.");
        }
    }

    public Message receive() throws IOException, ClassNotFoundException {
        if (in != null) {
            Object received = in.readObject();
            if (received instanceof Message) {
                return (Message) received;
            }
            throw new ClassNotFoundException("O objeto recebido não é do tipo Message.");
        } else {
            throw new IOException("Input stream Não está inicializado. A conexão pode estar fechada ou não ter sido estabelecida.");
        }
    }

    public void close() {
        try {
            if (out != null) out.close();
        } catch (IOException ignored) {}
        
        try {
            if (in != null) in.close();
        } catch (IOException ignored) {}
        
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
        
        //clear references
        out = null;
        in = null;
        socket = null;
    }

}
