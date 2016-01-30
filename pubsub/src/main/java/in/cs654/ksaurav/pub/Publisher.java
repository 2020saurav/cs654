package in.cs654.ksaurav.pub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Publisher {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 2020);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.print("PUBLISH P1 T42 Yoo Bro, what's up?");
        writer.flush();
    }
}
