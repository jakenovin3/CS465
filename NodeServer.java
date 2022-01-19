import java.util.ArrayList;
import java.net.*;

public class NodeServer {
    public static void main() {
        // Use array to store info for each client node
        ArrayList<NodeInfo> connectivityInfo;

        // scanner will be used to parse input
        // multithreaded loop to distribute threads to clients
        // NodeClient should use two threads, one for listening and the other for being able to send a message to the server
        // There is a server socket class. Open socket on both client and server?

        // *whenever a connection comes in from a client, a thread is spawned to handle the message in the same
        // thread the server opens sockets to all clients to send out the message then closes the connection and
        // terminates the thread.

        //

        // server waits for message
            // Server receives a message, check type, use scanner:
                // NOTE
                    // cast content to String
                    // send NOTE to all clients using NodeInfo
                // JOIN
                    // cast content to NodeInfo
                // LEAVE
                    // cast content to NodeInfo
                // SHUTDOWN
                    // cast content to NodeInfo
                // SHUTDOWNALL
                    // cast content to NodeInfo

    }
}
