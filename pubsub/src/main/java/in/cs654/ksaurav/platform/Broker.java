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

/**
 * This Broker class is used to handle requests from and responding to Publishers and Subscribers. Class Server listens
 * to incoming requests and creates a Broker thread to handle the request.
 * This broker thread parses the message and accordingly handles it. Some requests (like PUBLISH, REGISTER) need not
 * 4 keep an alive connection, and so the socket connection is closed after such requests, but other requests (like
 * LOGIN) keeps an alive connection remembering the socket so as to push the messages to the subscriber as and when
 * subscriber publishes.
 */
public class Broker extends Thread {

    private final Socket socket;
    private String email = "";
    private final static Logger LOGGER = Logger.getLogger(Broker.class.getName());
    private static final String NOT_LOGGED_IN_MESSAGE = "ERROR: You are not logged in. Please login to continue";
    private static final String ALREADY_LOGGED_IN_MESSAGE = "ERROR: You are already logged in";

    public Broker(Socket socket) {
        this.socket = socket;
    }

    /**
     * This function gets the input stream of the socket and starts the processing of the message. Method processMessage
     * recursively calls itself or returns based on the message.
     */
    public void run() {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            processMessage(reader);
        } catch (Exception e) {
            LOGGER.severe("Error in processing message: " + e.toString());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.severe("Error in closing socket: " + e.toString());
            }
        }
    }

    /**
     * This method parses the message and handles the request according to the head of the message.
     * @param reader buffered reader of the socket.
     * @throws IOException
     */
    private void processMessage(BufferedReader reader) throws IOException {
        final String input = reader.readLine();
        String messageHead;
        if (input == null) {
            LOGGER.warning("Null message received");
            return;
        }
        try {
            messageHead = input.substring(0, input.indexOf(" "));
        } catch (Exception e) {
            messageHead = "";
        }
        switch (messageHead) {
            case "LOGIN":
                if (handleLogin(input)){
                    processMessage(reader);
                }
                break;
            case "LOGOUT":
                handleLogout();
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
                this.sendMessage("Connection closed.");
                break;
        }
    }

    /**
     * This method handles request from a publisher to publish a message.
     * The message is parsed and is then passed on to all the subscribers of the topic of the message.
     * @param input string from the publisher
     * @throws IOException
     */
    private void handlePublish(String input) throws IOException {
        if (checkLogin()) {
            final Message message = Message.convertToMessage(input);
            LOGGER.info(message.getPublisher() + " published in " + message.getTopicId());
            // TODO add message to db?
            notifySubscribers(message);
        } else {
            this.sendMessage(NOT_LOGGED_IN_MESSAGE);
        }
    }

    /**
     * If email is empty, user not logged in
     * @return true if logged in, false otherwise
     */
    private boolean checkLogin() {
        return !this.email.equals("");
    }

    /**
     * This method gets the list of subscribers of the topic from the database and sends the message to those logged in.
     * TODO decide case for offline users
     * @param message object containing content, publisherId and topicId
     * @throws IOException
     */
    private void notifySubscribers(Message message) throws IOException {
        List<String> subscribers = getSubscribersList(message.getTopicId());
        for (String subscriber : subscribers) {
            Broker broker = Server.brokerHashMap.get(subscriber);
            if (broker != null) {
                broker.sendMessage(message.serialize());
            } else {
                // TODO dump the message or store it somewhere to deliver later
            }
        }
    }

    /**
     * Send message to subscriber on this broker thread
     * @param message to be sent to the subscriber
     * @throws IOException
     */
    private void sendMessage(String message) throws IOException {
        PrintWriter writer = new PrintWriter(this.socket.getOutputStream(), true);
        writer.println(message);
        writer.flush();
    }

    /**
     * Get list of subscribers of a selected topic (by Id)
     * @param topicId of the topic
     * @return list of subscribers (email)
     */
    private List<String> getSubscribersList(String topicId) {
        // TODO change it to make query to db
        List<String> subscribers = new ArrayList<>();
        subscribers.add("2020saurav@gmail.com");
        subscribers.add("ksaurav@iitk.ac.in");
        return subscribers;
    }

    /**
     * Register new user, by inserting email in db
     * @param input from subscriber socket
     */
    private void handleRegister(String input) {
        // REGISTER ksaurav@iitk.ac.in
        final String newEmail = input.split(" ")[1];
        addUser(newEmail);
        // login check not required here. Anyone should be able to register
    }

    /**
     * Insert email in database
     * @param email of the subscriber
     */
    private void addUser(String email) {
        // TODO insert in db, if not already exists
    }

    /**
     * Change email of the subscriber on current thread
     * @param input containing new email id
     */
    private void handleEmailChange(String input) throws IOException {
        // EMAILCHANGE ksaurav@iitk.ac.in
        if (checkLogin()) {
            final String newEmail = input.split(" ")[1];
            // TODO update db
            // TODO update hashmap
        } else {
            this.sendMessage(NOT_LOGGED_IN_MESSAGE);
        }
    }

    /**
     * Unsubscribe the current subscriber from selected topic
     * @param input with email and topicId
     */
    private void handleUnsubscribe(String input) throws IOException {
        // UNSUBSCRIBE T42
        if (checkLogin()) {
            final String topicId = input.split(" ")[1];
            removeSubscription(topicId);
        } else {
            this.sendMessage(NOT_LOGGED_IN_MESSAGE);
        }
    }

    /**
     * Remove subscription of the current user from selected topic
     * @param topicId of the topic to be unsubscribed from
     */
    private void removeSubscription(String topicId) {
        // TODO remove from db
    }

    /**
     * Subscribe to a topic
     * @param input containing the topic id
     */
    private void handleSubscribe(String input) throws IOException {
        // SUBSCRIBE T42
        if (checkLogin()) {
            final String topicId = input.split(" ")[1];
            addSubscription(topicId);
        } else {
            this.sendMessage(NOT_LOGGED_IN_MESSAGE);
        }
    }

    /**
     * Add subscription to selected topic id
     * @param topicId of the selected topic
     */
    private void addSubscription(String topicId) {
        // TODO insert into DB
    }

    /**
     * Logout the current user
     */
    private void handleLogout() throws IOException {
        if (checkLogin()) {
            Server.brokerHashMap.remove(this.email);
            LOGGER.info(this.email + " logged out");
            this.email = "";
        } else {
            this.sendMessage(NOT_LOGGED_IN_MESSAGE);
        }
    }

    /**
     * Login the user (no authentication check for now)
     * @param input containing email of the user
     * @return true if the valid user and not already logged in
     */
    private boolean handleLogin(String input) throws IOException {
        // LOGIN 2020saurav@gmail.com
        if (checkLogin()) {
            this.sendMessage(ALREADY_LOGGED_IN_MESSAGE);
            return true; // allow to continue
        } else if (Server.brokerHashMap.containsKey(this.email)) {
            this.sendMessage(ALREADY_LOGGED_IN_MESSAGE + " at " + (this.socket.getInetAddress().toString()));
            return false; // not allowed to login
        } else {
            this.email = input.split(" ")[1];
            Server.brokerHashMap.put(this.email, this);
            LOGGER.info(this.email + " logged in");
            this.sendMessage("WELCOME " + this.email);
            return true; // TODO check login in db if it exists
        }
    }
}
