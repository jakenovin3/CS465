import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

public class TransactionServer {
    TransactionManager transactionManager = new TransactionManager();

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(55555);
            Socket proxyConnection;

            while( true ) {
                proxyConnection = serverSocket.accept();
                ObjectInputStream fromClient = new ObjectInputStream(proxyConnection.getInputStream());
                int receivedMessage = (int) fromClient.readObject();

                transactionManager.runTransaction(receivedMessage);
            }
        }
        catch (IOException IOE){
            System.out.println("TransactionServer: IOE");
        }
        catch (ClassNotFoundException CNFE){
            System.out.println("TransactionServer: CNFE");
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
