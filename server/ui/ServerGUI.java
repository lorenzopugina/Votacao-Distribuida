package server.ui;

import common.model.Election;
import common.ui.Credits;
import common.ui.Help;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import server.core.ElectionManager;
import server.core.ElectionReport;
import server.net.ClientHandler;

public class ServerGUI extends JFrame {

    private JButton newElectionButton;
    private JButton startServerButton;
    private JButton stopServerButton;

    private JTable resultsTable;
    private DefaultTableModel tableModel;

    private ServerSocket serverSocket;
    private Thread serverThread;
    private AtomicInteger clientCounter = new AtomicInteger(0);

    private ElectionManager electionManager = new ElectionManager();

    private JTextField portField;
    private JLabel ipLabel;
    private JButton refreshIpButton;

    public ServerGUI() {
        super("Election Server");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createMenuBar();

        // TOP PANEL
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portField = new JTextField("5000", 5);
        portPanel.add(new JLabel("Porta:"));
        portPanel.add(portField);
        // label para exibir o IP local
        ipLabel = new JLabel("IP: " + getLocalIpAddress());
        portPanel.add(Box.createHorizontalStrut(12));
        portPanel.add(ipLabel);
        // botão para atualizar o IP à mão
        refreshIpButton = new JButton("Atualizar IP");
        refreshIpButton.addActionListener(ev -> ipLabel.setText("IP: " + getLocalIpAddress()));
        portPanel.add(refreshIpButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        newElectionButton = new JButton("Nova Eleição");
        startServerButton = new JButton("Iniciar Votação");
        stopServerButton = new JButton("Parar Votação");
        stopServerButton.setEnabled(false);

        buttonPanel.add(newElectionButton);
        buttonPanel.add(startServerButton);
        buttonPanel.add(stopServerButton);

        topPanel.add(portPanel);
        topPanel.add(buttonPanel);

        add(topPanel, BorderLayout.NORTH);


        // TABLE
        String[] cols = {"Opção", "Votos"};
        tableModel = new DefaultTableModel(cols, 0);
        resultsTable = new JTable(tableModel);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        // LISTENERS
        newElectionButton.addActionListener(e -> newElection());
        startServerButton.addActionListener(e -> startServer());
        stopServerButton.addActionListener(e -> stopServer());

        // TIMER FOR LIVE RESULTS
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateResults();
            }
        }, 1000, 2000);
    }

    // MENU BAR (OPTIONS, HELP, CREDITS, EXIT)
    private void createMenuBar() {
        JMenuBar bar = new JMenuBar();

        bar.add(Box.createHorizontalGlue());

        JMenu menuSettings = new JMenu("Opções");

        JMenuItem itemHelp = new JMenuItem("Ajuda");
        itemHelp.addActionListener(e ->
            Help.show("common/resources/serverHelp.txt")
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

    private void newElection() {
        JTextField questionField = new JTextField();
        JTextField optionCountField = new JTextField();

        Object[] fields = {
                "Pergunta da eleição:", questionField,
                "Quantidade de opções:", optionCountField
        };

        int ok = JOptionPane.showConfirmDialog(this, fields,
                "Nova Eleição", JOptionPane.OK_CANCEL_OPTION);

        if (ok != JOptionPane.OK_OPTION) return;

        String question = questionField.getText().trim();
        int optionCount;

        try {
            optionCount = Integer.parseInt(optionCountField.getText().trim());
            if (optionCount <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Número de opções inválido.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> options = new java.util.ArrayList<>();

        for (int i = 0; i < optionCount; i++) {
            String opt = JOptionPane.showInputDialog(
                    this,
                    "Nome da opção " + (i + 1) + ":"
            );
            if (opt == null || opt.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Opção inválida.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            options.add(opt);
        }

        electionManager = new ElectionManager();
        electionManager.loadElection(question, options);

        JOptionPane.showMessageDialog(this, "Eleição criada com sucesso!");
    }

    private void startServer() {
        serverThread = new Thread(() -> {
            try {

                int port = Integer.parseInt(portField.getText().trim());

                serverSocket = new ServerSocket(port);
                System.out.println("[SERVER] Servidor iniciado na porta " + port);

                while (!Thread.currentThread().isInterrupted()) {
                    Socket client = serverSocket.accept();
                    int id = clientCounter.incrementAndGet();
                    System.out.println("[SERVER] Cliente #" + id + " conectado.");

                    ClientHandler handler = new ClientHandler(client, id, electionManager);
                    handler.start();
                }
            } catch (IOException e) {
                System.out.println("[SERVER] Servidor finalizado.");
            }
        });

        serverThread.start();

        startServerButton.setEnabled(false);
        stopServerButton.setEnabled(true);

        JOptionPane.showMessageDialog(this, "Servidor iniciado!");
    }

    private void stopServer() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException ignored) {}

        if (serverThread != null)
            serverThread.interrupt();

        startServerButton.setEnabled(true);
        stopServerButton.setEnabled(false);

        JOptionPane.showMessageDialog(this, "Servidor parado!");

        int ok = JOptionPane.showConfirmDialog(
                this, "Deseja gerar relatório da eleição?",
                "Relatório", JOptionPane.YES_NO_OPTION
        );

        if (ok == JOptionPane.YES_OPTION) {
            ElectionReport.generate(electionManager, "relatorio.txt");
        }
    }

    private void updateResults() {
        Election election = electionManager.getElection();
        if (election == null) return;

        List<String> options = election.getOptions();
        List<Integer> results = electionManager.getResults();

        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            for (int i = 0; i < options.size(); i++) {
                tableModel.addRow(new Object[]{
                        options.get(i),
                        results.get(i)
                });
            }
        });
    }

    // procura um endereço IPv4 não-loopback nas interfaces ativas
    private String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while (nets.hasMoreElements()) {
                NetworkInterface iface = nets.nextElement();
                if (!iface.isUp() || iface.isLoopback()) continue;
                Enumeration<InetAddress> addrs = iface.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (addr instanceof java.net.Inet4Address) {
                        String ip = addr.getHostAddress();
                        if (!"127.0.0.1".equals(ip)) return ip;
                    }
                }
            }
        } catch (SocketException ignored) {}
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}
