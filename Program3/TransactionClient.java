import java.io.*;
import java.net.*;
import java.util.*;

public class TransactionClient {

    String serverIP;
    int serverPort;
    int numAccounts;
    int accountBalance;

    // Concstructor obatining server and config information
    public TransactionClient() {

        try(InputStream input = new FileInputStream("Program3/Server.properties")){

            Properties prop = new Properties();
            prop.load(input);

            // Obtaining server details
            serverIP = prop.getProperty("SERVER_IP");
            serverPort = Integer.parseInt(prop.getProperty("SERVER_PORT"));
            numAccounts = Integer.parseInt(prop.getProperty("NUM_ACCOUNTS"));
            accountBalance = Integer.parseInt(prop.getProperty("ACCOUNT_BALANCE"));
        }
        catch(IOException IOE) {}
    }

    public void start() {

        try{
            TransactionServerProxy serverProxy = new TransactionServerProxy();
            Socket toProxy = new Socket(serverIP, serverPort);

            serverProxy.openTransaction(toProxy);

        }
        catch(IOException IOE){}
    }

    public void getProperties() {

        try(InputStream input = new FileInputStream("Program3/Server.properties")){
            Properties prop = new Properties();
            prop.load(input);

            serverIP = prop.getProperty("SERVER_IP");
            serverPort = Integer.parseInt(prop.getProperty("SERVER_PORT"));
            numAccounts = Integer.parseInt(prop.getProperty("NUM_ACCOUNTS"));
            accountBalance = Integer.parseInt(prop.getProperty("ACCOUNT_BALANCE"));
        }
        catch(IOException IOE) {}
    }

    public static void main(String[] args) {
        String serverIP;
        int serverPort;
        int numAccounts;
        int accountBalance;

        // Obtain configuration information and display
        try(InputStream input = new FileInputStream("Program3/Server.properties")){
            Properties prop = new Properties();
            prop.load(input);

            serverIP = prop.getProperty("SERVER_IP");
            serverPort = Integer.parseInt(prop.getProperty("SERVER_PORT"));
            numAccounts = Integer.parseInt(prop.getProperty("NUM_ACCOUNTS"));
            accountBalance = Integer.parseInt(prop.getProperty("ACCOUNT_BALANCE"));

            System.out.println("================================");
            System.out.println("TRANSACTION SERVER CONFIGURATION\n");
            System.out.println("Server IP: " + serverIP);
            System.out.println("Server Port: " + serverPort);
            System.out.println("Number of Accounts: " + numAccounts);
            System.out.println("Account Balance: " + accountBalance);
            System.out.println("================================");
        }
        catch(IOException IOE) {}

        TransactionClient client = new TransactionClient();
        client.start();
    }

    // When this calls read()/write() on proxy server, does it randomly select the operation to do?

}

/*
    DESCRIPTION:
        IMPORTANT: The scenario the USE CASE runs through is the client running one transaction
            For the project, client will be able to create a configurable number of threads, each one
            running its own transaction
        Client created that wants to initiate a transaction

    ------------------------------------------------------------
    METHODS:
        createProxy() function:
            creates proxy
            calls openTransaction() from transaction proxy object
            return void

    ------------------------------------------------------------
    USE CASE:

        1) Initializes, creates proxy --> calls openTransaction() on proxy

        9) After proxy server comes back with transactionID, calls read()/write() on proxy server

        16) Client receives result sent from server proxy that came from TransactionManager
            Calls closeTransaction() on proxy
     */
