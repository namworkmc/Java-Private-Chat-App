package vn.edu.hcmus.student.sv19127048.lab06.Client;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginView extends JFrame {

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public LoginView(DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        initComponents();
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }

    private void initComponents() {

        loginPanel = new JPanel();
        registerButton = new JButton();
        usernameLabel = new JLabel();
        usernameTextField = new JTextField();
        passwordLabel = new JLabel();
        loginButton = new JButton();
        passwordField = new JPasswordField();

        registerButton.setText("REGISTER");
        registerButton.setFont(new java.awt.Font("Segoe UI", Font.PLAIN, 10)); // NOI18N
        registerButton.addActionListener(evt -> {
            try {
                String username = usernameTextField.getText();
                String password = String.valueOf(passwordField.getPassword());

                dataOutputStream.writeUTF("register");
                dataOutputStream.writeUTF(username);
                dataOutputStream.writeUTF(password);
                dataOutputStream.flush();

                String res = dataInputStream.readUTF();
                JOptionPane.showMessageDialog(
                        null,
                        res,
                        "Response",
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (res.equals("Register Successful")) {
                    new ClientService(username, dataInputStream, dataOutputStream);
                    dispose();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12)); // NOI18N
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setText("Username");

        usernameTextField.setHorizontalAlignment(JTextField.CENTER);

        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12)); // NOI18N
        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordLabel.setText("Password");

        loginButton.setText("LOGIN");
        loginButton.addActionListener(evt -> {
            try {
                String username = usernameTextField.getText();
                String password = String.valueOf(passwordField.getPassword());

                dataOutputStream.writeUTF("login");
                dataOutputStream.writeUTF(username);
                dataOutputStream.writeUTF(password);
                dataOutputStream.flush();

                String res = dataInputStream.readUTF();
                JOptionPane.showMessageDialog(
                        null,
                        res,
                        "Response",
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (res.equals("Login Successful")) {
                    new ClientService(username, dataInputStream, dataOutputStream);
                    dispose();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        passwordField.setHorizontalAlignment(JTextField.CENTER);

        GroupLayout jPanel1Layout = new GroupLayout(loginPanel);
        loginPanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap(56, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(registerButton, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE))
                                                .addGap(137, 137, 137))
                                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(passwordLabel, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(passwordField))
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(usernameLabel, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(usernameTextField, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)))
                                                .addGap(50, 50, 50))))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(usernameLabel, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(usernameTextField, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(passwordLabel, GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                                        .addComponent(passwordField))
                                .addGap(26, 26, 26)
                                .addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(registerButton, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(24, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(loginPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(loginPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>                        

    public void render() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(LoginView.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        EventQueue.invokeLater(() -> setVisible(true));
    }

    // Variables declaration - do not modify                     
    private JPanel loginPanel;
    private JButton loginButton;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JButton registerButton;
    private JLabel usernameLabel;
    private JTextField usernameTextField;
    // End of variables declaration                   
}
