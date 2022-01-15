package vn.edu.hcmus.student.sv19127048.lab06.Client;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * vn.edu.hcmus.student.sv19127048.lab06.Client<br> Created by 19127048 - Nguyen Duc Nam<br> Date
 * 1/13/2022 - 9:38 PM<br> Description: JDK16<br>
 */
public class ClientService {

    final private ClientView clientView;

    final private DataInputStream dataInputStream;
    final private DataOutputStream dataOutputStream;

    final private String username;
    final private HashMap<String, DefaultListModel<String>> privateChatModel;

    public ClientService(String username, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;

        this.username = username;
        this.privateChatModel = new HashMap<>();

        this.clientView = new ClientView(username, dataInputStream, dataOutputStream);
        clientView.render();
        clientView.getOnlineList().addListSelectionListener(evt -> {
            String selectedValue = clientView.getOnlineList().getSelectedValue();
            if (privateChatModel.containsKey(selectedValue)) {
                clientView.getChatHistoryList().setModel(privateChatModel.get(selectedValue));
            } else {
                clientView.getChatHistoryList().setModel(new DefaultListModel<>());
            }
        });

        new Receiver(dataInputStream).start();
    }

    private class Receiver extends Thread {

        private final DataInputStream dataInputStream;

        public Receiver(DataInputStream dataInputStream) {
            this.dataInputStream = dataInputStream;

            clientView.getSubmitBtn().addActionListener(evt -> {
                String content = clientView.getInputTextField().getText();
                String selectedUsername = clientView.getOnlineList().getSelectedValue();
                try {
                    dataOutputStream.writeUTF("send-text");
                    dataOutputStream.writeUTF(selectedUsername);
                    dataOutputStream.writeUTF(content);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                DefaultListModel<String> model;
                if (privateChatModel.containsKey(selectedUsername)) {
                    model = privateChatModel.get(selectedUsername);
                } else {
                    model = new DefaultListModel<>();
                    privateChatModel.put(selectedUsername, model);
                }
                model.addElement(String.format("%s: %s", username, content));
                clientView.getChatHistoryList().setModel(model);

                privateChatModel.put(selectedUsername, model);

                clientView.getInputTextField().setText("");
            });
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String incomingMess = dataInputStream.readUTF();
                    switch (incomingMess) {
                        case "online-users" -> {
                            String[] onlineUsers = dataInputStream.readUTF().split(";");
                            DefaultListModel<String> onlineListModel = new DefaultListModel<>();

                            Arrays.stream(onlineUsers).forEach(elt -> {
                                if (!elt.equals(username)) {
                                    onlineListModel.addElement(elt);
                                }
                            });

                            clientView.getOnlineList().setModel(onlineListModel);
                        }
                        case "send-text" -> {
                            String username = dataInputStream.readUTF();
                            String content = dataInputStream.readUTF();

                            DefaultListModel<String> model;
                            if (privateChatModel.containsKey(username)) {
                                model = privateChatModel.get(username);
                            } else {
                                model = new DefaultListModel<>();
                                privateChatModel.put(username, model);
                            }
                            model.addElement(String.format("%s: %s", username, content));
                            clientView.getChatHistoryList().setModel(model);

                            privateChatModel.put(username, model);
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public HashMap<String, DefaultListModel<String>> getPrivateChatModel() {
        return privateChatModel;
    }
}
