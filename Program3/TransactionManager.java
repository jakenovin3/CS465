public class TransactionManager extends Thread{
    // DESCRIPTION:

    // Manages transaction's id/# and logging information

    // This is the container for transactions:
        // Keeps list of active transactions

    // Has method (called in server loop) that takes socket to client, handing it
    // off to new starter 'TransactionManagerWorker' threads
        // TransactionManagerWorker threads:
            // Runs loop that reads messages coming from client ans translates them into high-level actions

    // Implement write() function
    // Implement validate() function

    //------------------------------------------------------------
    // USE CASE:

    // 5) Spawns off a TransactionManagerWorker thread
        // Enters its run() method that keeps receiving/processing messages (large switch statement)
    // 6) Determines that the transaction needs to be opened
        // Creation of Transaction object
        // Assigns an id and # to Transaction object
        // Stores reference into list of transactions held by Manager
            // ArrayList<Transaction> transactionsList = new ArrayList<Transaction>
    // 7) Sends the transactionID back to proxy server

    // 11) Receives message of either type READ_REQUEST or WRITE_REQUEST from server
    // Determines account that needs to be read/written to
    // Calls read()/write() on the Transaction object

    // 14) After AccountManager returns result of their read()/write() operation to here
    // Creates a message containing result and sends it back to proxy

    // 18) Received CLOSE_TRANSACTION message from proxy
    // Determines the Transaction that needs to be closed
    // Runs validate():
        // If conflict, abort
        // If no conflict, writeTransaction() --> AccountManager

    // 19) Removes Transaction from transactionList arraylist
    // Sends message of type TRANSACTION_COMMITTED back to client
        // Also, closes network connections
        // Leaves loop and returns from run()
}
