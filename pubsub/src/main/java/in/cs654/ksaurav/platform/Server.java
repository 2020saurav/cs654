package in.cs654.ksaurav.platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    public static final Integer PORT = 2020;
    public static final ConcurrentHashMap<String, Broker> brokerHashMap = new ConcurrentHashMap<>();
    public static void main(String[] args) throws IOException {
        System.out.println("Pub-Sub server running on " + PORT.toString());
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                new Broker(listener.accept()).start();
            }
        }
    }
}
