package vn.edu.hcmus.student.sv19127048.lab06.Server;

import java.io.IOException;

/**
 * vn.edu.hcmus.student.sv19127048.lab06.Server<br>
 * Created by 19127048 - Nguyen Duc Nam<br>
 * Date 1/12/2022 - 11:09 PM<br>
 * Description: JDK16<br>
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServerService server = new ServerService();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
