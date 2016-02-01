package in.cs654.ksaurav.util;

/**
 * This class is used to store message structure containing publisher, topicId and the content.
 * It also provides a function to directly convert the socket message to Message object.
 */
public class Message {

    private final String publisher;
    private final String topicId;
    private final String content;

    public Message(String publisher, String topicId, String content) {
        this.publisher = publisher;
        this.topicId = topicId;
        this.content = content;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getContent() {
        return content;
    }

    public String serialize() {
        return "PUBLISHER: " + getPublisher() + "\n"
                + "TOPIC: " + Mongo.getTopicNameById(getTopicId()) + "\n"
                + "CONTENT: " + getContent() + "\n";
    }

    /**
     * To convert message from socket to Message object.
     * @param input from the socket message in the following format:
     *              "PUBLISH BBC T42 Lorem ipsum dolor sit amet..."
     * @return Message object with corresponding fields.
     */
    public static Message convertToMessage(String input) {
        final int firstSpaceIndex = input.indexOf(" ");
        final int secondSpaceIndex = input.indexOf(" ", firstSpaceIndex+1);
        final int thirdSpaceIndex = input.indexOf(" ", secondSpaceIndex+1);
        final String publisher = input.substring(firstSpaceIndex+1, secondSpaceIndex);
        final String topicId = input.substring(secondSpaceIndex+1, thirdSpaceIndex);
        final String content = input.substring(thirdSpaceIndex+1);
        return new Message(publisher, topicId, content);
    }
}
