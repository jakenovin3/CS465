import java.util.ArrayList;
import java.net.*;

public class NodeServer {
    // Use array to store info for each client node
    public static ArrayList<NodeInfo> participants = new ArrayList<NodeInfo>();

    public static void main() {
        // scanner will be used to parse input
        // multithreaded loop to distribute threads to clients
        // NodeClient should use two threads, one for listening and the other for being able to send a message to the server
        // There is a server socket class. Open socket on both client and server?

        // *whenever a connection comes in from a client, a thread is spawned to handle the message in the same
        // thread the server opens sockets to all clients to send out the message then closes the connection and
        // terminates the thread.

        //



    }
}
