package in.cs654.ksaurav.util;

/**
 * This class is used to store message structure containing publisherId, topicId and the content.
 * It also provides a function to directly convert the socket message to Message object.
 */
public class Message {

    private final String publisherId;
    private final String topicId;
    private final String content;

    public Message(String publisherId, String topicId, String content) {
        this.publisherId = publisherId;
        this.topicId = topicId;
        this.content = content;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getContent() {
        return content;
    }

    public String serialize() {
        return "PUBLISHER: " + getPublisherId() + "\n"
                + "TOPIC ID: " + getTopicId() + "\n"
                + "CONTENT: " + getContent() + "\n";
    }

    /**
     * To convert message from socket to Message object.
     * @param input from the socket message in the following format:
     *              "PUBLISH T42 Lorem ipsum dolor sit amet..."
     * @return Message object with corresponding fields.
     */
    public static Message convertToMessage(String input, String pubId) {

        final int firstSpaceIndex = input.indexOf(" ");
        final int secondSpaceIndex = input.indexOf(" ", firstSpaceIndex+1);
        final String topicId = input.substring(firstSpaceIndex+1, secondSpaceIndex);
        final String content = input.substring(secondSpaceIndex+1);
        return new Message(pubId, topicId, content);
    }
}
