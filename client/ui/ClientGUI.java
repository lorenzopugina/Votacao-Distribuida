package client.ui;

import javax.swing.JFrame;

public class ClientGUI extends JFrame {

    public ClientGUI() {
        super("Election Client");
        setSize(1400, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}

//Mostra tela de CPF, opções e botão de voto; Usa ClientConnection