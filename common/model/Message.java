package common.model;

import java.io.Serializable;
import common.net.NetCommand; 

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private NetCommand command;
    private Object payload;//guarda dados (como Election, Vote, etc
    private boolean success;
    private String message;//mensagem adicional (erro, info, etc)
    
    //construtor
    public Message(NetCommand command, Object payload, boolean success, String message) {
        this.command = command;
        this.payload = payload;
        this.success = success;
        this.message = message;
    }
    
    //construtor sem nd/vazio para ser preenchido com setters
    public Message() {
    }
    
    //getters e setters
    public NetCommand getCommand() {
        return command;
    }
    
    public void setCommand(NetCommand command) {
        this.command = command;
    }
    
    public Object getPayload() {
        return payload;
    }
    
    public void setPayload(Object payload) {
        this.payload = payload;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}