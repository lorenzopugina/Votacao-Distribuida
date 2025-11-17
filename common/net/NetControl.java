package common.net;

import common.model.Message;
import java.io.*;
import java.net.Socket;

public class NetControl {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public boolean connect(String host, int port) {
        try {
            this.socket = new Socket(host, port);

            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();

            this.in = new ObjectInputStream(socket.getInputStream());

            return true;

        } catch (IOException e) {
            System.err.println("[CLIENT] Falha ao conectar: " + e.getMessage());
            return false;
        }
    }

    public boolean send(Message msg) {
        return NetProtocol.send(out, msg);
    }

    public Message receive() {
        return NetProtocol.receive(in);
    }

    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}
