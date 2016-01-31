package in.cs654.ksaurav.platform;

import in.cs654.ksaurav.util.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Broker extends Thread {

    private final Socket socket;
    private final static Logger LOGGER = Logger.getLogger(Broker.class.getName());

    public Broker(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            processMessage(reader);
        } catch (IOException e) {
            LOGGER.severe("Error in processing message: " + e.toString());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.severe("Error in closing socket: " + e.toString());
            }
        }
    }

    private void processMessage(BufferedReader reader) throws IOException {
        final String input = reader.readLine();
        final String messageHead = input.substring(0, input.indexOf(" "));
        switch (messageHead) {
            case "LOGIN":
                if (handleLogin(input)){
                    processMessage(reader);
                }
                break;
            case "LOGOUT":
                handleLogout(input);
                break;
            case "SUBSCRIBE":
                handleSubscribe(input);
                processMessage(reader);
                break;
            case "UNSUBSCRIBE":
                handleUnsubscribe(input);
                processMessage(reader);
                break;
            case "EMAILCHANGE":
                handleEmailChange(input);
                processMessage(reader);
                break;
            case "REGISTER":
                handleRegister(input);
                break;
            case "PUBLISH":
                handlePublish(input);
                break;
            default:
                LOGGER.warning("Message head didn't match any case. Ignoring message.");
                break;
        }
    }

    private void handlePublish(String input) throws IOException {
        final Message message = Message.convertToMessage(input);
        LOGGER.info(message.getPublisherId() + " published in " + message.getTopicId());
        // TODO add message to db?
        notifySubscribers(message);
    }

    private void notifySubscribers(Message message) throws IOException {
        List<String> subscribers = getSubscribersList(message.getTopicId());
        for (String subscriber : subscribers) {
            Broker broker = Server.brokerHashMap.get(subscriber);
            if (broker != null) {
                broker.sendMessage(message.getContent());
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
        List<String> subscribers = new ArrayList<>();
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
        LOGGER.info(email + " logged out");
    }

    private boolean handleLogin(String input) {
        // LOGIN 2020saurav@gmail.com
        String email = input.substring(input.indexOf(" ")+1);
        Server.brokerHashMap.put(email, this);
        LOGGER.info(email + " logged in");
        return true; // TODO check login in db if it exists
    }
}
