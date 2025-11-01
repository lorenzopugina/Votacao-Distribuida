package common.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;//guarda lista de opcoes

public class Election implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String question;
    private List<String> options;
    
    //construtor
    public Election(String question, List<String> options) {
        this.question = question;
        this.options = new ArrayList<>(options);
    }
    
    //construtor vazio/sem nada
    public Election() { //cria uma elição que vai ser preenchida com setters
        this.options = new ArrayList<>();
    }
    
    //getters e setters
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public void setOptions(List<String> options) {
        this.options = options;
    }
    
    public void addOption(String option) {
        this.options.add(option);
    }
    
}