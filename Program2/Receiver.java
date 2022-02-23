import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Receiver extends Thread {
    // use a ServerSocket, the ports between the client serversocket and other clients must be distinct
    ObjectInputStream fromClients = null;
    // any message may be coming in from any other peer
    // Needs some logic: look for the type of requests. Look at server. There is more responsibility, receiving leave or join requests
    public void run() {
        // To Do: handle receiving a join message (NodeInfo)
        // handle receiving a leave message (NodeInfo)
        // handle receiving a NOTE/text (String)
        try{
            String displayMessage = (String) this.fromClients.readObject();
            System.out.println( "Message from server: " + displayMessage );
        } catch( ClassNotFoundException CNF) {}
        catch( IOException IOE ){}
    }

    public Receiver(NodeInfo clientInfo) {
        try {
            // have a server socket continuously looking for connection
            ServerSocket receivingServerSocket = new ServerSocket(clientInfo.getPort());
            Socket serverConnection = receivingServerSocket.accept();
            fromClients = new ObjectInputStream(serverConnection.getInputStream());
        }
        catch(IOException ex)
        {
            Logger.getLogger(NodeClient.class.getName()).log(Level.SEVERE, "Cannot connect to server", ex);
            System.exit(1);
        }
    }
}