package common.model;

import common.net.NetCommand;
import java.io.Serializable; 

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private NetCommand command;
    private Object payload;
    private boolean success;
    private String message;
    
    public Message(NetCommand command, Object payload, boolean success, String message) {
        this.command = command;
        this.payload = payload;
        this.success = success;
        this.message = message;
    }
    
    public Message() {}
    
    public NetCommand getCommand() { return command; }
    public Object getPayload() { return payload; }
    public String getMessage() { return message; }

    public void setCommand(NetCommand command) { this.command = command; }
    public void setPayload(Object payload) { this.payload = payload; }
    public void setMessage(String message) { this.message = message; }
    public void setSuccess(boolean success) { this.success = success; }

    public boolean isSuccess() { return success; } 
}