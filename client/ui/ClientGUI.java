package client.ui;

import client.net.ClientConnection;
import common.model.Election;
import common.model.Message;
import common.model.Vote;
import common.net.NetCommand;
import server.core.CPFValidator; 

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ClientGUI extends JFrame {

    private JTextField serverField;
    private JTextField portField;
    private JButton connectButton;
    private JButton disconnectButton;

    private JTextField cpfField;
    private JButton voteButton;

    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup optionGroup;

    private JTextArea logArea;

    private ClientConnection connection;
    private Election currentElection;

    public ClientGUI() {
        super("Cliente de Votação");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // PAINEL SUPERIOR (conexão)
        JPanel topPanel = new JPanel(new GridLayout(2, 3));

        serverField = new JTextField("localhost");
        portField = new JTextField("5000");

        connectButton = new JButton("Conectar");
        disconnectButton = new JButton("Desconectar");
        disconnectButton.setEnabled(false);

        topPanel.add(new JLabel("Servidor:"));
        topPanel.add(new JLabel("Porta:"));
        topPanel.add(new JLabel(""));

        topPanel.add(serverField);
        topPanel.add(portField);
        topPanel.add(connectButton);

        add(topPanel, BorderLayout.NORTH);

        // PAINEL CENTRAL (eleição)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        questionLabel = new JLabel("Nenhuma eleição carregada.");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(questionLabel);

        optionGroup = new ButtonGroup();

        add(centerPanel, BorderLayout.CENTER);

        // PAINEL DE VOTO
        JPanel votePanel = new JPanel();

        cpfField = new JTextField(12);
        voteButton = new JButton("Votar");
        voteButton.setEnabled(false);

        votePanel.add(new JLabel("CPF:"));
        votePanel.add(cpfField);
        votePanel.add(voteButton);

        add(votePanel, BorderLayout.SOUTH);

        // LOG
        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.EAST);

        // LISTENERS
        connectButton.addActionListener(e -> connectToServer());
        disconnectButton.addActionListener(e -> disconnect());
        voteButton.addActionListener(e -> sendVote());
    }

    // CONECTAR AO SERVIDOR
    private void connectToServer() {
        try {
            String host = serverField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());

            connection = new ClientConnection(host, port);
            connection.connect();

            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);

            log("Conectado ao servidor.");

            requestElection();

        } catch (Exception e) {
            log("Erro ao conectar: " + e.getMessage());
        }
    }

    // PEDIR ELEIÇÃO
    private void requestElection() {
        try {
            Message req = new Message(NetCommand.REQUEST_ELECTION, null, true, "");
            connection.send(req);

            Message resp = connection.receive();

            if (resp.getCommand() == NetCommand.SEND_ELECTION) {
                currentElection = (Election) resp.getPayload();
                loadElectionOnScreen(currentElection);
                log("Eleição recebida.");
            } else {
                log("Servidor retornou comando inesperado.");
            }

        } catch (Exception e) {
            log("Erro ao solicitar eleição: " + e.getMessage());
        }
    }

    // MOSTRAR ELEIÇÃO NA TELA
    private void loadElectionOnScreen(Election election) {

        questionLabel.setText(election.getQuestion());

        // Remove radio buttons antigos
        Container center = getContentPane().getComponent(1);
        JPanel panel = (JPanel) center;
        panel.removeAll();

        panel.add(questionLabel);

        optionGroup = new ButtonGroup();
        List<String> options = election.getOptions();
        optionButtons = new JRadioButton[options.size()];

        for (int i = 0; i < options.size(); i++) {
            optionButtons[i] = new JRadioButton(options.get(i));
            optionGroup.add(optionButtons[i]);
            panel.add(optionButtons[i]);
        }

        voteButton.setEnabled(true);

        revalidate();
        repaint();
    }

    // ENVIAR VOTO
    private void sendVote() {
        try {
            String cpf = cpfField.getText().trim();

            // valida CPF localmente
            if (!CPFValidator.validate(cpf)) {
                JOptionPane.showMessageDialog(this, "CPF inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // pega opção selecionada
            int selectedIndex = -1;
            for (int i = 0; i < optionButtons.length; i++) {
                if (optionButtons[i].isSelected()) {
                    selectedIndex = i;
                    break;
                }
            }

            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Selecione uma opção!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Vote vote = new Vote(cpf, selectedIndex);

            Message req = new Message(NetCommand.SEND_VOTE, vote, true, "");
            connection.send(req);

            Message resp = connection.receive();

            JOptionPane.showMessageDialog(this, resp.getMessage());
            log("Voto enviado, resposta: " + resp.getMessage());

        } catch (Exception e) {
            log("Erro ao votar: " + e.getMessage());
        }
    }

    // DESCONECTAR
    private void disconnect() {
        try {
            if (connection != null) {
                Message msg = new Message(NetCommand.DISCONNECT, null, true, "");
                connection.send(msg);
                connection.close();
            }

            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);

            log("Desconectado.");

        } catch (Exception e) {
            log("Erro ao desconectar: " + e.getMessage());
        }
    }

    // LOG
    private void log(String txt) {
        logArea.append(txt + "\n");
    }
}
