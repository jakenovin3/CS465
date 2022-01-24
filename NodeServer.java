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
    String propertiesFile = "/home/vrm/Documents/school/CS465/CS465/Server.properties";

    public void start() {
        try
        {
            properties = new utils.PropertyHandler(propertiesFile);
        }
        catch (IOException ex)
        {
            Logger.getLogger(NodeClient.class.getName()).log(Level.SEVERE, "Cannot open properties file", ex);
            System.exit(1);
        }

        // get server IP
        try
        {
            ip = properties.getProperty("SERVER_IP");
        }
        catch (Exception ex)
        {
            Logger.getLogger(NodeClient.class.getName()).log(Level.SEVERE, "Cannot read server IP", ex);
            System.exit(1);
        }

        // Get port number
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
                ServerSocket serverSocket = new ServerSocket( portNum );
                Socket receiverSocket = serverSocket.accept();

                ObjectInputStream fromClient = new ObjectInputStream( receiverSocket.getInputStream() );
                ObjectOutputStream toClient = new ObjectOutputStream( receiverSocket.getOutputStream() );

                Message received = (Message) fromClient.readObject();

                receiverSocket.close();

                String userName = ((NodeInfo) received.getMessageContent()).getName();

                // When the message received is a JOIN
                if( received.getMessageType() == MessageTypes.MessageEnum.JOIN ) {
                    if( activeParticipants.contains( (NodeInfo) received.getMessageContent() )) {
                        System.out.println( userName + " is already in!");
                    }
                    else{
                        if( inactiveParticipants.contains( (NodeInfo) received.getMessageContent() )) {
                            inactiveParticipants.remove( (NodeInfo) received.getMessageContent() );
                        }

                        activeParticipants.add( (NodeInfo) received.getMessageContent() );
                        System.out.println( userName + " has joined the chat!");
                    }
                }

                // When the message received is a LEAVE
                else if( received.getMessageType() == MessageTypes.MessageEnum.LEAVE ) {
                    if( activeParticipants.contains( (NodeInfo) received.getMessageContent() )) {
                        activeParticipants.remove( (NodeInfo) received.getMessageContent() );
                        inactiveParticipants.add( (NodeInfo) received.getMessageContent() );
                        System.out.println( userName + " has been removed from the chat!");
                    }
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

                        toClient.close();
                        fromClient.close();
                    }
                }
                // When the message received is a NOTE
                else {
                    String note = (String) received.getMessageContent();
                    Socket socket = null;
                    ObjectOutputStream messageStream = null;
                    for( NodeInfo user : activeParticipants ) {

                        if( user.getName() != userName ) {
                            socket = new Socket( user.getIP(), user.getPort() );
                            messageStream = new ObjectOutputStream(socket.getOutputStream());

                            // Creating message to be sent to other users and sending it over the stream
                            Message sentMessage = new Message( MessageTypes.MessageEnum.NOTE , note );
                            messageStream.writeObject( sentMessage );
                        }
                    }

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
        NodeServer server = new NodeServer();
        server.start();
    }
}
