import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Receiver extends Thread {
    private Socket serverConnection = null;
    public ObjectInputStream fromServer = null;

    public Receiver(NodeInfo clientInfo) {
        try {
            serverConnection = new Socket(clientInfo.getIP(), clientInfo.getPort());
            fromServer = new ObjectInputStream(serverConnection.getInputStream());

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