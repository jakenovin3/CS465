import java.io.*;
import java.util.*;

public class TransactionClient extends Thread {

    String serverIP;
    int serverPort;
    int numAccounts;
    int accountBalance;
    int numTransactions;

    ArrayList<Transaction> transactionList = new ArrayList<>();

    // Constructor obtaining server and config information
    public TransactionClient() {

        try(InputStream input = new FileInputStream("Program3/Server.properties")){

            Properties prop = new Properties();
            prop.load(input);

            // Obtaining server details
            serverIP = prop.getProperty("SERVER_IP");
            serverPort = Integer.parseInt(prop.getProperty("SERVER_PORT"));
            numTransactions = Integer.parseInt(prop.getProperty("NUM_TRANSACTIONS"));
            numAccounts = Integer.parseInt(prop.getProperty("NUM_ACCOUNTS"));
            accountBalance = Integer.parseInt(prop.getProperty("ACCOUNT_BALANCE"));
        }
        catch(IOException IOE) {}
    }

    public void run() {

        Thread newTransactionThread;
        int transID;
        for(transID = 1; transID <= numTransactions; transID++) {
            Transaction newTransaction = new Transaction(transID);
            transactionList.add(newTransaction);

            newTransactionThread = new TransactionThread(transID);
            newTransactionThread.start();
        }
    }

    // These are the actual accounts created and transactions that are going to occur
    public class TransactionThread extends Thread {

        int transID;

        public TransactionThread(int id) {
            this.transID = id;
        }

        public void run() {

            int senderAccountID, receiverAccountID;
            int amountToTransfer;
            int accountBalance;
            int maxTransferAmount = 50; // Change to set the limit on money sent
            int transactionStatus;

            // Setting up which accounts will be sending and receiving money
            senderAccountID = (int) (Math.random() * numAccounts);
            receiverAccountID = (int) (Math.random() * numAccounts);
            amountToTransfer = (int) (Math.random() * maxTransferAmount);

            // If the same account is chosen at random, choose new account until different
            if(senderAccountID == receiverAccountID) {
                while(senderAccountID == receiverAccountID) {
                    receiverAccountID = (int) (Math.random() * 10);
                }
            }

            // Allows the individual Thread's prints to not overlap
            try {
                Thread.sleep((int) Math.floor(Math.random() * 3250));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            TransactionServerProxy transactionProxy = new TransactionServerProxy(serverIP, serverPort);

            transID = transactionProxy.openTransaction();
            if(transID == 1) {
                System.out.println("Transaction #" + transID + " started, transfer $" + amountToTransfer + ": " + senderAccountID + "->" + receiverAccountID);
            }
            else {
                System.out.println("TransactionClient: Failed openTransaction()");
            }

            // This is the withdrawal of money from the sender's account
            accountBalance = transactionProxy.read(senderAccountID);
            transactionProxy.write(senderAccountID, accountBalance - amountToTransfer);

            // This is the deposit of money into the receiver's account
            accountBalance = transactionProxy.read(receiverAccountID);
            transactionProxy.write(receiverAccountID, accountBalance + amountToTransfer);

            // Closes transaction and tells if it was a success or not
            transactionStatus = transactionProxy.closeTransaction();
            if(transactionStatus == 1) {
                System.out.println("Transaction #" + transID + " COMMITTED");
            }
            else {
                System.out.println("Transaction #" + transID + " ABORTED");
            }

        }

    }

    public static void main(String[] args) {
        String serverIP;
        int serverPort;
        int numAccounts;
        int accountBalance;
        int numTransactions;

        // Obtain configuration information and display
        try(InputStream input = new FileInputStream("Program3/Server.properties")){
            Properties prop = new Properties();
            prop.load(input);

            serverIP = prop.getProperty("SERVER_IP");
            serverPort = Integer.parseInt(prop.getProperty("SERVER_PORT"));
            numTransactions = Integer.parseInt(prop.getProperty("NUM_TRANSACTIONS"));
            numAccounts = Integer.parseInt(prop.getProperty("NUM_ACCOUNTS"));
            accountBalance = Integer.parseInt(prop.getProperty("ACCOUNT_BALANCE"));

            System.out.println("================================");
            System.out.println("TRANSACTION SERVER CONFIGURATION\n");
            System.out.println("Server IP: " + serverIP);
            System.out.println("Server Port: " + serverPort);
            System.out.println("Number of Transactions: " + numTransactions);
            System.out.println("Number of Accounts: " + numAccounts);
            System.out.println("Account Balance: " + accountBalance);
            System.out.println("================================\n");
        }
        catch(IOException IOE) {}

        TransactionClient client = new TransactionClient();
        client.start();
    }

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
