package in.cs654.ksaurav.platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Broker extends Thread {
    private Socket socket;

    public Broker(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            processMessage(reader, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processMessage(BufferedReader reader, PrintWriter writer) throws IOException {
        String input = reader.readLine();
        String messageHead = input.substring(0, input.indexOf(" "));
        switch (messageHead) {
            case "LOGIN":
                if (handleLogin(input)){
                    processMessage(reader, writer);
                }
                break;
            case "LOGOUT":
                handleLogout(input); // Logout and return, so as to close the socket connection
                break;
            case "SUBSCRIBE":
                handleSubscribe(input);
                processMessage(reader, writer);
                break;
            case "UNSUBSCRIBE":
                handleUnsubscribe(input);
                processMessage(reader, writer);
                break;
            case "EMAILCHANGE":
                handleEmailChange(input);
                processMessage(reader, writer);
                break;
            case "REGISTER":
                handleRegister(input);
                break;
            case "PUBLISH":
                handlePublish(input);
                break;
            default:
                System.out.println(messageHead);
                break;
        }
    }

    private void handlePublish(String input) throws IOException {
        // PUBLISH P42 T42 Lorem ipsum dolor sit amet
        int firstSpaceIndex = input.indexOf(" ");
        int secondSpaceIndex = input.indexOf(" ", firstSpaceIndex+1);
        int thirdSpaceIndex = input.indexOf(" ", secondSpaceIndex+1);
        String publisherId = input.substring(firstSpaceIndex+1, secondSpaceIndex);
        String topicId = input.substring(secondSpaceIndex+1, thirdSpaceIndex);
        String message = input.substring(thirdSpaceIndex+1);
        // TODO add message to db?
        notifySubscribers(topicId, message);
    }

    private void notifySubscribers(String topicId, String message) throws IOException {
        List<String> subscribers = getSubscribersList(topicId);
        for (String subscriber : subscribers) {
            Broker broker = Server.brokerHashMap.get(subscriber);
            if (broker != null) {
                broker.sendMessage(message);
            } else {
                // TODO dump the message or store it somewhere to deliver later
            }
        }
    }

    private void sendMessage(String message) throws IOException {
        PrintWriter writer = new PrintWriter(this.socket.getOutputStream(), true);
        writer.println(message);
        writer.flush();
    }

    private List<String> getSubscribersList(String topicId) {
        // TODO change it to make query to db
        List<String> subscribers = new ArrayList<String>();
        subscribers.add("2020saurav@gmail.com");
        subscribers.add("ksaurav@iitk.ac.in");
        return subscribers;
    }

    private void handleRegister(String input) {
        String[] split = input.split(" ");
        String email = split[1];
        addUser(email);
    }

    private void addUser(String email) {
        // TODO insert in db
    }

    private void handleEmailChange(String input) {
        // TODO update db
        // TODO update hashmap
    }

    private void handleUnsubscribe(String input) {
        // UNSUBSCRIBE 2020saurav@gmail.com T42
        // TODO check login
        String[] split = input.split(" ");
        String email = split[1];
        String topicId = split[2];
        removeSubscription(email, topicId);
    }

    private void removeSubscription(String email, String topicId) {
        // TODO remove from db
    }

    private void handleSubscribe(String input) {
        // SUBSCRIBE 2020saurav@gmail.com T42
        // TODO check login
        String[] split = input.split(" ");
        String email = split[1];
        String topicId = split[2];
        addSubscription(email, topicId);
    }

    private void addSubscription(String email, String topicId) {
        // TODO insert into DB
    }

    private void handleLogout(String input) {
        // LOGOUT 2020saurav@gmail.com
        String email = input.substring(input.indexOf(" ")+1);
        Server.brokerHashMap.remove(email);
    }

    private boolean handleLogin(String input) {
        // LOGIN 2020saurav@gmail.com
        String email = input.substring(input.indexOf(" ")+1);
        Server.brokerHashMap.put(email, this);
        return true; // TODO check login in db if it exists
    }
}
