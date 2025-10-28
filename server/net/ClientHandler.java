package server.net;

public class ClientHandler extends Thread {

    @Override
    public void run() {
        System.out.println("Client handler started...");
    }
}

//Usa NetProtocol para interpretar Message e interage com o ElectionManager