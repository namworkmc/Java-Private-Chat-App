package vn.edu.hcmus.student.sv19127048.lab06.Client;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientView extends JFrame {

    /**
     * Creates new form ChatGUI
     */
    public ClientView(String username, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.username = username;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        chatHistoryListModel = new DefaultListModel<>();
        onlineListModel = new DefaultListModel<>();

        // Variables declaration - do not modify
        JPanel chatBoxPanel = new JPanel();
        inputTextField = new JTextField();
        submitBtn = new JButton();
        JScrollPane sp = new JScrollPane();
        chatHistoryList = new JList<>(chatHistoryListModel);
        JLabel receiverUser = new JLabel();
        JScrollPane onlineSP = new JScrollPane();
        onlineList = new JList<>();
        JLabel onlineUsersLabel = new JLabel();
        sendFileBtn = new JButton();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    dataOutputStream.writeUTF("logout");
                    dataOutputStream.flush();

                    String res = dataInputStream.readUTF();
                    System.out.println(res);
                    if (res.equals("logging out")) {
                        System.exit(0);
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        inputTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        inputTextField.setHorizontalAlignment(JTextField.CENTER);
        inputTextField.getDocument().addDocumentListener(new DocumentListener() {
            private void enableSubmitBtn() {
                submitBtn.setEnabled(!inputTextField.getText().isEmpty());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                enableSubmitBtn();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                enableSubmitBtn();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableSubmitBtn();
            }
        });

        submitBtn.setText("SEND");
        submitBtn.setEnabled(false);
//        submitBtn.addActionListener(this::submitBtnActionPerformed);

        chatHistoryList.setFont(new Font("Segoe UI", 0, 18)); // NOI18N
        chatHistoryList.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        sp.setViewportView(chatHistoryList);

        receiverUser.setFont(new Font("Segoe UI", 0, 24)); // NOI18N
        receiverUser.setHorizontalAlignment(SwingConstants.CENTER);
        receiverUser.setText(username);

        onlineList.setFont(new Font("Segoe UI", 0, 14)); // NOI18N
        onlineList.addListSelectionListener(e -> {

        });
        onlineSP.setViewportView(onlineList);

        onlineUsersLabel.setFont(new Font("Segoe UI", 0, 18)); // NOI18N
        onlineUsersLabel.setHorizontalAlignment(SwingConstants.CENTER);
        onlineUsersLabel.setText("Online Users");

        sendFileBtn.setText("File");

        GroupLayout chatBoxPanelLayout = new GroupLayout(chatBoxPanel);
        chatBoxPanel.setLayout(chatBoxPanelLayout);
        chatBoxPanelLayout.setHorizontalGroup(
                chatBoxPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(chatBoxPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(chatBoxPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(sp)
                                        .addComponent(receiverUser, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(chatBoxPanelLayout.createSequentialGroup()
                                                .addComponent(inputTextField, GroupLayout.PREFERRED_SIZE, 352, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addGroup(chatBoxPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(submitBtn, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                                                        .addComponent(sendFileBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(18, 18, 18)
                                .addGroup(chatBoxPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(onlineSP)
                                        .addComponent(onlineUsersLabel, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
                                .addContainerGap())
        );
        chatBoxPanelLayout.setVerticalGroup(
                chatBoxPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(chatBoxPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(chatBoxPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(receiverUser, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                                        .addComponent(onlineUsersLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(chatBoxPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(chatBoxPanelLayout.createSequentialGroup()
                                                .addComponent(sp, GroupLayout.PREFERRED_SIZE, 329, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(chatBoxPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(inputTextField)
                                                        .addComponent(submitBtn, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sendFileBtn, GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
                                        .addComponent(onlineSP))
                                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(chatBoxPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(chatBoxPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    private void submitBtnActionPerformed(ActionEvent evt) {
        String text = inputTextField.getText();
        try {
            dataOutputStream.writeUTF("send-text");
            dataOutputStream.writeUTF(onlineList.getSelectedValue());
            dataOutputStream.writeUTF(text);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        chatHistoryListModel.addElement(text);
        chatHistoryList.setModel(chatHistoryListModel);

        inputTextField.setText("");
    }

    public void render() {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        EventQueue.invokeLater(() -> setVisible(true));
    }

    private String username;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private JList<String> chatHistoryList;
    private JTextField inputTextField;
    private JList<String> onlineList;

    private JButton sendFileBtn;
    private JButton submitBtn;

    private DefaultListModel<String> chatHistoryListModel;
    private DefaultListModel<String> onlineListModel;

    public JList<String> getOnlineList() {
        return onlineList;
    }

    public JList<String> getChatHistoryList() {
        return chatHistoryList;
    }

    public DefaultListModel<String> getChatHistoryListModel() {
        return chatHistoryListModel;
    }

    public JTextField getInputTextField() {
        return inputTextField;
    }

    public JButton getSendFileBtn() {
        return sendFileBtn;
    }

    public JButton getSubmitBtn() {
        return submitBtn;
    }

    public DefaultListModel<String> getOnlineListModel() {
        return onlineListModel;
    }
// End of variables declaration
}
