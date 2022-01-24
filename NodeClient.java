import org.w3c.dom.Node;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class NodeClient implements MessageTypes{
    private static int portNum = 0;
    private static String name, ip = "";
    static Properties properties = null;
    static String propertiesFile = "C:\\Users\\Jake\\Documents\\Classes\\Year 4\\CS465\\Git_Prog_1\\Server.properties";
    NodeInfo node;

    public void start() {
        String input;
        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        String host = "localhost";

        try {
            System.out.println( "Provide a name: " );
            name = reader.readLine();

            System.out.println( "Welcome in! Waiting on instructions..." );

            // Loop that keeps reading the input string
            while( true ) {
                input = reader.readLine();
                input = input.toLowerCase();

                Socket socket = new Socket( ip, 23657 );
                ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

                try{
                    String displayMessage = (String) fromServer.readObject();
                } catch( ClassNotFoundException CNF) {

                }

                if( input.equals( "join" ) ) {

                    node = new NodeInfo( name, ip, portNum );

                    // Connect to the other participants
                    System.out.println( "Connecting..." );

                    // Creating the join message
                    Message joinMsg = new Message( MessageTypes.MessageEnum.JOIN , node );

                    // Sending the join message to
                    toServer.writeObject( joinMsg );

                    socket.close();
                    toServer.close();
                    fromServer.close();
                }
                else if( input.equals( "leave" ) ) {
                    node = new NodeInfo( name, ip, portNum );

                    // Creating the leave message
                    Message leaveMsg = new Message( MessageTypes.MessageEnum.LEAVE , node );

                    // Sending the join message to
                    toServer.writeObject( leaveMsg );

                    socket.close();
                    toServer.close();
                    fromServer.close();
                }
                else if( input.equals( "shutdown" ) ) {
                    node = new NodeInfo( name, ip, portNum );

                    // Creating the SHUTDOWN message
                    Message shutdownMsg = new Message( MessageTypes.MessageEnum.SHUTDOWN , node );

                    // Sending the join message to
                    toServer.writeObject( shutdownMsg );

                    socket.close();
                    toServer.close();
                    fromServer.close();
                }
                else {
                    node = new NodeInfo( name, ip, portNum );

                    // Creating the NOTE message
                    Message noteMsg = new Message( MessageTypes.MessageEnum.NOTE , node );

                    // Sending the join message to
                    toServer.writeObject( noteMsg );

                    socket.close();
                    toServer.close();
                    fromServer.close();
                }
            }
        }
        catch( IOException exception ) {
            System.out.println( "Error at end of infinite loop" );
        }
    }

    public static void main(String[] args) throws IOException {
        // open properties
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

        NodeClient client = new NodeClient();
        client.start();
    }
}

