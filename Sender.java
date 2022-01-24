import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender extends Thread {
    private Socket serverConnection = null;
    public ObjectOutputStream toServer = null;

    public Sender(NodeInfo clientInfo) {
        try {
            serverConnection = new Socket(clientInfo.getIP(), clientInfo.getPort());
            // create thread
            toServer = new ObjectOutputStream(serverConnection.getOutputStream());

        }
        catch(IOException ex)
        {
            Logger.getLogger(NodeClient.class.getName()).log(Level.SEVERE, "Cannot connect to server", ex);
            System.exit(1);
        }
    }

    public Socket getSocket() {
        return serverConnection;
    }
    // start method? How do we run this in parallel, sender and receiver
}