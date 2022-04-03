import java.io.IOException;
import java.net.*;

public class TransactionServer {
    public static TransactionManager transactionManager = new TransactionManager();
    public static AccountManager accountManager = new AccountManager();
    public void run() {
        while( true ) {
            try {
                ServerSocket serverSocket = new ServerSocket(0);
                Socket proxyConnection;
                proxyConnection = serverSocket.accept();
                transactionManager.runTransaction(proxyConnection);
            }
            catch( SocketException SE){
                System.out.println("TransactionServer: Socket open exception, socket closed.");
            }
            catch (IOException IOE){
                System.out.println("TransactionServer: IOE server loop exception.");
            }
        }
        
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
