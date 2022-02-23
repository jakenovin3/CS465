import org.w3c.dom.Node;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class NodeClient implements MessageTypes {
    private static String userInfo = "";
    public static ArrayList<NodeInfo> activeParticipants = new ArrayList<NodeInfo>();
    NodeInfo node;

    public void start() {
        // client should keep track of status on server
        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );

        try {
            // To Do: lets change this to take in a single input line and parse out the information
            // read input from user. Should be in format 'name,ip,port' ex. 'John,127.0.0.1,55555'
            System.out.println( "Provide user info: " );
            userInfo = reader.readLine();
            System.out.println( "Welcome! Processing information..." );
            // 0=name,1=ip,2=port
            String[] infoArray = userInfo.split(",");
            // create and add personal node info to arraylist
            node = new NodeInfo( infoArray[0], infoArray[1], Integer.parseInt( infoArray[2] ) );
            activeParticipants.add(node);

            // print user info
            System.out.println(
                    "Client information:\nName:" + infoArray[0]
                            + "\nIP:" + infoArray[1]
                            + "\nPort:" + infoArray[2]
            );
            // construct sender and receiver thread instances
            // receiver has server socket.
            System.out.println("Starting Receiver and Sender threads");
            Receiver receiver = new Receiver( node );
            // the sender connects deliberately because it has the connection info, can connect to someones receiver/serversocket

            // currently the sender constructor creates a- local to this client- arraylist of NodeInfo/connectivity info. Should we construct this here?
            // we should expect to need to join first meaning we should not proceed with establishing our knowledge of others in the mesh until this user joins the session
            Sender sender = new Sender( node );

            // run threads
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