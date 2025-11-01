package common.model;

import java.io.Serializable;

public class Vote implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String cpf;
    private int optionIndex;
    
    //construtor
    public Vote(String cpf, int optionIndex) {
        this.cpf = cpf;
        this.optionIndex = optionIndex;
    }
    
    //construtor sem nada/vazio
    public Vote() { //por conta da serialização (o envio pela rede)
    }//cria um voto vazio, dps preenche com setters
    
    //getters e setters
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public int getOptionIndex() {
        return optionIndex;
    }
    
    public void setOptionIndex(int optionIndex) {
        this.optionIndex = optionIndex;
    }

}