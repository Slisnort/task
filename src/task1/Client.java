package task1;

import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

class ClientConnection {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader fromConsole;
    private String address;
    private int port;
    private Date time;
    private String dtime;
    private SimpleDateFormat dt;

    public ClientConnection(String address, int port) {
        this.address = address;
        this.port = port;
        try {
            this.socket = new Socket(address, port);
        } catch (IOException e) {
            System.err.println("Socket failed");
        }
        try {
            fromConsole = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new ReadMessage().start();
            new WriteMessage().start();
        } catch (IOException e) {
            ClientConnection.this.downConnection();
        }
    }

    private void counter(){
        System.out.println("s");
    }

    private void downConnection() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }

    private class ReadMessage extends Thread {
        @Override
        public void run() {
            String str;
            try {
                while (true) {
                    str = in.readLine();
                    if (str.equals("stop")) {
                        ClientConnection.this.downConnection();
                        break;
                    }
                    System.out.println(str);
                }
            } catch (IOException e) {
                ClientConnection.this.downConnection();
            }
        }
    }

    public class WriteMessage extends Thread {
        @Override
        public void run() {
            while (true) {
                String userWord;
                try {
                    time = new Date();
                    dt = new SimpleDateFormat("HH:mm:ss");
                    dtime = dt.format(time);
                    userWord = fromConsole.readLine();
                    if (userWord.equals("stop")) {
                        out.write("stop" + "\n");
                        ClientConnection.this.downConnection();
                        break;
                    } else if (userWord.equals("first")) {
                        out.write("first" + "\n");
                        ClientConnection.this.counter();
                    } else if (userWord.equals("second")) {
                        out.write("second" + "\n");
                        ClientConnection.this.counter();
                    }else if (userWord.equals("third")) {
                        out.write("third" + "\n");
                        ClientConnection.this.counter();
                    } else {
                        out.write("(" + dtime + ") " + ": " + userWord + "\n");
                    }
                    out.flush();
                } catch (IOException e) {
                    ClientConnection.this.downConnection();
                }
            }
        }
    }
}

public class Client {
    public static String ipAddress = "localhost";
    public static int port = 9876;

    public static void main(String[] args) {
        new ClientConnection(ipAddress, port);
    }
}


