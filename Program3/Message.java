public class Message {
    /*
    DESCRIPTION:
        Manipulates given message data, organizes each message into individual objects
        implements MessageTypes

    ------------------------------------------------------------
    METHODS:
        getMessageType()
            returns message type

        getMessageContent()
            returns content within message object
     */
    private int OPEN_TRANSACTION = 0;
    private int CLOSE_TRANSACTION = 1;
    private int TRANSACTION_COMMITTED = 2;
    private int READ_REQUEST = 3;
    private int WRITE_REQUEST = 4;

    public int getOpenTrans() {
        return OPEN_TRANSACTION;
    }

    public int getClosedTrans() {
        return CLOSE_TRANSACTION;
    }

    public int getCommitTrans() {
        return TRANSACTION_COMMITTED;
    }

    public int getReadReq() {
        return READ_REQUEST;
    }

    public int getWriteReq() {
        return WRITE_REQUEST;
    }
}
