import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                // implement array list counting, constantly comparing length of global list
                //   with personal array list, probably as an update function
                if( activeParticipants.size() > numParticipants ) {
                    // update numParticipants
                    numParticipants++;
                    // send last nodeinfo (newly joined participant) in activeParticipants to all other active participants

                    for( int iter = 0; iter < activeParticipants.size() - 1; iter++ ) {
                        // open objectOutputStream using connectivity info
                        clientConnection = new Socket(activeParticipants.get(iter).getIP(), activeParticipants.get(iter).getPort());
                        toClient = new ObjectOutputStream(clientConnection.getOutputStream());
                        // add yourself to activeParticipants
                        activeParticipants.add(node);
                        // Creating the join message
                        Message joinMsg = new Message(MessageTypes.MessageEnum.JOIN, activeParticipants.get(numParticipants));
                        // Sending the join message to
                        this.toClient.writeObject(joinMsg);
                        this.toClient.close();
                    }
                }
                // get the possible input
                input = reader.readLine();
                // When the user sends a message with JOIN
                if (input.startsWith("JOIN") && !node.checkJoined()) {
                    // if the user sends just "JOIN"
                    if ( input.length() == "JOIN".length() ) {
                        // add yourself to activeParticipants
                        activeParticipants.add(node);
                        // user successfully joined
                        node.setJoined();
                        System.out.println("Session has been started!");
                    }
                    else {
                        // get connectivity info
                        String[] connectInfo = input.split(" ")[1].split(",");
                        if (connectInfo.length != 2) {
                            System.out.println("Missing connectivity information.");
                        }
                        // if the user sends just "JOIN w/ IP and PORT"
                        else {
                            System.out.println("Connecting...");
                            // open objectOutputStream using connectivity info
                            clientConnection = new Socket(connectInfo[0], Integer.parseInt(connectInfo[1]));
                            toClient = new ObjectOutputStream(clientConnection.getOutputStream());
                            // add yourself to activeParticipants
                            activeParticipants.add(node);

                            // TO DELETE:
                            System.out.println( node.getName() + " was successful" );
                            // System.out.println( "Size: " + activeParticipants.size() );

                            // Creating the join message
                            Message joinMsg = new Message(MessageTypes.MessageEnum.JOIN, node);
                            // Sending the join message to
                            this.toClient.writeObject(joinMsg);
                            this.toClient.close();
                            // user successfully joined
                            node.setJoined();
                        }
                    }
                }
                else if (input.startsWith("JOIN") && node.checkJoined()) {
                    System.out.println("Already JOINED in a session!");
                }
                else if(node.checkJoined() ) {
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
                            input = node.getName() + ": " + input;
                            // iterate over participants. Send message to each in list, includes this client
                            Message noteMsg = new Message(MessageTypes.MessageEnum.NOTE, input);
                            clientConnection = new Socket(participant.getIP(), participant.getPort());
                            toClient = new ObjectOutputStream(clientConnection.getOutputStream());
                            this.toClient.writeObject(noteMsg);

                            // TO DELETE:
                            System.out.println( "(In Sender) Participant: " + participant.getName() );

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

/*
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
*/
