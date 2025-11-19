package server.core;

import common.model.Election;
import common.model.Vote;
import java.util.ArrayList;
import java.util.List;

public class ElectionManager {

    private Election currentElection;
    private List<String> votedCpfs;
    private List<Integer> voteCounts;

    public ElectionManager() {
        this.votedCpfs = new ArrayList<>();
        this.voteCounts = new ArrayList<>();
    }

    public void loadElection(String question, List<String> options) {
        this.currentElection = new Election(question, options);

        System.out.println("[MANAGER] Nova eleição criada: " + question);

        this.votedCpfs.clear();
        this.voteCounts.clear();

        for (int i = 0; i < options.size(); i++) {
            this.voteCounts.add(0);
        }
    }

    public Election getElection() {
        return this.currentElection;
    }

    public synchronized boolean receiveVote(Vote vote) {
        String cpf = vote.getCpf();
        int optionIndex = vote.getOptionIndex();

        if (!CPFValidator.validate(cpf)) {
            System.out.println("[MANAGER] Voto REJEITADO: CPF inválido (" + cpf + ")");
            return false;
        }

        String cleanedCpf = cpf.replace(".", "").replace("-", "");

        if (votedCpfs.contains(cleanedCpf)) {
            System.out.println("[MANAGER] Voto REJEITADO: CPF já votou (" + cleanedCpf + ")");
            return false;
        }

        if (optionIndex < 0 || optionIndex >= voteCounts.size()) {
            System.out.println("[MANAGER] Voto REJEITADO: opção inválida (" + optionIndex + ")");
            return false;
        }

        votedCpfs.add(cleanedCpf);

        voteCounts.set(optionIndex, voteCounts.get(optionIndex) + 1);

        System.out.println("[MANAGER] Voto registrado do CPF " + cleanedCpf);
        return true;
    }

    public List<Integer> getResults() {
        return new ArrayList<>(voteCounts);
    }

    public List<String> getVotedCpfs() {
        return new ArrayList<>(votedCpfs);
    }
}
