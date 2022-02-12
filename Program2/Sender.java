import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender extends Thread {
    Socket clientConnection = null;
    ObjectOutputStream toClients = null;
    private ArrayList<NodeInfo> activeParticipants;
    // without a server, a message must be handled completely by the client. In this context,
    // if you join, you do not have the knowledge of anybody else other than the peer. Once you join the mesh topology you must know of everyone. 
    // need a run method

    public Sender( ArrayList<NodeInfo> currentParticipants ) {
        // set participants list to current participants assuming currentParticipants is the current list of clients in the session - including this client
        activeParticipants = new ArrayList<NodeInfo>(currentParticipants);
        
        // try {
        //     // this will not be a one and done process now. The information of
        //     // each other client in the participants list will need to be iterated
        //     // over: socket and output stream set, data sent, socket and stream closed, then repeat.
        //     serverConnection = new Socket(clientInfo.getIP(), clientInfo.getPort());
        //     toServer = new ObjectOutputStream(serverConnection.getOutputStream());
            
        // }
        // catch(IOException ex)
        // {
        //     Logger.getLogger(NodeClient.class.getName()).log(Level.SEVERE, "Cannot connect to server", ex);
        //     System.exit(1);
        // }
    }

    public void run() {
        String input;
        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );

        while( true ) {
            try {
                input = reader.readLine();
                // To Do: alter sending of message to go to all users in participants list (once this client is in the session)
                // The JOIN should be specific to the ip & port this client specified as their point of contact
                // When the user sends a JOIN
                if (input.startsWith("JOIN")) {
                    // Connect to the other participants. What changes if this joining client is the first to join the session?
                    // joining a session should invoke a response from the receiver. Should they send a whole arraylist of the current participants?
                    // could we instead relay the join message to everyone else in the session and they all in turn respond with their NodeInfo.
                            // I think this could work because upon receiving a join request, if we say the response should be to send your own NodeInfo,
                            // then a joining client will be giving their info to everyone in the session and in return every existing client will
                            // send their client info to the newly joining client with the message type 'join' on both ends. Everyone who see's join
                            // can add the nodeinfo to their list.
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

                    this.toServer.writeObject(leaveMsg);
                    this.toServer.close();
                }
                // When the user sends a SHUTDOWN
                else if (input.startsWith("SHUTDOWN")) {
                    // Creating the SHUTDOWN message
                    Message shutdownMsg = new Message(MessageTypes.MessageEnum.SHUTDOWN, node);

                    this.toServer.writeObject(shutdownMsg);
                    this.toServer.close();
                }
                else { // When the user sends a NOTE
                    // Creating the NOTE message
                    for( NodeInfo participant : activeParticipants ) {
                        // iterate over participants. Send message to each in list, includes this client
                        Message noteMsg = new Message(MessageTypes.MessageEnum.NOTE, participant);
                        clientConnection = new Socket(participant.getIP(), participant.getPort());
                        toClients = new ObjectOutputStream(clientConnection.getOutputStream());
                        this.toClients.writeObject(noteMsg);
                        this.toClients.close();
                    }
                }
            }
            catch( IOException exception ) {
                    System.out.println( "Error at end of infinite loop" );
            }
        }
    }
}
