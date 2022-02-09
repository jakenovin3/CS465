import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender extends Thread {
    Socket serverConnection = null;
    ObjectOutputStream toServer = null;


    // need a run method
    public void run( String name, String ip, int portNum ) {
        NodeInfo node = new NodeInfo( name, ip, portNum );
        String input;
        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );

        while( true ) {
            try {
                input = reader.readLine();

                // When the user sends a JOIN
                if (input.startsWith("JOIN")) {
                    // Connect to the other participants
                    System.out.println("Connecting...");

                    // Creating the join message
                    Message joinMsg = new Message(MessageTypes.MessageEnum.JOIN, node);

                    // Sending the join message to
                    this.toServer.writeObject(joinMsg);
                    this.toServer.close();
                }
                // When the user sends a LEAVE
                else if (input.startsWith("LEAVE")) {
                    // Creating the leave message
                    Message leaveMsg = new Message(MessageTypes.MessageEnum.LEAVE, node);

                    // Sending the join message to
                    this.toServer.writeObject(leaveMsg);
                    this.toServer.close();
                }
                // When the user sends a SHUTDOWN
                else if (input.startsWith("SHUTDOWN")) {
                    // Creating the SHUTDOWN message
                    Message shutdownMsg = new Message(MessageTypes.MessageEnum.SHUTDOWN, node);

                    // Sending the join message to
                    this.toServer.writeObject(shutdownMsg);
                    this.toServer.close();
                }
                // When the user sends a NOTE
                else {
                    // Creating the NOTE message
                    Message noteMsg = new Message(MessageTypes.MessageEnum.NOTE, node);

                    // Sending the join message to
                    this.toServer.writeObject(noteMsg);
                    this.toServer.close();

                }
            }
            catch( IOException exception ) {
                    System.out.println( "Error at end of infinite loop" );
            }
        }
    }
        // while loop
    public Sender(NodeInfo clientInfo) {
        try {
            serverConnection = new Socket(clientInfo.getIP(), clientInfo.getPort());
            toServer = new ObjectOutputStream(serverConnection.getOutputStream());

        }
        catch(IOException ex)
        {
            Logger.getLogger(NodeClient.class.getName()).log(Level.SEVERE, "Cannot connect to server", ex);
            System.exit(1);
        }
    }
}
