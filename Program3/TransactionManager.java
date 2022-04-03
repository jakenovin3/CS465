import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.cert.TrustAnchor;
import java.io.IOException;

public class TransactionManager{
    private HashMap<Integer, Transaction> committedTransactions = new HashMap<>();
    private ArrayList<Transaction> abortedTransactions = new ArrayList<>();
    private ArrayList<Transaction> runningTransactions = new ArrayList<>();
    private int idCounter = 0;
    private int numCounter = 0;
    /*
        Identifies the account the request is for (via AccountManager methods).
        Gets information based on incoming transaction (how much money can be
        transferred on request) returns the info for the account in question
    */

    /*
    Method: 1) Receives socket from client
            2) Creates worker thread
            3) Starts worker thread to handle transaction
    */
    public void runTransaction( Socket client) {
        try {
            ObjectInputStream fromClient = new ObjectInputStream(client.getInputStream());
            int receivedMessage = (int) fromClient.readObject();
            TransactionManagerWorker transactionWorker = new TransactionManagerWorker( receivedMessage, client ); // receive client port
            transactionWorker.start();
        }
        catch (IOException IOE){
            System.out.println("TransactionManager: IOE");
        }
        catch (ClassNotFoundException CNFE) {
            System.out.println("TransactionManager: CNFE");
        }
    }

    public ArrayList<Transaction> getAbortedTransactions() {
        return abortedTransactions;
    }

