package vn.edu.hcmus.student.sv19127048.lab06.Client;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;

import static javax.swing.JFileChooser.APPROVE_OPTION;

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

        Thread receiver = new Receiver(dataInputStream);
        receiver.start();

        clientView.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        clientView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    dataOutputStream.writeUTF("logout");
                    dataOutputStream.flush();

                    try {
                        receiver.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    if (dataInputStream != null) {
                        dataInputStream.close();
                    }
                    if (dataOutputStream != null) {
                        dataOutputStream.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Thread class xử lí tin nhắn và file gửi đi
     */
    private class Receiver extends Thread {

        private final DataInputStream dataInputStream;

        public Receiver(DataInputStream dataInputStream) {
            this.dataInputStream = dataInputStream;

            // Xử lí gửi text
            clientView.getSubmitBtn().addActionListener(evt -> {
                String content = clientView.getInputTextField().getText();
                String selectedUsername = clientView.getOnlineList().getSelectedValue();
                if (selectedUsername == null) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Please choose receiver",
                            "Empty receiver",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
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
                    }
                    model.addElement(String.format("%s: %s", username, content));
                    clientView.getChatHistoryList().setModel(model);

                    privateChatModel.put(selectedUsername, model);

                    clientView.getInputTextField().setText("");
                }
            });

            // Xử lí gửi file
            clientView.getSendFileBtn().addActionListener(evt -> {
                String selectedUsername = clientView.getOnlineList().getSelectedValue();
                if (selectedUsername == null) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Choose receiver",
                            "Empty receiver",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JFileChooser fileChooser = new JFileChooser();
                    int selectedOption = fileChooser.showOpenDialog(clientView.getParent());
                    switch (selectedOption) {
                        case APPROVE_OPTION -> {
                            File selectedFile = fileChooser.getSelectedFile();

                            int fileSize = (int) selectedFile.length();
                            byte[] buffer = new byte[fileSize];
                            BufferedInputStream bufferedInputStream;
                            try {
                                bufferedInputStream = new BufferedInputStream(new FileInputStream(selectedFile));
                                // Load file vào buffer
                                bufferedInputStream.read(buffer, 0, fileSize);

                                // Thông tin người gửi và thông tin file
                                dataOutputStream.writeUTF("send-file");
                                dataOutputStream.writeUTF(selectedUsername);
                                dataOutputStream.writeUTF(selectedFile.getName());
                                dataOutputStream.writeLong(buffer.length);

                                // Gửi file chunk
                                int offset = 0;
                                int bufferSize = 4 * 1024; // mỗi chunk dài 4KB
                                // Chia file ra thành từng mảnh 4KB gửi theo phương pháp chunk
                                while (fileSize > 0) {
                                    dataOutputStream.write(buffer, offset, Math.min(fileSize, bufferSize));
                                    offset += Math.min(fileSize, bufferSize);
                                    fileSize -= bufferSize;
                                }
                                dataOutputStream.flush();
                                bufferedInputStream.close();

                                DefaultListModel<String> model;
                                if (privateChatModel.containsKey(selectedUsername)) {
                                    model = privateChatModel.get(selectedUsername);
                                } else {
                                    model = new DefaultListModel<>();
                                }
                                model.addElement(String.format("%s: %s", username, selectedFile.getAbsolutePath()));
                                clientView.getChatHistoryList().setModel(model);

                                privateChatModel.put(selectedUsername, model);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String incomingMess = dataInputStream.readUTF();

                    // Logout
                    if (incomingMess.equals("logout")) {
                        break;
                    }
                    switch (incomingMess) {
                        // Render danh sách tài khoản online
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
                        // Ouput stream gửi text
                        case "send-text" -> {
                            String username = dataInputStream.readUTF();
                            String content = dataInputStream.readUTF();

                            DefaultListModel<String> model;
                            if (privateChatModel.containsKey(username)) {
                                model = privateChatModel.get(username);
                            } else {
                                model = new DefaultListModel<>();
                            }
                            model.addElement(String.format("%s: %s", username, content));

                            privateChatModel.put(username, model);
                        }
                        // Output stream gửi file
                        case "send-file" -> {
                            String username = dataInputStream.readUTF();
                            String fileName = dataInputStream.readUTF();

                            int bytes;
                            long size = dataInputStream.readLong();
                            byte[] buffer = new byte[4 * 1024]; // Mỗi chunk dài 4KB

                            if (Files.notExists(Path.of("storage"))) {
                                Files.createDirectory(Path.of("storage"));
                            }
                            String fileDirectory = String.format("storage/%s", fileName);
                            if (Files.exists(Path.of(fileDirectory))) {
                                Files.delete(Path.of(fileDirectory));
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream(fileDirectory);
                            File file = new File(fileDirectory);
                            // Ghi file từ stream xuống folder theo chunk
                            while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                                fileOutputStream.write(buffer, 0, bytes);
                                size -= bytes;
                            }
                            fileOutputStream.flush();
                            fileOutputStream.close();

                            DefaultListModel<String> model;
                            if (privateChatModel.containsKey(username)) {
                                model = privateChatModel.get(username);
                            } else {
                                model = new DefaultListModel<>();
                            }
                            model.addElement(String.format("%s: %s", username, file.getAbsolutePath()));
                            privateChatModel.put(username, model);
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}