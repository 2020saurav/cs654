package in.cs654.ksaurav.util;

public class Message {

    private String publisherId;
    private String topicId;
    private String content;

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

    public static Message convertToMessage(String input) {
        // PUBLISH P42 T42 Lorem ipsum dolor sit amet
        final int firstSpaceIndex = input.indexOf(" ");
        final int secondSpaceIndex = input.indexOf(" ", firstSpaceIndex+1);
        final int thirdSpaceIndex = input.indexOf(" ", secondSpaceIndex+1);
        final String publisherId = input.substring(firstSpaceIndex+1, secondSpaceIndex);
        final String topicId = input.substring(secondSpaceIndex+1, thirdSpaceIndex);
        final String content = input.substring(thirdSpaceIndex+1);
        return new Message(publisherId, topicId, content);
    }
}
