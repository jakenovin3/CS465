import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// new joining person connects off of some one person's IP
// that participant adds joiner's info to their own list
// participant sends new list to all other participants to be reset
// joiner is effectively joined
// activeParticipants list locally present in NodeClient, shared with Sender and probably receiver

public class Sender extends Thread {
    boolean isJoined = false;
    int numParticipants = 1; // set to 1 to account for self
    NodeInfo node;
    Socket clientConnection = null;
    ObjectOutputStream toClient = null;
    private ArrayList<NodeInfo> activeParticipants;
    // without a server, a message must be handled completely by the client. In this context,
    // if you join, you do not have the knowledge of anybody else other than the peer. Once you join the mesh topology you must know of everyone.
    // need a run method

    public Sender( NodeInfo newNode ) {
        // set participants list to current participants assuming currentParticipants is the current list of clients in the session - including this client
        activeParticipants = new ArrayList<NodeInfo>();
        node = newNode;
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

    public void updateParticipants( ArrayList<NodeInfo> newParticipants ) {
        activeParticipants.clear();
        activeParticipants.addAll(newParticipants); // does this maintain order? sounds like it by description
    }

    public void run() {
        String input;
        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );

        while( true ) {
            // implement array list counting, constantly comparing length of global list
            //   with personal array list, probably as an update function
            if( activeParticipants.size() != numParticipants ) {
                // update numParticipants
                numParticipants++;
                // send last nodeinfo (newly joined participant) in activeParticipants to all other active participants
            }
            try {
                input = reader.readLine();
                // To Do: alter sending of message to go to all users in participants list (once this client is in the session)
                // The JOIN should be specific to the ip & port this client specified as their point of contact
                // When the user sends a JOIN
                if (input.startsWith("JOIN") && !isJoined) {
                    // if input.length() == length(JOIN)
                    // add yourself to currentParticipants
                    // else
                    // open objectOutputStream using connectivity info
                    // using OOS, send personal connectivity info (NodeInfo)
                    activeParticipants.add(node);
                    System.out.println("Connecting...");

                    // Creating the join message
                    Message joinMsg = new Message(MessageTypes.MessageEnum.JOIN, node);

                    // Sending the join message to
                    this.toClient.writeObject(joinMsg);
                    this.toClient.close();
                    // isJoined = true
                }
                else if (input.startsWith("JOIN") && isJoined) {
                    System.out.println("Already JOINED in a session!");
                }
                else if( isJoined ) {
                    // When the user sends a LEAVE
                    if (input.startsWith("LEAVE")) {
                        for ( NodeInfo participant : activeParticipants ) {
                            // Creating the leave message
                            clientConnection = new Socket(participant.getIP(), participant.getPort());
                            toClient = new ObjectOutputStream(clientConnection.getOutputStream());
                            Message leaveMsg = new Message(MessageTypes.MessageEnum.LEAVE, node);

                            this.toClient.writeObject(leaveMsg);
                            this.toClient.close();
                        }
                    }
                    else { // When the user sends a NOTE
                        // Creating the NOTE message
                        for ( NodeInfo participant : activeParticipants ) {
                            // iterate over participants. Send message to each in list, includes this client
                            Message noteMsg = new Message(MessageTypes.MessageEnum.NOTE, participant);
                            clientConnection = new Socket(participant.getIP(), participant.getPort());
                            toClient = new ObjectOutputStream(clientConnection.getOutputStream());
                            this.toClient.writeObject(noteMsg);
                            this.toClient.close();
                        }
                    }
                }
                else {
                    System.out.println( "You have not joined a session yet." );
                }
            }
            catch( IOException exception ) {
                System.out.println( "Error at end of infinite loop" );
            }
        }
    }
}