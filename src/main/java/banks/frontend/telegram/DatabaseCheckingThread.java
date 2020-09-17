package banks.frontend.telegram;

import banks.frontend.telegram.model.Transaction;

import java.util.ArrayList;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * DatabaseCheckingThread is a runnable process which verify if new transactions are available in database.
 */
public class DatabaseCheckingThread implements Runnable {
  private final ChatStateMachinesManager chatStateMachinesManager;
  private final Storage storage;

  private OffsetDateTime lastChecking;

  public DatabaseCheckingThread(ChatStateMachinesManager chatStateMachinesManager, Storage storage) {
    this.chatStateMachinesManager = chatStateMachinesManager;
    this.storage = storage;
    this.lastChecking = OffsetDateTime.of(LocalDate.now(), java.time.LocalTime.of(0,0), ZoneOffset.UTC);
  }

  public OffsetDateTime lastChecking() {
    return this.lastChecking;
  }

  public void run() {
    System.out.println("[" + OffsetDateTime.now(ZoneOffset.UTC) + "] Check database for new transactions");
    final OffsetDateTime newLastChecking = OffsetDateTime.now(ZoneOffset.UTC); // Get current time before checking for new transactions
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
