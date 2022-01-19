import org.w3c.dom.Node;

import java.io.IOException;
import java.sql.ClientInfoStatus;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeClient {
    private Message message;
    private NodeInfo clientInfo;
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

        try {
            propertiesFile = args[0];
        } catch (ArrayIndexOutOfBoundsException ex) {
            propertiesFile = "config/Server.properties";
        }

        (new NodeClient(propertiesFile)).run();
    }

}
