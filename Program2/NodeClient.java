import org.w3c.dom.Node;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class NodeClient implements MessageTypes {
    private static int portNum = 0;
    private static String name, ip = "";
    public static ArrayList<NodeInfo> activeParticipants = new ArrayList<NodeInfo>();
    NodeInfo node;

    public void start() {
        // client should keep track of status on server
        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );

        try {
            // Collects new user's name
            System.out.println( "Provide a name: " );
            name = reader.readLine();

            System.out.println( "Welcome in! Waiting on instructions..." );
            
            // get ip from user
            System.out.println( "Provide ip: " );
            ip = reader.readLine();
            
            // get port from user
            System.out.println( "Provide port: " );
            portNum = Integer.parseInt(reader.readLine());

            // create and add node info to arraylist
            node = new NodeInfo( name, ip, portNum );
            activeParticipants.add(node);

            // construct sender and receiver thread objects
            Receiver receiver = new Receiver( node );
            Sender sender = new Sender( node );
            
            // run 2 threads
            receiver.start();
            sender.start();

        }
        catch( IOException exception ) {
            System.out.println( "Error at end of infinite loop" );
        }
    }

    public static void main(String[] args) throws IOException {
        // Creation of client object and starting it up
        NodeClient client = new NodeClient();
        client.start();
    }
}
