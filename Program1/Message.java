import java.io.Serializable;

public class Message implements Serializable, MessageTypes {
    // The object streams need to be serializable
    private MessageTypes.MessageEnum msgType = null;
    private Object content = null;

    public Message( MessageTypes.MessageEnum msgType, Object content ){
        this.msgType = msgType;

        if( msgType == MessageEnum.JOIN
                || msgType == MessageEnum.LEAVE
                || msgType == MessageEnum.SHUTDOWN ) {
            this.content = (NodeInfo) content;
        }
        else {
            this.content = (String) content;
        }
    }

    public MessageTypes.MessageEnum getMessageType() {
        return msgType;
    }

    public Object getMessageContent() {
        return content;
    }
}
