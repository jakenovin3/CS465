import java.util.HashMap;

public class TransactionManager{
    // key = transaction ID
    HashMap<Integer, Transaction> transactionMap = new HashMap<>();
    // key = transaction number?
    HashMap<Integer, Transaction> readyToValidate = new HashMap<>();
    private int idCounter = 0;
    /*
        Identifies the account the request is for (via AccountManager methods).
        Gets information based on incoming transaction (how much money can be
        transferred on request) returns the info for the account in question
    */

    /*
    Method: 1) Receives message type ID
            2) Creates worker thread
            3) Starts worker thread to handle transaction
    */
    public void runTransaction() {
        TransactionManagerWorker transactionWorker = new TransactionManagerWorker();
        transactionWorker.start();
    }

    public class TransactionManagerWorker extends Thread
    {
        private int message = -1;
        private int update = 0;

        public TransactionManagerWorker() {

        }

        // Function used to get updates from proxy
        public void receiveMessage( int newMessage, int content )
        {
            message = newMessage;
            update = content;
        }

        // If the validated transaction does not exist or a transaction to be validated
        // is ahead of this transaction, then return false otherwise is valid
        public boolean validate(Transaction nextTransaction)
        {
            if (nextTransaction == null
                || readyToValidate.get(nextTransaction.getNumber()-1) != null)
            {
                return false;
            }
            return true;
        }

        public void run() {
            int lastValidated = 0;
            while( true ) {
                switch (message) {
                    case 0: //open transaction
                        Transaction transaction = new Transaction(idCounter);
                        transactionMap.putIfAbsent(idCounter++, transaction);
                        System.out.println("Transaction ID: " + Integer.toString(idCounter) + " opened");
                        break;

                    case 1: // close transaction
                        // Determines the Transaction that needs to be closed
                        if(validate(readyToValidate.get(lastValidated))) // does it make sense to 
                        {
                            transactionMap.remove(lastValidated);
                        }
                        else
                        {
                            // undo and abort
                        }
                        break;

                    case 2: // transaction committed
                        break;

                    case 3: // transaction read
                        // call account read function
                        // send result back to proxy
                        break;

                    case 4: // transaction write                        
                        AccountManager.write(lastValidated, update );
                        // send result back to proxy
                        break;

                    default: // erroneous entry message?
                        break;
                }
            }
        }
    } // End of Inner Class
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
