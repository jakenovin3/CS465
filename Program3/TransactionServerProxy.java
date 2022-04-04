import java.io.*;
import java.net.*;

public class TransactionServerProxy extends Thread {

    String serverIP;
    int serverPort;
    int numAccounts;
    int accountBalance;

    Message transactionMessage = new Message();
    Socket serverConnection;
    ObjectInputStream fromServer;
    ObjectOutputStream toServer;

    public TransactionServerProxy(String serverIP, int serverPort) {

        // Create a socket for connecting to the server
        try {
            serverConnection = new Socket(serverIP, serverPort);
        }
        catch (IOException IOE) {
            System.out.println("TransactionServerProxy: Error opening socket to server");
        }

    }

    public int openTransaction() {
        try{
            // Gets message type and writes it to the server socket
            int openMessage = transactionMessage.getOpenTrans();
            toServer = new ObjectOutputStream(serverConnection.getOutputStream());
            toServer.writeObject(openMessage);
            return 1;
        }
        catch(IOException IOE) {
            System.out.println("TransactionServerProxy: Error sending Open message to Server");
            return 0;
        }
    }

    // Returns a value representing if the transaction committed or aborted (TRANSACTION_COMMITTED OR TRANSACTION_ABORTED)
    public int closeTransaction() {
        try{
            int closeMessage = transactionMessage.getClosedTrans();
            toServer = new ObjectOutputStream(serverConnection.getOutputStream());
            toServer.writeObject(closeMessage);
            return 1;
        }
        catch(IOException IOE) {
            System.out.println("TransactionServerProxy: Error sending Close message to Server");
            return 0;
        }
    }

    public int read(int accountNumber) {

        try{
            int readMessage = transactionMessage.getReadReq();
            toServer = new ObjectOutputStream(serverConnection.getOutputStream());
            toServer.writeObject(readMessage);
            return 1;
        }
        catch(IOException IOE) {
            System.out.println("TransactionServerProxy: Error sending read message to Server");
            return 0;
        }
    }

    public int write(int accountNumber, int newBalance) {

        int newAccountBalance = newBalance;

        try{
            int writeMessage = transactionMessage.getWriteReq();
            toServer = new ObjectOutputStream(serverConnection.getOutputStream());
            toServer.writeObject(writeMessage);
            return 1;
        }
        catch(IOException IOE) {
            System.out.println("TransactionServerProxy: Error sending write message to Server");
            return 0;
        }

    }

}

/*
    DESCRIPTION:
        Abstracted server that the client interacts with
        Contains usable API
        Implements higher level functions

    ------------------------------------------------------------
    METHODS:
        openTransaction() function:
            Use passed in socket to open connection with transaction server.
            Create an OPEN_TRANSACTION message and send message to transaction server.
            connection is kept open.

        closeTransaction() function:
            Create message CLOSE_TRANSACTION and send to server on same connection opened.
            Once a message is received it should be either TRANSACTION_COMMITTED or TRANSACTION_ABORTED.
            Close connection and return

        read() function:
            create READ_REQUEST MESSAGE and send message to transaction server on open connection.

        write() function:
            create WRITE_REQUEST MESSAGE and send message to transaction server on open connection.

    ------------------------------------------------------------
    USE CASE:

        2) Creates a message of type OPEN_TRANSACTION

        3) Opens connection to transaction server and sends message (leaving the connection open)

        8) Coming back from the TransactionManager:
            Returns from openTransaction() call and provides transactionID to TransactionClient

        10) After client calls read()/write() on proxy:
            Creates message of type READ_REQUEST or WRITE_REQUEST
            Sends message to the server to its open server loop connection

        15) After receiving message sent from TransactionManager
            Reads message and returns result back to the client

        17) Client has called closeTransaction() proxy:
            Creates type message CLOSE_TRANSACTION
            Sends message to the server's open connection

        20) Receives the TRANSACTION_COMMITTED message from the TransactionManager
            returns from closeTransaction()
     */
