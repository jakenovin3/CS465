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
                System.out.println(activeParticipants.size());
                // if there has been an update to activeParticipants from NodeClient
                if( activeParticipants.size() > numParticipants ) {
                    // update numParticipants
                    numParticipants++;
                    clientConnection = new Socket(activeParticipants.get(numParticipants-1).getIP(), activeParticipants.get(numParticipants-1).getPort());
                    toClient = new ObjectOutputStream(clientConnection.getOutputStream());
                    // share session participants info with newly joined person
                    for( int iter = 0; iter < activeParticipants.size() - 1; iter++ ) {
                        // open objectOutputStream using connectivity info
                        
                        // Creating the join message
                        Message joinMsg = new Message(MessageTypes.MessageEnum.JOIN, activeParticipants.get(iter));
                        // Sending the join message to
                        this.toClient.writeObject(joinMsg);
                    }
                    toClient.close();
                    clientConnection.close();
                    // share new joined person info with session participants
                    if( activeParticipants.size() > 2 ) {
                        for( int iter = 1; iter < activeParticipants.size() - 1; iter++ ) {
                            clientConnection = new Socket(activeParticipants.get(iter).getIP(), activeParticipants.get(iter).getPort());
                            toClient = new ObjectOutputStream(clientConnection.getOutputStream());
                            // Creating the join message
                            Message joinMsg = new Message(MessageTypes.MessageEnum.JOIN, activeParticipants.get(numParticipants-1));
                            // Sending the join message to
                            toClient.writeObject(joinMsg);
                            toClient.close();
                            clientConnection.close();
                        }
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
                            toClient.writeObject(joinMsg);
                            toClient.close();
                            clientConnection.close();
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
                        isJoined = !isJoined;
                        for( int iter = 1; iter < activeParticipants.size(); iter++ ) {
                            // Creating the leave message
                            clientConnection = new Socket(activeParticipants.get(iter).getIP(), activeParticipants.get(iter).getPort());
                            toClient = new ObjectOutputStream(clientConnection.getOutputStream());
                            Message leaveMsg = new Message(MessageTypes.MessageEnum.LEAVE, node);
                            toClient.writeObject(leaveMsg);
                            toClient.close();
                            clientConnection.close();
                        }
                        activeParticipants.clear();
                    }
                    else { // When the user sends a NOTE
                        // Creating the NOTE message
                        
                        for( int iter = 1; iter < activeParticipants.size(); iter++ ) {
                            // iterate over participants. Send message to each in list, includes this client
                            input = node.getName() + ": " + input;
                            Message noteMsg = new Message(MessageTypes.MessageEnum.NOTE, input);
                            clientConnection = new Socket(activeParticipants.get(iter).getIP(), activeParticipants.get(iter).getPort());
                            toClient = new ObjectOutputStream(clientConnection.getOutputStream());
                            toClient.writeObject(noteMsg);
                            toClient.close();
                            clientConnection.close();
                        }
                        
                    }
                }
                else {
                    System.out.println( "You have not joined a session yet." );
                }
            }
            catch( IOException ex ){
                Logger.getLogger(NodeClient.class.getName()).log(Level.SEVERE, "Cannot connect to client", ex);
                System.exit(1);
            }
            
        }
    }
}
