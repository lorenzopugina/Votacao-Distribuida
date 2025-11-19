package server.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ServerGUI extends JFrame {
    private JButton loadElectionButton;
    private JButton startServerButton;
    private JButton stopServerButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;

    public ServerGUI() {
        super("Election Server");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel with buttons
        JPanel topPanel = new JPanel();
        loadElectionButton = new JButton("Load Election");
        startServerButton = new JButton("Start Server");
        stopServerButton = new JButton("Stop Server");
        stopServerButton.setEnabled(false); // Initially disabled
        topPanel.add(loadElectionButton);
        topPanel.add(startServerButton);
        topPanel.add(stopServerButton);
        add(topPanel, BorderLayout.NORTH);

        // Center panel with results table
        String[] columnNames = {"Option", "Votes"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        // Add button listeners
        loadElectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadElection();
            }
        });

        startServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });

        stopServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopServer();
            }
        });

        // Simulate real-time updates (polling example)
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateResults();
            }
        }, 0, 2000); // Update every 2 seconds
    }

    private void loadElection() {
        // Logic to load election (e.g., from a file or input dialog)
        JOptionPane.showMessageDialog(this, "Election loaded successfully!");
    }

    private void startServer() {
        // Logic to start the server
        startServerButton.setEnabled(false);
        stopServerButton.setEnabled(true);
        JOptionPane.showMessageDialog(this, "Server started!");
    }

    private void stopServer() {
        // Logic to stop the server
        startServerButton.setEnabled(true);
        stopServerButton.setEnabled(false);
        JOptionPane.showMessageDialog(this, "Server stopped!");
    }

    private void updateResults() {
        // Simulate fetching results from the server
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0); // Clear existing rows
            Map<String, Integer> results = getResultsFromServer();
            for (Map.Entry<String, Integer> entry : results.entrySet()) {
                tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
            }
        });
    }

    private Map<String, Integer> getResultsFromServer() {
        // Placeholder for server logic to fetch results
        return Map.of("Option A", 10, "Option B", 15, "Option C", 5);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerGUI gui = new ServerGUI();
            gui.setVisible(true);
        });
    }
}