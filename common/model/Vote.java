package common.model;

import java.io.Serializable;

public class Vote implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String cpf;
    private int optionIndex;
    
    public Vote(String cpf, int optionIndex) {
        this.cpf = cpf;
        this.optionIndex = optionIndex;
    }
    
    public Vote() { }
    
    public String getCpf() { return cpf; }
    public int getOptionIndex() { return optionIndex; }

    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setOptionIndex(int optionIndex) { this.optionIndex = optionIndex; }
}