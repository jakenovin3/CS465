import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            try {
                // if there has been an update to activeParticipants from NodeClient
                if( activeParticipants.size() > numParticipants && activeParticipants.size() > 2 ) {
                    // update numParticipants
                    numParticipants++;
                    for( int iter = 0; iter < activeParticipants.size() - 1; iter++ ) {
                        // open objectOutputStream using connectivity info
                        clientConnection = new Socket(activeParticipants.get(iter).getIP(), activeParticipants.get(iter).getPort());
                        toClient = new ObjectOutputStream(clientConnection.getOutputStream());
                        // Creating the join message
                        Message joinMsg = new Message(MessageTypes.MessageEnum.JOIN, activeParticipants.get(numParticipants));
                        // Sending the join message to
                        this.toClient.writeObject(joinMsg);
                        this.toClient.close();
                    }
                }
                input = reader.readLine();
                // When the user sends a message with JOIN
                if (input.startsWith("JOIN") && !isJoined) {
                    // if the user sends just "JOIN"
                    if ( input.length() == "JOIN".length() ) {
                        // add yourself to activeParticipants
                        activeParticipants.add(node);
                        // user successfully joined
                        isJoined = true;
                        System.out.println("Session has been started!");
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

                            System.out.println( node.getName() + " was successful" );
                            // System.out.println( "Size: " + activeParticipants.size() );

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
                else if( input.equals("STATUS") ) {
                    System.out.println("Current participants:");
                    for( NodeInfo participant : activeParticipants ) {
                        System.out.println(participant.getName());
                    }
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
                                if( !participant.getName().equals(node.getName()) ) {
                                System.out.println("Sending message to: " + participant.getName());
                                // iterate over participants. Send message to each in list, includes this client
                                input = node.getName() + ": " + input;
                                Message noteMsg = new Message(MessageTypes.MessageEnum.NOTE, input);
                                clientConnection = new Socket(participant.getIP(), participant.getPort());
                                toClient = new ObjectOutputStream(clientConnection.getOutputStream());
                                this.toClient.writeObject(noteMsg);
                                this.toClient.close();
                            }
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
