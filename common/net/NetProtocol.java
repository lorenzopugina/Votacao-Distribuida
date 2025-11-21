package common.net;

import common.model.Message;
import java.io.*;

public class NetProtocol {

    public static boolean send(ObjectOutputStream out, Message msg) {
        try {
            synchronized (out) {
                out.writeObject(msg);
                out.flush();
                out.reset(); 
            }
            return true;
        } catch (IOException e) {
            System.err.println("[NET] Erro ao enviar mensagem: " + e.getMessage());
            return false;
        }
    }

    public static Message receive(ObjectInputStream in) {
        try {
            Object obj = in.readObject();

            if (obj instanceof Message msg) {
                return msg;
            }

            return new Message(NetCommand.ERROR, null, false, "Tipo de objeto inválido recebido");

        } catch (EOFException e) {
            System.out.println("[NET] Conexão finalizada pelo remoto.");
            return null;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[NET] Erro ao receber mensagem: " + e.getMessage());
            return null;
        }
    }

    public static Message build(NetCommand cmd, Object payload, boolean ok, String text) {
        return new Message(cmd, payload, ok, text);
    }
}
