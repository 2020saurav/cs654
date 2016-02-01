package in.cs654.ksaurav.sub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Subscriber {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 2020);
        BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        String email = stdin.readLine();
        writer.println("LOGIN " + email);
        writer.flush();
        (new Thread() {
            public void run() {
                try {
                    String msg;
                    while((msg=socketReader.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.out.println("Sorry, error printing message");
                } finally {
                    System.out.println("Good Bye!");
                }
            }
        }).start();
        (new Thread() {
            public void run() {
                try {
                    String input;
                    while ((input=stdin.readLine()) != null) {
                        writer.println(input);
                        writer.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
