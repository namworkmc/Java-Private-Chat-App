package vn.edu.hcmus.student.sv19127048.lab06.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 9999);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            new LoginView(dataInputStream, dataOutputStream).render();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
