package in.cs654.ksaurav.sub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Subscriber {
    public static void main(String[] args) throws IOException {
        final Socket socket = new Socket("127.0.0.1", 2020);
        final BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("USAGE:\n------\nA. REGISTER email \nB. LOGIN email\n\tSUBSCRIBE topicId\n\t" +
        "UNSUBSCRIBE topicId\n\tEMAILCHANGE newEmail\n\tLOGOUT\nC. GETTOPICS\n");

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
                    System.exit(0);
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
