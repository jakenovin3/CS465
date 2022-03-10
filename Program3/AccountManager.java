public class AccountManager {
    // DESCRIPTION:

    // Manages Accounts that can be read and written to

    // Here, it implements the "high-level" operations on individual accounts (read and write)

    //------------------------------------------------------------
    // USE CASE:

    // ManagerWorker thread called either read() or write()

    // 12) In read()/write() functions, determines which Account using the account #

    // 13) Here, it issues the low-level read()/write() call on Account and returns the result to TransactionManagerWorker
}


