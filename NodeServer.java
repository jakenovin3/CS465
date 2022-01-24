import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.logging.*;
import java.net.*;

public class NodeServer {
    // Use array to store info for each client node
    public static ArrayList<NodeInfo> activeParticipants = new ArrayList<NodeInfo>();
    public static ArrayList<NodeInfo> inactiveParticipants = new ArrayList<NodeInfo>();

    int portNum = 0;
    String ip = "";
    Properties properties = null;
    String propertiesFile = "C:\\Users\\Jake\\Documents\\Classes\\Year 4\\CS465\\Git_Prog_1\\Server.properties";

    public void start() {
        // Attempts to open the properties file
        try
        {
            properties = new utils.PropertyHandler(propertiesFile);
        }
        catch (IOException ex)
        {
            Logger.getLogger(NodeClient.class.getName()).log(Level.SEVERE, "Cannot open properties file", ex);
            System.exit(1);
        }

        // Gets server IP from properties file
        try
        {
            ip = properties.getProperty("SERVER_IP");
        }
        catch (Exception ex)
        {
            Logger.getLogger(NodeClient.class.getName()).log(Level.SEVERE, "Cannot read server IP", ex);
            System.exit(1);
        }

        // Gets server PORT from properties file
        try
        {
            portNum = Integer.parseInt( properties.getProperty("SERVER_PORT") );
        }
        catch (Exception ex)
        {
            Logger.getLogger(NodeClient.class.getName()).log(Level.SEVERE, "Cannot read server PORT", ex);
            System.exit(1);
        }

        while(true)
        {
            try {
                // Opens up a socket to connect to client
                ServerSocket serverSocket = new ServerSocket( portNum );
                Socket receiverSocket = serverSocket.accept();

                // Creates object streams to send NodeInfo over
                ObjectInputStream fromClient = new ObjectInputStream( receiverSocket.getInputStream() );
                ObjectOutputStream toClient = new ObjectOutputStream( receiverSocket.getOutputStream() );

                Message received = (Message) fromClient.readObject();

                receiverSocket.close();

                String userName = ((NodeInfo) received.getMessageContent()).getName();

                // When the message received is a JOIN
                if( received.getMessageType() == MessageTypes.MessageEnum.JOIN ) {
                    // Detects if user is already an active participant
                    if( activeParticipants.contains( (NodeInfo) received.getMessageContent() )) {
                        System.out.println( userName + " is already in!");
                    }
                    else{
                        // Adds user to active participants if they are not already
                        if( inactiveParticipants.contains( (NodeInfo) received.getMessageContent() )) {
                            inactiveParticipants.remove( (NodeInfo) received.getMessageContent() );
                        }

                        activeParticipants.add( (NodeInfo) received.getMessageContent() );
                        System.out.println( userName + " has joined the chat!");
                    }
                }

                // When the message received is a LEAVE
                else if( received.getMessageType() == MessageTypes.MessageEnum.LEAVE ) {
                    // If a user is still active, makes them inactive and removes from chat
                    if( activeParticipants.contains( (NodeInfo) received.getMessageContent() )) {
                        activeParticipants.remove( (NodeInfo) received.getMessageContent() );
                        inactiveParticipants.add( (NodeInfo) received.getMessageContent() );
                        System.out.println( userName + " has been removed from the chat!");
                    }
                    // Detects if user is already an inactive participant
                    else if( inactiveParticipants.contains( (NodeInfo) received.getMessageContent() ) ){
                        System.out.println( userName + " has already left the chat!");
                    }
                }
                // When the message received is a SHUTDOWN
                else if( received.getMessageType() == MessageTypes.MessageEnum.SHUTDOWN ) {
                    if( !activeParticipants.contains( (NodeInfo) received.getMessageContent() ) &&
                            !inactiveParticipants.contains( (NodeInfo) received.getMessageContent() )) {
                        System.out.println( userName + " is not in the system!");
                    }
                    else{
                        if (activeParticipants.contains( (NodeInfo) received.getMessageContent() )) {
                            activeParticipants.remove( (NodeInfo) received.getMessageContent() );
                        }

                        if (inactiveParticipants.contains( (NodeInfo) received.getMessageContent() )) {
                            inactiveParticipants.remove( (NodeInfo) received.getMessageContent() );
                        }
                        System.out.println( userName + " has been shutdown!");

                        // Closing connections
                        toClient.close();
                        fromClient.close();
                    }
                }
                // When the message received is a NOTE
                else {
                    String note = (String) received.getMessageContent();
                    Socket socket = null;
                    ObjectOutputStream messageStream = null;
                    // Loops through active participants and sends a message to each one
                    for( NodeInfo user : activeParticipants ) {

                        if( user.getName() != userName ) {
                            socket = new Socket( user.getIP(), user.getPort() );
                            messageStream = new ObjectOutputStream(socket.getOutputStream());

                            // Creating message to be sent to other users and writing it to the stream
                            Message sentMessage = new Message( MessageTypes.MessageEnum.NOTE , note );
                            messageStream.writeObject( sentMessage );
                        }
                    }

                    // Closing connections
                    messageStream.close();
                    socket.close();
                }
            } catch ( IOException IOE ) {

            } catch ( ClassNotFoundException CNF ) {
                System.out.println("ClassNotFoundException");
            }
        }
    }

    public static void main(String[] args) {

        // Creates server object and starts it up
        NodeServer server = new NodeServer();
        server.start();
    }
}