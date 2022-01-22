import org.w3c.dom.Node;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ClientInfoStatus;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeClient {
    private Message message;
    private static NodeInfo clientInfo;
    // each client needs to listen and have a server socket
    // the client should use two threads

    // Receiver class
    // Sender class extends Thread and implements MessageTypes/MessageEnumTypes ( whichever we choose to use )
    // use run
        // create two threads
        // receiver, listen for NOTEs, display to stdout
        // sender, accept user input to send to server

    public void run() {
        //
    }

    public NodeClient (String propertiesFile) {
        String clientName = null;
        String ip = null;
        int serverPort = -1;
        Properties properties = null;
        // get logical name
        System.out.println("Please enter name: ");
        Scanner inputScanner = new Scanner(System.in);
        clientName = inputScanner.nextLine();

        while( clientName.length() < 1 ) {
            System.out.println("Reenter name: ");
            clientName = inputScanner.nextLine();
        }

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

        // get server port
        try
        {
            serverPort = Integer.parseInt(properties.getProperty("SERVER_PORT"));
        }
        catch (NumberFormatException ex)
        {
            Logger.getLogger(NodeClient.class.getName()).log(Level.SEVERE, "Cannot read server port", ex);
            System.exit(1);
        }
        clientInfo = new NodeInfo(clientName, ip, serverPort);
    }
    // main
        // try properties file given args
        // start chat node
    public static void main(String[] args) throws IOException {

        String propertiesFile = null;
        ObjectOutputStream toServer = null;
        ObjectInputStream fromServer = null;
        try {
            propertiesFile = args[0];
        } catch (ArrayIndexOutOfBoundsException ex) {
            propertiesFile = "config/Server.properties";
        }

        (new NodeClient(propertiesFile)).run();
        Sender clientSender = new Sender(clientInfo);
        Receiver clientReceiver = new Receiver(clientInfo);
        // wait for join message
        String input = "";
        String serverOutput = "";
//        while( input != "join" ) {
//
//            input = System.console().readLine( "Enter message: ");
//        }

        Boolean running = true;
        // start loop - wait for message from Client
        while( running ) {

            // check for server messages
            try {
                serverOutput = fromServer.readUTF();
                System.out.println(serverOutput);
            }
            catch ( IOException ex ) {
            }

            input = System.console().readLine( "Enter message: " );

            // Server receives a message, check type, use scanner:
            switch ( input ) {
                case "join": // JOIN
                    toServer = new ObjectOutputStream( clientSender.getSocket().getOutputStream() );
                    fromServer = new ObjectInputStream( clientSender.getSocket().getInputStream() );
                    break;
                case "leave": // LEAVE
                    fromServer.close();
                    toServer.close();
                    break;
                // NOTE
                // cast content to String
                // send NOTE to all clients using NodeInfo
                case "shutdown": // SHUTDOWN
                // cast content to NodeInfo
                default: // Temp Note handling
                    toServer.writeBytes(input);
                    break;

            }
        }
    }

}
