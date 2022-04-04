import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Properties;

public class TransactionServer {

    String serverIP;
    int serverPort;
    int numAccounts;
    int accountBalance;
    int numTransactions;

    public static TransactionManager transactionManager;
    public static AccountManager accountManager;
    public static Message messages = new Message();
    ServerSocket serverSocket;

    public TransactionServer(String ip, int port) {
        transactionManager = new TransactionManager();
        System.out.println("[TransactionServer.TransactionServer] TransactionManager created.");
        accountManager = new AccountManager();
        System.out.println("[TransactionServer.TransactionServer] AccountManager created.");
        try {
        serverSocket = new ServerSocket(port);
        }
        catch (IOException IOE) {
            System.out.println("TransactionServer: IOE server loop exception.");
        }
        System.out.println("[TransactionServer.TransactionServer] ServerSocket created.");
        serverIP = ip;
        serverPort = port;
    }


    public void run() {
        while( true ) {
            try {
                transactionManager.runTransaction(serverSocket.accept());
            }
            catch( SocketException SE){
                // System.out.println("TransactionServer: Socket open exception, socket closed.");
            }
            catch (IOException IOE){
                // System.out.println("TransactionServer: IOE server loop exception.");
            }
        }
        
    }

    public static void main(String[] args) {

        String serverIP;
        int serverPort;

        // Obtain configuration information and display
        try(InputStream input = new FileInputStream("Program3/Server.properties")){
            Properties prop = new Properties();
            prop.load(input);

            serverIP = prop.getProperty("SERVER_IP");
            serverPort = Integer.parseInt(prop.getProperty("SERVER_PORT"));

            TransactionServer transactionServer = new TransactionServer(serverIP, serverPort);
            System.out.println("Transaction Server running...");
            transactionServer.run();
        }
        catch(IOException IOE) {}

    }
}

/*
    DESCRIPTION:
        Stores and manages database accounts using AccountManager and TransactionManager objects.
        Runs server loop
            Server socket --> accept()
        Passes socket to TransactionManager

    ------------------------------------------------------------
    USE CASE:

        4) Wakes up from the proxy server's sent OPEN_TRANSACTION message, returning a Socket object
            Calls runTransaction() on TransactionManager

        Receives message of either type READ_REQUEST or WRITE_REQUEST
            Calls runTransaction() on TransactionManager
     */
