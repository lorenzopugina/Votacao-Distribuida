package common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Election implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String question;
    private List<String> options;
    
    public Election(String question, List<String> options) {
        this.question = question;
        this.options = new ArrayList<>(options);
    }
    
    public Election() { 
        this.options = new ArrayList<>();
    }
    
    public String getQuestion() { return question;}
    public List<String> getOptions() { return options; }

    public void setQuestion(String question) { this.question = question; }
    public void setOptions(List<String> options) { this.options = options; }
    
    public void addOption(String option) { this.options.add(option); }
}