    /* For each transaction in transactionMap
    *  current transaction > comparedTo ||
    *  if balance is negative, return false?
    */
    public boolean validateTransaction(Transaction transaction) {
        // compare the read to write
        for( Integer readEntry : transaction.getReadSet() ) {
            for( Transaction currTransaction : runningTransactions ) {
                if( transaction.getNumber() == currTransaction.getNumber() - 1
                    || currTransaction.getWriteSet().containsKey(readEntry) ) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /* Write write set of transaction to account
    */
    public void writeTransaction( Transaction transaction ) {
        HashMap<Integer, Integer> writeSet = transaction.getWriteSet();
        int balance;
        int accountNum;

        for ( Map.Entry<Integer,Integer> entry : writeSet.entrySet()) {
            accountNum = entry.getKey();
            balance = entry.getValue();
            TransactionServer.accountManager.write(accountNum, balance);
            System.out.println("Transaction " + transaction.getID() + ": writing transaction.");
        }
    }


    // ================================================= Inner Worker Class =======================================================
    public class TransactionManagerWorker extends Thread
    {
        private int message = -1; // message type
        int nextAccount = 0;
        private int balance = 0;   // current balance change?
        private Socket client;
        Transaction transaction = null;
        public TransactionManagerWorker( int receivedMessage, Socket client ) {
            message = receivedMessage;
            this.client = client;
        }

        public void run() {
            boolean continueRun = true;
            while( continueRun ) {
                switch (message) {
                    case 0: //open transaction, should always be first message received
                        transaction = new Transaction(idCounter);
                        transaction.setNumber(numCounter++);
                        runningTransactions.add(transaction);
                        System.out.println("Transaction ID: " + Integer.toString(transaction.getID()) + " opened");
                        break;

                    case 1: // close transaction
                        // enter validation phase
                        try{
                            ObjectOutputStream toClient = new ObjectOutputStream(client.getOutputStream());
                            if(validateTransaction(transaction)) {
                                // commit transaction
                                writeTransaction(transaction);
                                committedTransactions.put(transaction.getID(), transaction);
                                runningTransactions.remove(transaction);
                                toClient.writeObject(TransactionServer.messages.getCommitTrans());
                            }
                            else {
                                // add transaction to aborted transaction list
                                abortedTransactions.add(transaction);
                                toClient.writeObject(TransactionServer.messages.getAbortTrans());
                            }
                            continueRun = false;
                            client.close(); // close socket to client
                            toClient.close();
                        }
                        catch(IOException IOE) {
                            System.out.println("TransactionManagerWorker: IO exception.");
                        }
                        break;

                    case 4: // transaction read
                        try {
                            ObjectOutputStream toClient = new ObjectOutputStream(client.getOutputStream());
                            toClient.writeObject(TransactionServer.messages.getReadResp()); // read response to client
                            
                            ObjectInputStream fromClient = new ObjectInputStream(client.getInputStream());
                            nextAccount = (int) fromClient.readObject();
                            toClient.writeObject(TransactionServer.accountManager.read(nextAccount));

                            toClient.close();
                            fromClient.close();
                        }
                        catch(SocketException SE) {
                            System.out.println("TransactionManagerWorker: Socket object in/out stream exception in write attempt.");
                        }
                        catch (ClassNotFoundException CNFE) {
                            System.out.println("TransactionManagerWorker: CNFE class not found in write attempt.");
                        }
                        catch(IOException IOE) {
                            System.out.println("TransactionManagerWorker: IO exception in write attempt.");
                        }
                        break;

                    case 5: // transaction write
                        // accept write request, get balance from client
                        try {
                            ObjectOutputStream toClient = new ObjectOutputStream(client.getOutputStream());
                            toClient.writeObject(TransactionServer.messages.getWriteResp()); // write response to client
                            toClient.close();
                            ObjectInputStream fromClient = new ObjectInputStream(client.getInputStream());
                            nextAccount = (int) fromClient.readObject();
                            balance = (int) fromClient.readObject();
                            fromClient.close();
                        }
                        catch(SocketException SE) {
                            System.out.println("TransactionManagerWorker: Socket object in/out stream exception in write attempt.");
                        }
                        catch (ClassNotFoundException CNFE) {
                            System.out.println("TransactionManagerWorker: CNFE class not found in write attempt.");
                        }
                        catch(IOException IOE) {
                            System.out.println("TransactionManagerWorker: IO exception in write attempt.");
                        }
                        transaction.write(nextAccount, balance );
                        break;

                    default: // erroneous client message?
                        break;
                }
                // Wait for incoming messages from client thread (ObjectInputStream)
                // get message from client
                try {
                    ObjectInputStream fromClient = new ObjectInputStream(client.getInputStream());
                    message = (int) fromClient.readObject();
                    fromClient.close();
                }
                catch(IOException IOE) {
                    System.out.println("TransactionManagerWorker: IO exception.");
                }
                catch (ClassNotFoundException CNFE) {
                    System.out.println("TransactionManagerWorker: CNFE class not found in client message request check.");
                }
            }
        }
    } // End of Inner Class ======================================================================
}

/*
    DESCRIPTION:
        Manages transaction's id/# and logging information
        This is the container for transactions:
            Keeps list of active transactions
        Has method (called in server loop) that takes socket to client, handing it
        off to new starter 'TransactionManagerWorker' threads
            TransactionManagerWorker threads:
                Runs loop that reads messages coming from client and translates them into high-level actions

    ------------------------------------------------------------
    METHODS:
        read() function:
            Identifies the account the request is for (via AccountManager methods)
            Gets information based on incoming transaction (how much money can be transferred on request)
            returns the info for the account in question

        write() function:
            Identifies the account the request is for (via AccountManager methods)
            Modifies user information based on incoming transaction (adds or removes some amount of dollars)
            returns the new info for the account in question after the transaction is complete

        validate() function:
            Method to maintain atomicity
                Used so no two transactions can occur at the same time
                Arbitrarily assigns order for two (or more) incoming transactions asking to occur at the same time

        writeTransaction() function:
            under condition that validate passes, commit write request, invoke AccountManager write method to update account

        openTransaction() function:
            send transaction id of opened transaction to proxy
            return id

        closeTransaction() function:
            remove transaction from container/list
            send message back to client (commit or abort)
            close worker thread loop

        runTransaction() function:
            takes in socket, creates worker thread, runs loop that reads and processes messages from client.
            Uses message to determine action:
            (OPEN_TRANSACTION, READ_REQUEST, WRITE_REQUEST, CLOSE_TRANSACTION)
            For action, create transaction object, assign ID and number to transaction, store transaction in transaction list
            return void

    ------------------------------------------------------------
    USE CASE:

        5) Spawns off a TransactionManagerWorker thread
            Enters its run() method that keeps receiving/processing messages (large switch statement)

        6) Determines that the transaction needs to be opened
            Creation of Transaction object
            Assigns an id and # to Transaction object
            Stores reference into list of transactions held by Manager
                ArrayList<Transaction> transactionsList = new ArrayList<Transaction>

        7) Sends the transactionID back to proxy server

        11) Receives message of either type READ_REQUEST or WRITE_REQUEST from server
            Determines account that needs to be read/written to
            Calls read()/write() on the Transaction object

        14) After AccountManager returns result of their read()/write() operation to here
            Creates a message containing result and sends it back to proxy

        18) Received CLOSE_TRANSACTION message from proxy
            Determines the Transaction that needs to be closed
            Runs validate():
                If conflict, abort
                If no conflict, writeTransaction() --> AccountManager

        19) Removes Transaction from transactionList arraylist
            Sends message of type TRANSACTION_COMMITTED back to client
                Also, closes network connections
                Leaves loop and returns from run()
     */
