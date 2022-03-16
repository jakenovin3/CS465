public class TransactionClient {
    // DESCRIPTION:
        // IMPORTANT: The scenario the USE CASE runs through is the client running one transaction
        // For the project, client will be able to create a configurable number of threads, each one
        // running its own transaction

    // Client created that wants to initiate a transaction
    
    // METHODS:
    // createProxy() function:
    // creates proxy
    // calls openTransaction() from transaction proxy object
    // return void

    //------------------------------------------------------------
    // USE CASE:

    // 1) Initializes, creates proxy --> calls openTransaction() on proxy

    // 9) After proxy server comes back with transactionID, calls read()/write() on proxy server

    // 16) Client receives result sent from server proxy that came from TransactionManager
        // Calls closeTransaction() on proxy

}
