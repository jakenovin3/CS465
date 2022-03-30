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

     private int transID;
     private int transNum;

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

    
}
