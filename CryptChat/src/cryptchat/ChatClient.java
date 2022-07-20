/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptchat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Vuk
 */
public class ChatClient extends javax.swing.JFrame {

    final static int ServerPort = 8080;
    DataInputStream in = null;
    DataOutputStream out = null;
    String clientName;
    boolean stop = true;
    Socket client = null;
    List<ChatLog> logs;
    List<String[]> keys;
    static AsymmetricC asymmetric;
    static SymmetricC symmetric;

    public ChatClient() {
        initComponents();
        this.setTitle("Klijent Chat");
        logs = new ArrayList<>();
        keys = new ArrayList<>();

        // Iskljucujemo dijelove jFrame-a
        refreshUserList_B.setEnabled(false);
        send_B.setEnabled(false);
        send_TF.setEnabled(false);
        disconnect_B.setEnabled(false);
        userList_L.setEnabled(false);
        inbox_TA.setEnabled(false);
    }

    class KSocket implements Runnable {

        int port;
        JTextArea jta;
        JTextField jtf;
        String serverName = "localhost";

        public KSocket(int port, JTextArea jta, JTextField jtf) {
            this.port = port;
            this.jta = jta;
            this.jtf = jtf;
            asymmetric = new AsymmetricC();
            asymmetric.createNewKeys();
        }

