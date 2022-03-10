public class TransactionServer {
    // DESCRIPTION:

    // Runs server loop
        // Server socket --> accept()

    //------------------------------------------------------------
    // USE CASE:

    // 4) Wakes up from the proxy server's sent OPEN_TRANSACTION message, returning a Socket object
        // Calls runTransaction() on TransactionManager

    // Receives message of either type READ_REQUEST or WRITE_REQUEST
        // Calls runTransaction() on TransactionManager
}
