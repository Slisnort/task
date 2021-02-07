package task1;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

class ServerConnection extends Thread {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    int count = 0;

    public ServerConnection(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Server.story.printStory(out);
        start();
    }

    @Override
    public void run() {
        String word;
            try {
                while (true) {
                    word = in.readLine();
                    if(word.equals("stop")) {
                        this.downConnection();
                    } else if(word.equals("first")){
                        count = 1;
                        this.counter();
                    } else if(word.equals("second")){
                        count = 2;
                        this.counter();
                    } else if(word.equals("third")){
                        count = 3;
                        this.counter();
                    }
                    System.out.println("Echoing: " + word);
                    Server.story.addStoryMessage(word);
                    for (ServerConnection serverConnection : Server.connectionList) {
                        serverConnection.sendMessage(word);
                    }
                }
            } catch (NullPointerException | IOException ignored) {}
    }

    private void sendMessage(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException ignored) {}

    }

    private void counter(){
        if (count == 1){
            System.out.println("это 1!");
        } else  if (count == 2){
            System.out.println("это 2!");
        } else  if (count == 3){
            System.out.println("это 3!");
        }
    }

    private void downConnection() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ServerConnection vr : Server.connectionList) {
                    if(vr.equals(this)) vr.interrupt();
                    Server.connectionList.remove(this);
                }
            }
        } catch (IOException ignored) {}
    }
}

class Story {
    private LinkedList<String> story = new LinkedList<>();

    public void addStoryMessage(String storyMsg) {
        if (story.size() >= 10) {
            story.removeFirst();
            story.add(storyMsg);
        } else {
            story.add(storyMsg);
        }
    }

    public void printStory(BufferedWriter writer) {
        if(story.size() > 0) {
            try {
                writer.write("History messages" + "\n");
                for (String vr : story) {
                    writer.write(vr + "\n");
                }
                writer.write("...." + "\n");
                writer.flush();
            } catch (IOException ignored) {}

        }

    }
}

public class Server {
    public static final int PORT = 9876;
    public static LinkedList<ServerConnection> connectionList = new LinkedList<>();
    public static Story story;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        story = new Story();
        System.out.println("Server connected");
        try {
            while (true) {
                Socket socket = server.accept();
                try {
                    connectionList.add(new ServerConnection(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}