        @Override
        public void run() {
            try {
                jta.setText("Connection on " + serverName + " on port " + port + "\n");
                client = new Socket(serverName, port);
                jta.append("Connection established: " + client.getRemoteSocketAddress() + "\n");
                in = new DataInputStream(client.getInputStream());
                out = new DataOutputStream(client.getOutputStream());

                // Server PRVO prihvata public key length i sam kljuc
                out.writeUTF(asymmetric.getPublicKeyHex());

                // Server DRUGO salje symmetric key
                symmetric = new SymmetricC(asymmetric.decryptMessage(in.readUTF()));
                System.out.println("Symmetric key: " + symmetric.getKeyHex());

                // Server TRECE prihvata ime klijenta pa saljemo
                out.writeUTF(clientName);

                // Send a request for user list
                out.writeUTF(symmetric.encryptMessage("-rl#"));

                while (!stop) {
                    String receivedCipher = in.readUTF();
                    System.out.println("Recieved coded: " + receivedCipher);
                    // First we decrypt
                    String received = symmetric.decryptMessage(receivedCipher);
                    System.out.println("Received decoded: " + received);

                    StringTokenizer st = new StringTokenizer(received, "#");
                    String type = st.nextToken();
                    boolean found;
                    switch (type) {
                        // -m Message
                        case "-m":
                            // Received looks like: -m#sender#message
                            String sender = st.nextToken();
                            String messageCipher = st.nextToken();
                            String message = asymmetric.decryptMessage(messageCipher);
                            found = false;
                            // Provjerava da li imamo korisnika u listi aktivnih partnera "logs" i appendamo njegovu poruku
                            for (ChatLog log : logs) {
                                if (log.getName().equals(sender)) {
                                    found = true;
                                    log.append(sender + ": " + message);
                                    if (userList_L.getSelectedValue() != null) {
                                        if (userList_L.getSelectedValue().equals(log.getName())) {
                                            inbox_TA.setText(log.getLog());
                                        }
                                    }
                                }
                            }
                            if (!found) {
                                ChatLog cl = new ChatLog(sender);
                                cl.append(sender + ":" + message);
                                if (userList_L.getSelectedValue() != null) {
                                    if (userList_L.getSelectedValue().equals(sender)) {
                                        inbox_TA.setText(cl.getLog());
                                    }
                                }
                                logs.add(cl);
                            }
                            break;

                        // -rl Refresh list
                        case "-rl":
                            int listSize = Integer.parseInt(st.nextToken());
                            String[] userListArray;
                            if (listSize != 0) {
                                userListArray = new String[listSize];
                            } else {
                                userListArray = new String[listSize + 1];
                            }
                            String nextUser;
                            // Dodajemo javne kljuceve u listu
                            String nextKey;
                            for (int i = 0; i < userListArray.length; i++) {
                                nextUser = st.nextToken();
                                userListArray[i] = nextUser;
                                if (listSize != 0) {
                                    nextKey = st.nextToken();

                                    found = false;

                                    for (String[] profile : keys) {
                                        if (profile[0].equals(nextUser)) {
                                            found = true;
                                        }
                                    }
                                    if (!found) {
                                        String[] keyProfile = new String[2];
                                        keyProfile[0] = nextUser;
                                        keyProfile[1] = nextKey;
                                        keys.add(keyProfile);
                                    }
                                }
                            }
                            userList_L.setListData(userListArray);

                            break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        inbox_TA = new javax.swing.JTextArea();
        send_TF = new javax.swing.JTextField();
        send_B = new javax.swing.JButton();
        connect_B = new javax.swing.JButton();
        name_TF = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        userList_L = new javax.swing.JList<>();
        jLabel3 = new javax.swing.JLabel();
        refreshUserList_B = new javax.swing.JButton();
        notifications_LB = new javax.swing.JLabel();
        disconnect_B = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        inbox_TA.setEditable(false);
        inbox_TA.setColumns(20);
        inbox_TA.setRows(5);
        jScrollPane1.setViewportView(inbox_TA);

        send_B.setText("Send");
        send_B.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send_BActionPerformed(evt);
            }
        });

        connect_B.setText("Connect");
        connect_B.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connect_BActionPerformed(evt);
            }
        });

        jLabel2.setText("Enter name:");

        userList_L.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                userList_LValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(userList_L);

        jLabel3.setText("Current users:");

        refreshUserList_B.setText("Refresh");
        refreshUserList_B.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshUserList_BActionPerformed(evt);
            }
        });

        disconnect_B.setText("Disconnect");
        disconnect_B.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnect_BActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(send_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(298, 298, 298)
                                .addComponent(connect_B))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(163, 163, 163)
                                .addComponent(jLabel2)
                                .addGap(31, 31, 31)
                                .addComponent(name_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(send_B)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(refreshUserList_B))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(notifications_LB, javax.swing.GroupLayout.PREFERRED_SIZE, 613, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(disconnect_B)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(name_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(connect_B)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(refreshUserList_B)))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(send_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(send_B))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                        .addComponent(notifications_LB, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(disconnect_B)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void send_BActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_send_BActionPerformed
        String recipient = userList_L.getSelectedValue();
        String message = send_TF.getText();
        String messageCipher;
        try {
            for (String[] profile : keys) {
                if (profile[0].equals(recipient)) {
                    messageCipher = asymmetric.encryptMessage(message, profile[1]);
                    String send = "-m#" + recipient + "#" + messageCipher;
                    this.out.writeUTF(symmetric.encryptMessage(send));//m for message
                }
            }

            this.send_TF.setText("");
            System.out.println("Message sent to " + recipient);
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        boolean found = false;
        for (ChatLog log : this.logs) {
            if (log.getName().equals(recipient)) {
                found = true;
                log.append(clientName + ": " + message);
                if (userList_L.getSelectedValue().equals(log.getName())) {
                    inbox_TA.setText(log.getLog());
                }
            }
        }
        if (!found) {
            ChatLog cl = new ChatLog(recipient);
            cl.append(clientName + ": " + message);
            if (userList_L.getSelectedValue().equals(cl.getName())) {
                inbox_TA.setText(cl.getLog());
            }
            this.logs.add(cl);
        }
    }//GEN-LAST:event_send_BActionPerformed

    private void connect_BActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connect_BActionPerformed
        this.clientName = name_TF.getText();
        if (clientName.length() > 1 && !clientName.contains("#")) {
            Thread connection = new Thread(new KSocket(ServerPort, inbox_TA, send_TF));
            this.stop = false;
            connection.start();
            System.out.println("Connection establsihed with server");

            refreshUserList_B.setEnabled(true);
            send_B.setEnabled(true);
            send_TF.setEnabled(true);
            disconnect_B.setEnabled(true);
            userList_L.setEnabled(true);
            inbox_TA.setEnabled(true);

            name_TF.setEditable(false);
            connect_B.setEnabled(false);
        } else {
            notifications_LB.setText("You have not entered the name correctly");
        }

    }//GEN-LAST:event_connect_BActionPerformed

    private void refreshUserList_BActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshUserList_BActionPerformed
        try {
            out.writeUTF(symmetric.encryptMessage("-rl#"));
            System.out.println("User list refreshed");
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_refreshUserList_BActionPerformed

    private void disconnect_BActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnect_BActionPerformed
        try {
            out.writeUTF(symmetric.encryptMessage("-dc#"));
            this.stop = true;
            in.close();
            out.close();
            client.close();
            this.notifications_LB.setText("Disconnected!");
            System.out.println("Disconnected from server");
            
            
            refreshUserList_B.setEnabled(false);
            send_B.setEnabled(false);
            send_TF.setEnabled(false);
            disconnect_B.setEnabled(false);
            userList_L.setEnabled(false);
            inbox_TA.setEnabled(false);
            name_TF.setEditable(true);
            connect_B.setEnabled(true);
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_disconnect_BActionPerformed

    private void userList_LValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_userList_LValueChanged
        boolean found = false;
        if (userList_L.getSelectedValue().equals("-No active users-")) {
            found = true;
        }
        for (ChatLog log : this.logs) {
            if (log.getName().equals(userList_L.getSelectedValue())) {
                found = true;
                inbox_TA.setText(log.getLog());
                break;
            }
        }
        if (!found) {
            inbox_TA.setText("No active chat with " + userList_L.getSelectedValue());
        }
    }//GEN-LAST:event_userList_LValueChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChatClient().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connect_B;
    private javax.swing.JButton disconnect_B;
    private javax.swing.JTextArea inbox_TA;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField name_TF;
    private javax.swing.JLabel notifications_LB;
    private javax.swing.JButton refreshUserList_B;
    private javax.swing.JButton send_B;
    private javax.swing.JTextField send_TF;
    private javax.swing.JList<String> userList_L;
    // End of variables declaration//GEN-END:variables
}
