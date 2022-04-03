public class Message {

    private int OPEN_TRANSACTION = 0;
    private int CLOSE_TRANSACTION = 1;
    private int TRANSACTION_COMMITTED = 2;
    private int TRANSACTION_ABORTED = 3;
    private int READ_REQUEST = 4;
    private int WRITE_REQUEST = 5;
    private int READ_REQUEST_RESPONSE = 6;
    private int WRITE_REQUEST_RESPONSE = 7;
    private int SHUTDOWN = 8;

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
