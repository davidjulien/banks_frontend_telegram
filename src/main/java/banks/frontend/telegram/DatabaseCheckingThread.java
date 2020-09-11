package banks.frontend.telegram;

import banks.frontend.telegram.model.Transaction;

import java.util.ArrayList;
import java.time.LocalDateTime;

/**
 * DatabaseCheckingThread is a runnable process which verify if new transactions are available in database.
 */
public class DatabaseCheckingThread implements Runnable {
  private final ChatStateMachinesManager chatStateMachinesManager;
  private final Storage storage;

  private LocalDateTime lastChecking;

  public DatabaseCheckingThread(ChatStateMachinesManager chatStateMachinesManager, Storage storage) {
    this.chatStateMachinesManager = chatStateMachinesManager;
    this.storage = storage;
    this.lastChecking = LocalDateTime.of(2020,9,10,0,0,0);
  }

  public LocalDateTime lastChecking() {
    return this.lastChecking;
  }

  public void run() {
    System.out.println("[" + LocalDateTime.now() + "] Check database for new transactions");
    final LocalDateTime newLastChecking = LocalDateTime.now(); // Get current time before checking for new transactions
    final ArrayList<Transaction> transactions = this.storage.getNewTransactionsSince(this.lastChecking);

    if (transactions.isEmpty()) {
      // Nothing to do
    } else {
      final NewTransactionsEvent newTransactionsEvent = new NewTransactionsEvent(transactions);

      // Send NewTransactionsEvent to all ChatStateMachines
      ChatStateMachine[] chatStateMachines = this.chatStateMachinesManager.getAllChatStateMachines();
      for(ChatStateMachine chatStateMachine : chatStateMachines) {
        chatStateMachine.process(newTransactionsEvent);
      }
    }

    // Update lastChecking
    this.lastChecking = newLastChecking;
  }
}
