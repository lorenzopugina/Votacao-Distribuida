package server.core;

import common.model.Election;
import common.model.Vote;
import java.util.List;
import java.util.ArrayList;

public class ElectionManager {
    private Election currentElection;
    
    private List<String> votedCpfs;
    private List<Integer> voteCounts;

    public ElectionManager() {
        this.votedCpfs = new ArrayList<>();
        this.voteCounts = new ArrayList<>();

        loadElection();
    }

    public void loadElection() {
        List<String> options = new ArrayList<>();
        options.add("Antônio Azevedo");
        options.add("Bruna Bosco");
        options.add("Carlos Carvalho");

        this.currentElection = new Election("Para quem irá seu voto?", options);
        System.out.println("[MANAGER] Eleição carregada: " + this.currentElection.getQuestion());

        // reseta os dados da lista de CPFs e votos
        this.votedCpfs.clear();
        this.voteCounts.clear();

        for (int i = 0; i < this.currentElection.getOptions().size(); i++)
            this.voteCounts.add(0); 
    }

    public Election getElection() {
        return this.currentElection;
    }

    public synchronized boolean receiveVote(Vote vote) {
        String cpf = vote.getCpf();
        int optionIndex = vote.getOptionIndex();

        if(!CPFValidator.validate(cpf)) {
            System.out.println("[MANAGER] Voto REJEITADO: Formato de CPF inválido (" + cpf + ")");
            return false;
        }

        String cleanedCpf = cpf.replace(".","").replace("-", "");

        if (votedCpfs.contains(cleanedCpf)) {
            System.out.println("[MANAGER] Voto REJEITADO: Voto duplicado do CPF (" + cleanedCpf + ")");
            return false;
        }

        if (optionIndex < 0 || optionIndex >= voteCounts.size()) {
            System.out.println("[MANAGER] Voto REJEITADO: Opção de voto inválida (" + optionIndex + ")");
            return false;
        }

        votedCpfs.add(cleanedCpf);

        int currentCount = voteCounts.get(optionIndex);
        voteCounts.set(optionIndex, currentCount + 1);

        System.out.println("[MANAGER] Voto REGISTRADO: CPF " + cleanedCpf + " votou pela opção " + optionIndex);
        System.out.println("[MANAGER] Resultados atuais: " + voteCounts.toString()); 

        return true;
    }

    public List<Integer> getResults() {
        return new ArrayList<>(this.voteCounts);
    }
}

//Manipula Election e Vote
// carrega election, recebe voto, conta votos, etc.