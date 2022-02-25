import org.w3c.dom.Node;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class NodeClient implements MessageTypes {
    private static int portNum = 0;
    private static String name, ip = "";
    static Properties properties = null;
    static String propertiesFile = "/home/vrm/Documents/school/CS465/CS465/Server.properties";
    NodeInfo node;

    public void start() {
        // client should keep track of status on server
        Boolean joined = false;
        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );

        try {
            // Collects new user's name
            System.out.println( "Provide a name: " );
            name = reader.readLine();

            System.out.println( "Welcome in! Waiting on instructions..." );
            // Opens up connections
//            Socket socket = new Socket( ip, portNum );
            node = new NodeInfo( name, ip, portNum );
            Receiver receiver = new Receiver( node );
            Sender sender = new Sender( node );

            receiver.start();
            sender.start();

            // Loop that keeps reading the client input string
            // change the message types to uppercase

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
