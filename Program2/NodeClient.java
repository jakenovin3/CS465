import java.io.*;
import java.net.*;
import java.util.*;

public class NodeClient implements MessageTypes {
    private static String userInfo = "";
    public static ArrayList<NodeInfo> activeParticipants = new ArrayList<NodeInfo>();
    NodeInfo node;

    public void start() {
        // client should keep track of status on server
        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );

        try {
            // read input from user. Should be in format 'name,ip,port' ex. 'John,127.0.0.1,55555'
            System.out.println( "Provide user info: " );
            userInfo = reader.readLine();
            System.out.println( "Welcome! Processing information..." );
            // 0=name,1=ip,2=port
            String[] infoArray = userInfo.split(",");
            // create and add personal node info to arraylist
            node = new NodeInfo( infoArray[0], infoArray[1], Integer.parseInt( infoArray[2] ) );
            // print user info
            System.out.println(
                                "Client information:\nName:" + infoArray[0]
                                + "\nIP:" + infoArray[1]
                                + "\nPort:" + infoArray[2]
                                );
            // construct sender and receiver thread instances
            // receiver has server socket.
            System.out.println("Starting Receiver and Sender threads");
            Sender sender = new Sender( node );
            sender.start();
            ServerSocket receivingServerSocket = new ServerSocket( node.getPort() );
            Receiver receiver = new Receiver( node, receivingServerSocket.accept() );
            receiver.start();
            while( true ) {
                // check for updates from receiver thread
                ArrayList<NodeInfo> updatedParticipants = new ArrayList<NodeInfo>( receiver.getUpdate() );
                if( updatedParticipants.size() > 1 && updatedParticipants.size() > activeParticipants.size() ) {
                    
                    System.out.println("Current Session Participants: ");
                    for( NodeInfo participant : activeParticipants ) {
                        System.out.println(participant.getName());
                    }
                    activeParticipants.clear();
                    activeParticipants.addAll( updatedParticipants );
                    sender.updateParticipants( activeParticipants );
                    System.out.println("Updated Session Participants: ");
                    for( NodeInfo participant : activeParticipants ) {
                        System.out.println(participant.getName());
                    }
                }
            }

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
