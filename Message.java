import java.io.Serializable;

public class Message implements Serializable {
    // The object streams need to be serializable
    public Message(){};
    public Object Message( MessageTypes messageType, Object content ) {
        // determine type of message
        if( messageType == MessageTypes.JOIN
            || messageType == MessageTypes.LEAVE
            || messageType == MessageTypes.SHUTDOWN ) {
            NodeInfo participantInfo = (NodeInfo) content;
            return participantInfo;
        }
        String participantNote = (String) content;
        return participantNote;
    }

}
