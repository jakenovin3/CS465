public class Account {

    private int accountNum = 0; // needs to be randomly generated at the start
    private int balance = 0;

    public int getAccountNum() { return accountNum; }
    public int getBalance() { return balance; }
    public void setAccountNum( int newAccountNum ) { accountNum = newAccountNum; }
    public void updateBalance( int newBalance ) { balance = newBalance; }

    /*
    DESCRIPTION:
        Each account holds a balance and account number. Initial balance in the accounts are randomly generated.

    ------------------------------------------------------------
    METHODS:
        getAccountNum() function:
            returns account number
    
        getBalance() function:
            returns balance
    
        updateBalance( new_balance ) function:
            new balance from account manager write transaction replaces existing balance
            returns void
     */
}
