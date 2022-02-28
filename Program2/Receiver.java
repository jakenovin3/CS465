import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;

public class Receiver extends Thread {
     // use a ServerSocket, the ports between the client serversocket and other clients must be distinct
    ObjectInputStream fromClients = null;
    Socket clientConnection;
    ArrayList<NodeInfo> activeParticipants;
    public void run() {

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
                        int size = 0;
                        for( NodeInfo participant : activeParticipants ) {
                            if( participant.getName() != sendingNode.getName() ) {
                                size++;
                            }
                        }
                        if( size == activeParticipants.size() ) {
                            activeParticipants.add( sendingNode );
                        }
                        // if( !activeParticipants.contains( sendingNode ) ) {
                        //     activeParticipants.add( sendingNode );
                        // }

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

                        System.out.println( noteMessage );
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

    public Receiver( NodeInfo clientInfo, Socket clientSocket ) {
        
        clientConnection = clientSocket;
        activeParticipants = new ArrayList<NodeInfo>();
        activeParticipants.add(clientInfo);
    }
}
