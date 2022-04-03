import java.util.ArrayList;
import java.util.HashMap;

public class Transaction {
    /*
    DESCRIPTION:
        Holds transaction ID and number.

    ------------------------------------------------------------
        METHODS:
            getID() function:
                returns transaction ID

            getNumber() function:
                returns transaction number

            setID() function:
                assigns ID param
                returns void

            setNumber() function:
                assign most recent transaction number
                returns void
     */
    ArrayList<Integer> readSet = new ArrayList<>();
    HashMap<Integer, Integer> writeSet = new HashMap<>();
    private int transID;
    private int transNum;
    private int lastcommit;

    public Transaction(int id) {
        transID = id;
    }

    public void setID( int newID ) {
        transID = newID;
    }
    public int getID() {
        return transID;
    }

    public void setNumber( int newNum ) {
        transNum = newNum;
    }

    public int getNumber() {
        return transNum;
    }

    /* Add account,balance(key,value) pair to writeSet and return account 
    *
    */
    public int write( int accountNumber, int balance ) {
        if( !writeSet.containsKey(accountNumber) ) {
            System.out.println("Transaction: Account not in write set.");
        }

        writeSet.put(accountNumber, balance);
        return accountNumber;
    }

    /* get account balance, add account to readSet, return balance
    *
    */
    public int read( int accountNumber ) {
        Integer balance = writeSet.get(accountNumber);
        if( balance == null ) {
        balance = TransactionServer.accountManager.read(accountNumber);
        }

        if( !readSet.contains(accountNumber) ) {
            readSet.add(accountNumber);
        }
        return balance;
    }

    public ArrayList<Integer> getReadSet() {
        return readSet;
    }

    public HashMap<Integer,Integer> getWriteSet() {
        return writeSet;
    }
}
