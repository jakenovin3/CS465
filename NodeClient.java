public class NodeClient {
    Message message;

    // each client needs to listen and have a server socket
    // the client should use two threads

    // Receiver class
    // Sender class extends Thread and implements MessageTypes/MessageEnumTypes ( whichever we choose to use )
    // use run
        // create two threads
        // receiver, listen for NOTEs, display to stdout
        // sender, accept user input to send to server
    // main
        // try properties file given args
        // start chat node

}
