package in.cs654.ksaurav.util;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains all required functions for database operations required in this application.
 * Database : pubsub
 * Collections : subs - email of subscribers
 *               topics - topicId and topicName
 *               topicSubs - topicId and list of subscribers in that topic
 *               pendingMsgs - undelivered messages
 */
public class Mongo {
    private static final MongoClient mongoClient = new MongoClient("localhost", 27017);
    private static final MongoDatabase db = mongoClient.getDatabase("pubsub");
    private static final MongoCollection<Document> subs = db.getCollection("subs");
    private static final MongoCollection<Document> topics = db.getCollection("topics");
    private static final MongoCollection<Document> topicSubs = db.getCollection("topicSubs");
    private static final MongoCollection<Document> pendingMsgs = db.getCollection("pendingMsgs");

    /**
     * Method to check if the subscriber's email is in the database
     * @param email to check
     * @return true if present, false otherwise
     */
    public static boolean isSubscriberPresent(String email) {
        FindIterable<Document> docs = subs.find(new Document("email", email));
        return docs.first() != null;
    }

    /**
     * Method to add new subscriber. If the email is already present, request is discarded and false is returned
     * @param email to insert
     * @return false if already existing email, true otherwise.
     */
    public static boolean insertSubscriber(String email) {
        if (!isSubscriberPresent(email)) {
            subs.insertOne(new Document("email", email));
            return true;
        }
        return false;
    }

    /**
     * Method to change email of a subscriber. To change the email, the old one must be present in db, whereas the new
     * one must not have already been taken, i.e should not be present in db.
     * @param oldEmail old email to be replaced
     * @param newEmail new email to be set
     * @return true if email was changed, false otherwise.
     */
    public static boolean changeEmail(String oldEmail, String newEmail) {
        if (isSubscriberPresent(oldEmail) && !isSubscriberPresent(newEmail)) {
            subs.updateOne(new Document("email", oldEmail),
                    new Document("$set", new Document("email", newEmail)));
            return true;
        }
        return false;
    }

    /**
     * Get "_id", guaranteed to be unique (by Mongo). I am keeping this _id as identifier which remains invariant for
     * a subscriber. This _id is used in subscriptions as well.
     * @param email whose id is required
     * @return id corresponding to the email. If email is not present, return empty string.
     */
    public static String getIdFromEmail(String email) {
        if (isSubscriberPresent(email)) {
            FindIterable<Document> docs = subs.find(new Document("email", email));
            return docs.first().get("_id").toString();
        } else {
            return "";
        }
    }

    /**
     * Get email from "_id" (of Mongo).
     * @param id of the subscriber
     * @return email of the user. If no such id exists, return empty string.
     */
    public static String getEmailFromId(String id) {
        FindIterable<Document> docs = subs.find(new Document("_id", new ObjectId(id)));
        if (docs.first() != null) {
            return docs.first().get("email").toString();
        } else {
            return "";
        }
    }

    /**
     * Method to insert undelivered messages along with the subscriber info (id).
     * @param email of the subscriber
     * @param message to be saved
     */
    public static void insertPendingMessage(String email, String message) {
        String subId = getIdFromEmail(email);
        if (!subId.equals("")) {
            pendingMsgs.insertOne(new Document("subId", subId).append("message", message));
        }
    }

    /**
     * Method to find all messages of a given subscriber in pending database, delete them from db and return the list.
     * @param email of the subscriber
     * @return list of string, containing the messages
     */
    public static List<String> popPendingMessages(String email) {
        List<String> messages = new ArrayList<>();
        FindIterable<Document> docs = pendingMsgs.find(new Document("subId", getIdFromEmail(email)));
        docs.forEach((Block<Document>) document -> {
            messages.add(document.get("message").toString());
        });
        pendingMsgs.deleteMany(new Document("subId", getIdFromEmail(email)));
        return messages;
    }

    /**
     * Method to get list of subscriber of a given topic
     * @param topicId of the topic
     * @return list of subscriber of a given topic
     */
    public static List<String> getSubscriberByTopic(String topicId) {
        List<String> subscribers = new ArrayList<>();
        Document doc = topicSubs.find(new Document("topicId", topicId)).first();
        List<Document> docs = (ArrayList) doc.get("subs");
        subscribers.addAll(docs.stream().filter(d -> d.get("subId") != null)
                                .map(d -> getEmailFromId(d.getString("subId")))
                                .collect(Collectors.toList()));
        return subscribers;
    }

    /**
     * Method to get name of the topic from topicId
     * @param topicId of the topic
     * @return name of the topic
     */
    public static String getTopicNameById(String topicId) {
        Document doc = topics.find(new Document("topicId", topicId)).first();
        return (doc != null) ? doc.getString("topicName") : null;
    }

    /**
     * Method to insert new topic
     * @param topicId of the topic
     * @param topicName of the topic
     * @return true if new topicId, false if pre-existing topicId
     */
    public static boolean addTopic(String topicId, String topicName) {
        if(topics.find(new Document("topicId", topicId)).first() == null) {
            topics.insertOne(new Document("topicId", topicId).append("topicName", topicName));
            List<Document> subsList = new ArrayList<>();
            Document document = new Document("topicId", topicId).append("subs", subsList);
            topicSubs.insertOne(document);
            return true;
        }
        return false;
    }

    /**
     * Method to subscribe to a new topic
     * @param email of the subscriber
     * @param topicId of the topic to be subscribed
     */
    public static void addTopicSubscription(String email, String topicId) {
        if (getTopicNameById(topicId) != null && isSubscriberPresent(email)) {
            topicSubs.updateOne(new Document("topicId", topicId),
                    new Document("$addToSet", new Document("subs", new Document("subId", getIdFromEmail(email)))));
        }
    }

    /**
     * Method to remove subscription of a topic
     * @param email of the (un)subscriber
     * @param topicId of the topic to be unsubscribed
     */
    public static void removeTopicSubscription(String email, String topicId) {
        if (getTopicNameById(topicId) != null) {
            topicSubs.updateOne(new Document("topicId", topicId),
                    new Document("$pull", new Document("subs", new Document("subId", getIdFromEmail(email)))));
        }
    }
}
