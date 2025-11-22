package client.ui;

import client.net.ClientConnection;
import common.model.Election;
import common.model.Message;
import common.model.Vote;
import common.net.NetCommand;
import common.ui.Credits;
import common.ui.Help;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import server.core.CPFValidator;

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

    private JPanel centerPanel;

    public ClientGUI() {
        super("Voting Client");
        setSize(650, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createMenuBar();

        // TOP PANEL (Connection)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel connectionRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        serverField = new JTextField("localhost", 12);
        portField = new JTextField("5000", 5);

        connectionRow1.add(new JLabel("IP:"));
        connectionRow1.add(serverField);
        connectionRow1.add(new JLabel("Porta:"));
        connectionRow1.add(portField);

        JPanel connectionRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectButton = new JButton("Começar votação");
        disconnectButton = new JButton("Sair da votação");
        disconnectButton.setEnabled(false);

        connectionRow2.add(connectButton);
        connectionRow2.add(disconnectButton);

        topPanel.add(connectionRow1);
        topPanel.add(connectionRow2);

        add(topPanel, BorderLayout.NORTH);


        // CENTER PANEL (Election)
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        questionLabel = new JLabel("Conecte-se para votar.", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(questionLabel);
        add(centerPanel, BorderLayout.CENTER);

        // BOTTOM PANEL (Vote)
        JPanel votePanel = new JPanel();

        cpfField = new JTextField(12);
        voteButton = new JButton("Votar");
        voteButton.setEnabled(false);

        votePanel.add(new JLabel("CPF:"));
        votePanel.add(cpfField);
        votePanel.add(voteButton);

        add(votePanel, BorderLayout.SOUTH);

        // LOG PANEL
        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.EAST);

        // LISTENERS
        connectButton.addActionListener(e -> connectToServer());
        disconnectButton.addActionListener(e -> disconnect());
        voteButton.addActionListener(e -> sendVote());
    }

    // MENU BAR
    private void createMenuBar() {

        JMenuBar bar = new JMenuBar();

        bar.add(Box.createHorizontalGlue());

        JMenu menuSettings = new JMenu("Opções");

        JMenuItem itemHelp = new JMenuItem("Ajuda");
        itemHelp.addActionListener(e ->
            Help.show("common/resources/clientHelp.txt")
        );
        menuSettings.add(itemHelp);

        JMenuItem itemCredits = new JMenuItem("Créditos");
        itemCredits.addActionListener(e -> Credits.show());
        menuSettings.add(itemCredits);

        JMenuItem itemExit = new JMenuItem("Sair do Programa");
        itemExit.addActionListener(e -> dispose());
        menuSettings.add(itemExit);

        bar.add(menuSettings);
        setJMenuBar(bar);
    }

    // CONNECT TO SERVER
    private void connectToServer() {
        try {
            String host = serverField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());

            connection = new ClientConnection(host, port);
            connection.connect();

            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);

            log("Conectado ao servidor");

            requestElection();

        } catch (Exception e) {
            log("Falha na conexão: " + e.getMessage());
        }
    }

    // REQUEST ELECTION
    private void requestElection() {
        try {
            Message req = new Message(NetCommand.REQUEST_ELECTION, null, true, "");
            connection.send(req);

            Message resp = connection.receive();

            if (resp.getCommand() == NetCommand.SEND_ELECTION) {
                currentElection = (Election) resp.getPayload();
                loadElectionOnScreen(currentElection);
                log("Eleição recebida com sucesso");
            } else {
                log("Comando inesperado");
            }

        } catch (Exception e) {
            log("Erro ao receber votação: " + e.getMessage());
        }
    }

    // LOAD ELECTION ON SCREEN
    private void loadElectionOnScreen(Election election) {

        centerPanel.removeAll();

        questionLabel.setText(election.getQuestion());
        centerPanel.add(questionLabel);

        optionGroup = new ButtonGroup();
        List<String> options = election.getOptions();
        optionButtons = new JRadioButton[options.size()];

        for (int i = 0; i < options.size(); i++) {
            optionButtons[i] = new JRadioButton(options.get(i));
            optionGroup.add(optionButtons[i]);
            centerPanel.add(optionButtons[i]);
        }

        voteButton.setEnabled(true);

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    // SEND VOTE
    private void sendVote() {
        try {
            String cpf = cpfField.getText().trim();

            if (!CPFValidator.validate(cpf)) {
                JOptionPane.showMessageDialog(this, "CPF Inválido ou ja votou!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int selectedIndex = -1;
            for (int i = 0; i < optionButtons.length; i++) {
                if (optionButtons[i].isSelected()) {
                    selectedIndex = i;
                    break;
                }
            }

            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Escolha uma opção!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Vote vote = new Vote(cpf, selectedIndex);
            Message req = new Message(NetCommand.SEND_VOTE, vote, true, "");

            connection.send(req);

            Message resp = connection.receive();

            JOptionPane.showMessageDialog(this, resp.getMessage());
            log("Voto enviado: " + resp.getMessage());

        } catch (Exception e) {
            log("Erro ao votar: " + e.getMessage());
        }
    }

    // DISCONNECT
    private void disconnect() {
        try {
            if (connection != null) {
                Message msg = new Message(NetCommand.DISCONNECT, null, true, "");
                connection.send(msg);
                connection.close();
            }

            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);

            log("Disconectado.");

        } catch (Exception e) {
            log("Erro ao disconectar: " + e.getMessage());
        }
    }

    private void log(String txt) {
        logArea.append(txt + "\n");
    }
}
