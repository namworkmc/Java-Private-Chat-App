package vn.edu.hcmus.student.sv19127048.lab06.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * vn.edu.hcmus.student.sv19127048.lab06.Server<br> Created by 19127048 - Nguyen Duc Nam<br> Date
 * 1/12/2022 - 8:25 PM<br> Description: JDK16<br>
 */
public class ServerService {

    final static int PORT = 9999;

    private Lock lock;
    private ServerSocket serverSocket;
    private Socket socket;
    final private String fileName = "accounts.txt";

    static HashMap<String, ClientHandlerThread> clients = new HashMap<>();

    public ServerService() throws IOException {
        this.lock = new ReentrantLock();
        this.serverSocket = new ServerSocket(PORT);
        loadAccounts();
    }

    public void start() throws IOException {
        while (true) {
            this.socket = serverSocket.accept();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String incomingMess = dataInputStream.readUTF();

            switch (incomingMess) {
                case "login" -> login(dataInputStream);
                case "register" -> register(dataInputStream);
            }
        }
    }

    /**
     * Login
     *
     * @param dataInputStream input stream
     * @throws IOException
     */
    private void login(DataInputStream dataInputStream) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        String username = dataInputStream.readUTF();
        String password = dataInputStream.readUTF();
        if (clients.containsKey(username)) {
            ClientHandlerThread client = clients.get(username);
            if (client.getPassword().equals(password)) {
                client.setSocket(socket);
                client.setLogin(true);

                dataOutputStream.writeUTF("Login Successful");
                dataOutputStream.flush();

                client.start();

                notifyOnlineUsers();
            }
        } else {
            dataOutputStream.writeUTF("Invalid username or password");
            dataOutputStream.flush();
        }
    }

    /**
     * Đăng ký tài khoản
     *
     * @param dataInputStream input stream
     * @throws IOException
     */
    private void register(DataInputStream dataInputStream) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        String username = dataInputStream.readUTF();
        String password = dataInputStream.readUTF();
        if (clients.containsKey(username)) {
            ClientHandlerThread clientHandlerThread = new ClientHandlerThread(username, password, true,
                    socket, lock);
            clients.put(username, clientHandlerThread);

            dataOutputStream.writeUTF("Register successful");
            dataOutputStream.flush();

            clientHandlerThread.start();

            notifyOnlineUsers();
        } else {
            dataOutputStream.writeUTF("Username already existed");
            dataOutputStream.flush();
        }
    }

    /**
     * Thông báo các tài khoản khác có tài khoản mới online
     */
    private void notifyOnlineUsers() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (ClientHandlerThread client : clients.values()) {
            if (client.getIsLogin()) {
                stringBuilder.append(client.getUsername());
                stringBuilder.append(";");
            }
        }
        for (ClientHandlerThread client : clients.values()) {
            if (client.getIsLogin()) {
                DataOutputStream dataOutputStream = client.getDataOutputStream();
                dataOutputStream.writeUTF("online-users");
                dataOutputStream.writeUTF(stringBuilder.toString());
                dataOutputStream.flush();
            }
        }
    }

    /**
     * Save danh sách account
     */
    private void saveAccounts() throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        for (ClientHandlerThread client : clients.values()) {
            fileWriter.write(String.format("%s - %s\n", client.getUsername(), client.getPassword()));
        }
    }

    /**
     * Load danh sách account
     *
     * @throws IOException
     */
    private void loadAccounts() throws IOException {
        if (Files.notExists(Path.of(fileName))) {
            Files.createFile(Path.of(fileName));
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] account = line.split(" - ");

            String username = account[0];
            String password = account[1];
            ClientHandlerThread client = new ClientHandlerThread(username, password, false, lock);
            clients.put(username, client);
        }

        bufferedReader.close();
    }

    /**
     * Thread xử lí các yêu cầu từ phía client
     */
    class ClientHandlerThread extends Thread {

        private final Lock lock;
        private Socket socket;
        private DataInputStream dataInputStream;

        private DataOutputStream dataOutputStream;

        final private String username;
        final private String password;
        private Boolean isLogin;

        /**
         * Tạo thread cho tài khoản vừa đăng ký rồi đăng nhập liền
         *
         * @param username tên đăng nhập
         * @param password password
         * @param isLogin  kiểm tra xem đã login chưa
         * @param socket   socket
         * @param lock     khoá synchronize
         */
        ClientHandlerThread(String username, String password, Boolean isLogin, Socket socket, Lock lock)
                throws IOException {
            this.username = username;
            this.password = password;
            this.isLogin = isLogin;
            this.socket = socket;
            this.lock = lock;

            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());
        }

        /**
         * Tạo thread cho tài khoản chưa đăng nhập
         *
         * @param username tên đăng nhập
         * @param password password
         * @param isLogin  kiểm tra xem đã login chưa
         * @param lock     khoá synchronize
         */
        ClientHandlerThread(String username, String password, Boolean isLogin, Lock lock) {
            this.username = username;
            this.password = password;
            this.isLogin = isLogin;
            this.lock = lock;
        }

        public DataInputStream getDataInputStream() {
            return dataInputStream;
        }

        public DataOutputStream getDataOutputStream() {
            return dataOutputStream;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public Boolean getIsLogin() {
            return isLogin;
        }

        public void setSocket(Socket socket) throws IOException {
            this.socket = socket;

            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());
        }

        public void setLogin(Boolean isLogin) {
            this.isLogin = isLogin;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String messageFromClient = dataInputStream.readUTF();

                    if (messageFromClient.equals("logout")) {
                        dataOutputStream.writeUTF("logout");
                        dataOutputStream.flush();
                        socket.close();

                        isLogin = false;
                        notifyOnlineUsers();

                        break;
                    }
                    switch (messageFromClient) {
                        case "send-text" -> {
                            String receiver = dataInputStream.readUTF();
                            String content = dataInputStream.readUTF();

                            try {
                                lock.lock();
                                ClientHandlerThread client = clients.get(receiver);
                                client.dataOutputStream.writeUTF("send-text");
                                client.dataOutputStream.writeUTF(username);
                                client.dataOutputStream.writeUTF(content);
                                client.dataOutputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                lock.unlock();
                            }
                        }
                        case "send-file" -> {
                            int bytes;
                            byte[] buffer = new byte[4 * 1024];

                            String receiver = dataInputStream.readUTF();
                            String fileName = dataInputStream.readUTF();
                            long fileSize = dataInputStream.readLong();

                            ClientHandlerThread client = clients.get(receiver);
                            DataOutputStream clientOutputStream = client.getDataOutputStream();
                            synchronized (lock) {
                                clientOutputStream.writeUTF("send-file");
                                clientOutputStream.writeUTF(username);
                                clientOutputStream.writeUTF(fileName);
                                clientOutputStream.writeLong(fileSize);
                                while ((bytes = dataInputStream.read(buffer)) != -1) {
                                    System.out.println(bytes);
                                    clientOutputStream.write(buffer, 0, bytes);
                                }
                                clientOutputStream.flush();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}