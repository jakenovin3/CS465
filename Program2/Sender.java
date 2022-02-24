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
    public Sender( NodeInfo newNode ) {
        activeParticipants = new ArrayList<NodeInfo>();
        node = newNode;
    }

    public void updateParticipants( ArrayList<NodeInfo> newParticipants ) {
        activeParticipants.clear();
        activeParticipants.addAll(newParticipants); // does this maintain order? sounds like it by description
    }

    public void run() {
        String input;
        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );

        // infinite loop checking if message needs to be sent
        while( true ) {
            // implement array list counting, constantly comparing length of global list
            //   with personal array list, probably as an update function
            if( activeParticipants.size() > numParticipants ) {
                // update numParticipants
                numParticipants++;
                // send last nodeinfo (newly joined participant) in activeParticipants to all other active participants
              for( NodeInfo participant : activeParticipants ) {
                // open objectOutputStream using connectivity info
                clientConnection = new Socket(participant.getIP(), participant.getPort());
                toClient = new ObjectOutputStream(clientConnection.getOutputStream());
                // add yourself to activeParticipants
                activeParticipants.add(node);
                // Creating the join message
                Message joinMsg = new Message(MessageTypes.MessageEnum.JOIN, activeParticipants[numParticipants]);
                // Sending the join message to
                this.toClient.writeObject(joinMsg);
                this.toClient.close();
              }
            }
            try {
                // get the possible input
                input = reader.readLine();
                // When the user sends a message with JOIN
                if (input.startsWith("JOIN") && !isJoined) {
                    // if the user sends just "JOIN"
                    if ( input.length() == "JOIN".length() ) {
                        // add yourself to activeParticipants
                        activeParticipants.add(node);
                        // user successfully joined
                        isJoined = true;
                    }
                    else {
                        // get connectivity info
                        String[] connectInfo = input.split(" ")[1].split(",");
                        if (connectInfo.length != 2) {
                            System.out.println("Missing connectivity information.");
                        }
                        else {
                            System.out.println("Connecting...");
                            // open objectOutputStream using connectivity info
                            clientConnection = new Socket(connectInfo[0], Integer.parseInt(connectInfo[1]));
                            toClient = new ObjectOutputStream(clientConnection.getOutputStream());
                            // add yourself to activeParticipants
                            activeParticipants.add(node);
                            // Creating the join message
                            Message joinMsg = new Message(MessageTypes.MessageEnum.JOIN, node);
                            // Sending the join message to
                            this.toClient.writeObject(joinMsg);
                            this.toClient.close();
                            // user successfully joined
                            isJoined = true;
                        }
                    }
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
                            Message noteMsg = new Message(MessageTypes.MessageEnum.NOTE, input);
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

            catch( IOException ex ) {
                Logger.getLogger(NodeClient.class.getName()).log(Level.SEVERE, "Cannot connect to client", ex);
                System.exit(1);
            }
        }
    }
}