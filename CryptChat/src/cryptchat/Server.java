/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptchat;

import java.io.*;
import java.util.*;
import java.net.*;

public class Server {

    // Vektor za cuvanje aktivnih klijenata
    static Vector<ClientHandler> ar = new Vector<>();

    // Brojac za klijente
    static int i = 0;

    public static void main(String[] args) throws IOException {
        System.out.println("Server starting...");
        final int serverPort = 8080;
        ServerSocket ss = new ServerSocket(serverPort);
        System.out.println("Server socket established at port: " + serverPort);
        Socket s;
        String clientName;
        String publicKey;
        SymmetricC symmetric = new SymmetricC();
        symmetric.createNewKey();
        AsymmetricC asymmetric = new AsymmetricC();

        while (true) {
            System.out.println("Listening...");
            s = ss.accept();
            System.out.println("New client request recieved: " + s);

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            // getting public key bytes
            publicKey = dis.readUTF();
            
            // Send symmetric key for client handler
            String key = symmetric.getKeyHex(); // symmetric server - client key
            dos.writeUTF(asymmetric.encryptMessage(key, publicKey)); // encrypting and sending symmetric key
            System.out.println("Symetric key: "+symmetric.getKeyHex());
            
            // Getting name from client
            clientName = dis.readUTF();
            
            

            System.out.println("Creating a new handler for " + clientName + "...");
            ClientHandler mtch = new ClientHandler(s, clientName, key, publicKey, dis, dos);
            Thread t = new Thread(mtch);

            System.out.println("Adding " + clientName + " to active client list");
            ar.add(mtch);
            t.start();

            System.out.println("Current clients:");
            for (ClientHandler ch : ar) {
                System.out.println(ch.name);
            }

            i++;
        }
    }
}

class ClientHandler implements Runnable {

    Scanner scn = new Scanner(System.in);
    String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;
    boolean stopHandler=false;
    String publicKey;
    String key; // symmetric key

    public ClientHandler(Socket s, String name, String key, String publicKey, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.publicKey = publicKey;
        this.key = key;
        this.isloggedin = true;
    }

    @Override
    public void run() {
        String received;
        String receivedCipher;
        SymmetricC symmetric = new SymmetricC(key);
        while (!stopHandler) {
            try {
                receivedCipher = dis.readUTF();
                received = symmetric.decryptMessage(receivedCipher);
                System.out.println(this.name + ":" + received);

                // message # recipient
                StringTokenizer st = new StringTokenizer(received, "#");
                String type = st.nextToken();

                switch (type) {
                    // -m Message
                    case "-m":
                        String recipient = st.nextToken();
                        String msgToSend = st.nextToken();

                        for (ClientHandler mc : Server.ar) {
                            if (mc.name.equals(recipient) && mc.isloggedin == true) {
                                String send = "-m#" + this.name + "#" + msgToSend;
                                mc.dos.writeUTF(symmetric.encryptMessage(send));
                                break;
                            }
                        }
                        break;
                    // -rl Refresh list
                    case "-rl":
                        String otherConnectedClients = "";
                        int arSize = Server.ar.size()-1;
                        for (ClientHandler mc : Server.ar) {
                            if (mc.name != this.name) {
                                otherConnectedClients = otherConnectedClients + mc.name + "#" + mc.publicKey + "#";
                            }
                        }
                        if (otherConnectedClients.equals("")) {
                            
                            otherConnectedClients = "-No active users-";
                        }
                        String send = "-rl#" + arSize + "#" + otherConnectedClients;
                        dos.writeUTF(symmetric.encryptMessage(send));
                        break;
                    case "-dc":
                        this.isloggedin = false;
                        for (int i = 0; i < Server.ar.size(); i++) {
                            if (Server.ar.get(i).name.equals(this.name)) {
                                Server.ar.remove(i);
                            }
                        }
                        try {
                            this.dis.close();
                            this.dos.close();
                            this.s.close();
                            this.stopHandler=true;
                            System.out.println(this.name+" has disconnected");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        System.out.println("Unrecongniezed message type!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
