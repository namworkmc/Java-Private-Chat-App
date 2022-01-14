package vn.edu.hcmus.student.sv19127048.lab06.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * vn.edu.hcmus.student.sv19127048.lab06.Client<br> Created by 19127048 - Nguyen Duc Nam<br> Date
 * 1/13/2022 - 9:38 PM<br> Description: JDK16<br>
 */
public class ClientService {

  public static void main(String[] args) {
    try {
      Socket s1 = new Socket("localhost", 9999);
      Socket s2 = new Socket("localhost", 9999);

      DataInputStream dis1 = new DataInputStream(s1.getInputStream());
      DataOutputStream dos1 = new DataOutputStream(s1.getOutputStream());
      dos1.writeUTF("login");
      dos1.writeUTF("nam - 123");
      System.out.println(dis1.readUTF());

      DataInputStream dis2 = new DataInputStream(s2.getInputStream());
      DataOutputStream dos2 = new DataOutputStream(s2.getOutputStream());
      dos2.writeUTF("login");
      dos2.writeUTF("minh - 123");
      System.out.println(dis2.readUTF());
      
      dos1.writeUTF("send-text");
      dos1.writeUTF("hello world");

      System.out.println(dis1.readUTF());
      System.out.println(dis2.readUTF());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private class Receiver extends Thread {

    private DataInputStream dataInputStream;

    public Receiver(DataInputStream dataInputStream) {
      this.dataInputStream = dataInputStream;
    }

    @Override
    public void run() {
      try {
        while (true) {
          String incomingMess = dataInputStream.readUTF();

          switch (incomingMess) {
            case "send-text" -> {
              String[] incomingData = dataInputStream.readUTF().split(" - ");
              String username = incomingData[0];
              String content = incomingData[1];

              System.out.printf("%s: %s:\n", username, content);
            }
          }

        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
