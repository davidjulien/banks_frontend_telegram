package banks.frontend.telegram;

import banks.frontend.telegram.model.Transaction;

import java.util.ArrayList;

public class NewTransactionsEvent {
  private ArrayList<Transaction> transactions;

  public NewTransactionsEvent(ArrayList<Transaction> transactions) {
    this.transactions = transactions;
  }

  public ArrayList<Transaction> getTransactions() {
    return this.transactions;
  }
}
