/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptchat;

/**
 *
 * @author Vuk
 */
public class ChatLog {
    private String name;
    private String log;

    public ChatLog(String name) {
        this.name = name;
        StringBuilder sb = new StringBuilder("Chat begun with "+name);
        sb.append(System.lineSeparator());
        sb.append("-----------------------");
        log = sb.toString();
    }
    
    public void append(String newMessage){
        StringBuilder sb = new StringBuilder(log);
        sb.append(System.lineSeparator());
        sb.append(newMessage);
        this.log = sb.toString();
    }

    public String getName() {
        return name;
    }

    public String getLog() {
        return log;
    }
    
}
