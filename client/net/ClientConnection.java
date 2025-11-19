package client.net;

import common.model.Message;
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

    private String serverAddress;
    private int serverPort;

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
    public void connect() {
        // TODO:
        // 1. Criar o socket: new Socket(serverAddress, serverPort)
        // 2. Criar ObjectOutputStream e ObjectInputStream
        // 3. Lidar com IOException
    }

    //Envia uma Message ao servidor
    public void send(Message msg) {
        // TODO:
        // usar out.writeObject(msg) e out.flush()
        // capturar IOException
    }

    //Recebe uma Message do servidor
    public Message receive() {
        // TODO:
        // usar in.readObject()
        // retornar o objeto como Message
        // capturar IOException | ClassNotFoundException
        return null;
    }

    //Fecha a conexão e os streams.
    public void close() {
        // TODO:
        // fechar streams e socket
        // verificar null antes de fechar
    }

}

/*
    classe responsável por abrir o Socket
    ter ObjectInputStream e ObjectOutputStream
    enviar mensagens
    receber respostas
*/