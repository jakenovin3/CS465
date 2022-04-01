public class AccountManager {

    ArrayList<Account> accountList = new ArrayList<Account>();

    public void generateAccount( int accountNum ) {
        Account account = new Account();
        account.setAccountNum( accountNum );
        accountList.add( account );
    }

    public Account findAccount( int accountNum ) {
        Account foundAccount = null;
        for ( int curAcc = 0; curAcc < accountList.size(); curAcc++ ) {
            if ( accountList[curAcc].getAccountNum() == accountNum ) {
                foundAccount = accountList[curAcc];
            }
        }
        return foundAccount;
    }

    public Account read( int accountNum ) {
        Account account = findAccount( accountNum );
        return account;
    }

    public void write( int accountNum, int balChange ) {
        Account account = findAccount( accountNum );
        account.updateBalance( balChange ); // not sure how to validate this without knowing if transaction is a withdrawal or deposit
    }
}

/*
    DESCRIPTION:
        Manages Accounts that can be read and written to
        Here, it implements the "high-level" operations on individual accounts (read and write)

    ------------------------------------------------------------
    METHODS:
        read() function:
            given an account number, access select account and read balance of the account
            return value

        write() function:
            given an account number, access select account and send new balance to the account to update
            return void

    ------------------------------------------------------------
    USE CASE:
        ManagerWorker thread called either read() or write()

        12) In read()/write() functions, determines which Account using the account #

        13) Here, it issues the low-level read()/write() call on Account and returns the result to TransactionManagerWorker
     */


