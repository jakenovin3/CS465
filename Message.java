import java.io.Serializable;

public class Message implements Serializable {
    // The object streams need to be serializable
    public Message( int messageType, Object content ) {
        // determine type of message
        // cast content to appropriate type
    }
    // getter to provide message as whatever type appropriate
    public String getMessage() {
        return "";
    }

}
