import org.w3c.dom.Node;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class NodeClient implements MessageTypes {
    private static int portNum = 0;
    private static String name, ip = "";
    static Properties properties = null;
    static String propertiesFile = "C:\\Users\\Jake\\Documents\\Classes\\Year 4\\CS465\\Git_Prog_1\\Server.properties";
    NodeInfo node;

    public void start() {
        String input;
        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );

        try {
            // Collects new user's name
            System.out.println( "Provide a name: " );
            name = reader.readLine();

            System.out.println( "Welcome in! Waiting on instructions..." );

            // Loop that keeps reading the client input string
            while( true ) {
                input = reader.readLine();
                input = input.toLowerCase();

                // Opens up connections
                Socket socket = new Socket( ip, portNum );
                node = new NodeInfo( name, ip, portNum );
                Receiver receiver = new Receiver( node );
                Sender sender = new Sender( node );

                receiver.start();
                sender.start();

                // we are blocking using the readObject method. We need to create a thread by implementing runnable
                //or inheriting from thread. Create an instance of the class and call .start method for threading. Create one class that listens to the server and one class that listens to user input.
                try{
                   String displayMessage = (String) receiver.fromServer.readObject();
                } catch( ClassNotFoundException CNF) {

                }

                // When the user sends a JOIN
                if( input.equals( "join" ) ) {
                    // Connect to the other participants
                    System.out.println( "Connecting..." );

                    // Creating the join message
                    Message joinMsg = new Message( MessageTypes.MessageEnum.JOIN , node );

                    // Sending the join message to
                    sender.toServer.writeObject( joinMsg );

                    socket.close();
                    sender.toServer.close();
                    receiver.fromServer.close();
                }
                // When the user sends a LEAVE
                else if( input.equals( "leave" ) ) {
                    // Creating the leave message
                    Message leaveMsg = new Message( MessageTypes.MessageEnum.LEAVE , node );

                    // Sending the join message to
                    sender.toServer.writeObject( leaveMsg );

                    socket.close();
                    sender.toServer.close();
                    receiver.fromServer.close();
                }
                // When the user sends a SHUTDOWN
                else if( input.equals( "shutdown" ) ) {
                    // Creating the SHUTDOWN message
                    Message shutdownMsg = new Message( MessageTypes.MessageEnum.SHUTDOWN , node );

                    // Sending the join message to
                    sender.toServer.writeObject( shutdownMsg );

                    socket.close();
                    sender.toServer.close();
                    receiver.fromServer.close();
                }
                // When the user sends a NOTE
                else {
                    // Creating the NOTE message
                    Message noteMsg = new Message( MessageTypes.MessageEnum.NOTE , node );

                    // Sending the join message to
                    sender.toServer.writeObject( noteMsg );

                    socket.close();
                    sender.toServer.close();
                    receiver.fromServer.close();
                }
            }
        }
        catch( IOException exception ) {
            System.out.println( "Error at end of infinite loop" );
        }
    }

    public static void main(String[] args) throws IOException {
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

        // Creation of client object and starting it up
        NodeClient client = new NodeClient();
        client.start();
    }
}
