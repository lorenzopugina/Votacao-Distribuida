package server.ui;

import javax.swing.JFrame;

public class ServerGUI extends JFrame {

    public ServerGUI() {
        super("Election Server");
        setSize(1400, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}

//Mostra resultados em tempo real e opções de controle (iniciar, encerrar, visualizar votos)