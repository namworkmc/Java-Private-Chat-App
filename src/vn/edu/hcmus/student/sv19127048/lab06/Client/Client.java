package vn.edu.hcmus.student.sv19127048.lab06.Client;

import java.io.IOException;

public class Client {
    public static void main(String[] args) {
        try {
            new LoginView().render();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
