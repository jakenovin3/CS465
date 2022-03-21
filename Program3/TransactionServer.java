import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

public class TransactionServer {
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
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(55555);
            Socket clientConnection;
            while( true ) {
                clientConnection = serverSocket.accept();
                ObjectInputStream fromClient = new ObjectInputStream(clientConnection.getInputStream());
                int receivedMessage = (int) fromClient.readObject();
                switch (receivedMessage) {
                    case 0: // open transaction: call runTransaction on TransactionManager Object
                        TransactionManager TMThread = new TransactionManager();
                        TMThread.runTransaction( receivedMessage );
                        break;

                    case 1:
                        break;

                    case 2:
                        break;
                    
                    case 3:
                        break;

                    case 4:

                        break;
                
                    default:
                        break;
                }
            }
        }
        catch (IOException IOE){}
        catch (ClassNotFoundException CNFE){}
    }
}
