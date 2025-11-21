package server.core;

import common.model.Election;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ElectionReport {
    public static void generate(ElectionManager manager, String filename) {
        System.out.println("[REPORT] Gerando relatório...");

        Election election = manager.getElection();
        List<Integer> results = manager.getResults();
        List<String> votedCpfs = manager.getVotedCpfs();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("========================================");
            writer.println("          RELATORIO DA ELEICAO          ");
            writer.println("========================================");

            String dateHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            writer.println("Gerado em: " + dateHour);
            writer.println();
            
            writer.println(election.getQuestion());
            writer.println("Total de Votos: " + votedCpfs.size());
            writer.println();

            writer.println("---------- RESULTADOS ----------");

            List<String> options = election.getOptions();
            for (int i = 0; i < options.size(); i++) {
                writer.printf("OpCAo %d: %s - Votos: %d%n", i, options.get(i), results.get(i));
            }

            writer.println();
            writer.println("CPFs que votaram:");
            for (String cpf : votedCpfs) {
                writer.println("- " + cpf);
            }

            writer.println("========================================");
            System.out.println("[REPORT] Relatorio salvo com sucesso!");
        
        } catch (IOException e) {
            System.err.println("[REPORT] Erro ao salvar: " + e.getMessage());
        }
    }
}