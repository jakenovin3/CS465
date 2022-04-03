import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Receiver extends Thread {
     // use a ServerSocket, the ports between the client serversocket and other clients must be distinct
    ObjectInputStream fromClient = null;
    ServerSocket receivingServerSocket;
    Socket clientConnection;

    ArrayList<NodeInfo> activeParticipants = new ArrayList<NodeInfo>();

    // any message may be coming in from any other peer
    // Needs some logic: look for the type of requests. Look at server. There is more responsibility, receiving leave or join requests
    public void run() {

        // The client which is being connected to from an incoming client will hold the responsibility of notifying the
        // rest of the chat of the incoming client
            // Responsibilites:
                // Tell every client to update their participant lists
                    // Loop through each participant: update their lists (arrayList.add())
                // Display "xxx has joined!"
        while( true )
        {
            try {
                    // Opens up input stream which gets message from the sender's output stream
                    ObjectInputStream fromClient = new ObjectInputStream( clientConnection.getInputStream() );

                    // Makes the incoming message a 'Message' type
                    Message message = (Message) fromClient.readObject();

                    NodeInfo sendingNode = null;
                    // Checks if message is a JOIN or LEAVE, make its content NodeInfo
                    if( message.getMessageType() != MessageTypes.MessageEnum.NOTE ) {
                        sendingNode = (NodeInfo) message.getMessageContent();
                    }

                    // Check if incoming message is JOIN
                    if( message.getMessageType() == MessageTypes.MessageEnum.JOIN ) {

                        // Check if joining client is already within ArrayList, if not, add them
                        if( !activeParticipants.contains( sendingNode ) ) {
                            activeParticipants.add( sendingNode );
                        }

                        System.out.println( sendingNode.getName() + " has joined the chat!" );
                    }
                    // Check if incoming message is LEAVE
                    else if( message.getMessageType() == MessageTypes.MessageEnum.LEAVE ) {

                        // Check if leaving client is already within ArrayList, if they are, remove them
                        if( activeParticipants.contains( sendingNode ) ) {
                            activeParticipants.remove( sendingNode );
                        }

                        System.out.println( sendingNode.getName() + " has left the chat!" );
                    }
                    // Check if incoming message is a NOTE
                    else if( message.getMessageType() == MessageTypes.MessageEnum.NOTE ) {
                        String noteMessage = (String) message.getMessageContent();

                        System.out.println( "NOTE : " + noteMessage );
                    }

                    // Close connection
                    fromClient.close();
                    clientConnection.close();
                }
            catch( ClassNotFoundException CNF) {}
            catch( IOException IOE ){}
        }
    }

    public ArrayList<NodeInfo> getUpdate() {
        return activeParticipants;
    }

    public Receiver( Socket clientSocket ) {
        
        clientConnection = clientSocket;
    }
}