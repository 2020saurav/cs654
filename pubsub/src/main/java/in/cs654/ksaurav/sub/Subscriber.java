package in.cs654.ksaurav.sub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Subscriber {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 2020);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println("LOGIN 2020saurav@gmail.com");
        writer.flush();
        while (true) {
            System.out.println(reader.readLine());
        }
    }
}
