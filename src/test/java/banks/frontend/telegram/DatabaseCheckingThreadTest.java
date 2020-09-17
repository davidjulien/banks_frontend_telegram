package banks.frontend.telegram;

import banks.frontend.telegram.model.Transaction;

import java.util.ArrayList;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.LocalDate;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;


import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Unit tests for DatabaseCheckingThread
 */
public class DatabaseCheckingThreadTest {
  private static final String BOT_TOKEN = "fake_bot_token";
  private static final String SECURITY_CODE = "security_code";
  private static final long CHAT_ID_1 = 1;
  private static final long CHAT_ID_2 = 2;

  class StorageMock extends Storage {
    private ArrayList<Transaction> transactions;

    public StorageMock(ArrayList<Transaction> transactions) {
      super(null);
      this.transactions = transactions;
    }

    public ArrayList<Transaction> getNewTransactionsSince(OffsetDateTime lastChecking) {
      return this.transactions;
    }
  }

  @Test 
  public void shouldCallAllChatStateMachines() {
    final Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    final TelegramBot bot = new TelegramBot(BOT_TOKEN);
    final ChatStateMachinesManagerMock chatStateMachinesManagerMock = new ChatStateMachinesManagerMock(configuration, bot);
    final ArrayList<Transaction> newTransactionsArrayList = new ArrayList<Transaction>();
    final Transaction fakeTransaction = new Transaction(1, "ing", "client", "acccount", OffsetDateTime.now(ZoneOffset.UTC), "transaction", LocalDate.now(), LocalDate.now(), 123.45, "descriiption", Transaction.TransactionType.SEPA_DEBIT);
    newTransactionsArrayList.add(fakeTransaction);
    final StorageMock storageMock = new StorageMock(newTransactionsArrayList);

    assertNotNull(chatStateMachinesManagerMock);
    DatabaseCheckingThread databaseCheckingThread = new DatabaseCheckingThread(chatStateMachinesManagerMock, storageMock);

    // Create two ChatStateMachines
    ChatStateMachineMock chatStateMachineMock1 = (ChatStateMachineMock)chatStateMachinesManagerMock.getOrCreateChatStateMachine(CHAT_ID_1);
    ChatStateMachineMock chatStateMachineMock2 = (ChatStateMachineMock)chatStateMachinesManagerMock.getOrCreateChatStateMachine(CHAT_ID_2);

    // Run DatabaseCheckingThread
    final OffsetDateTime offsetDateTimeBeforeRun = OffsetDateTime.now(ZoneOffset.UTC);
    assertNotNull(databaseCheckingThread);
    assertTrue(databaseCheckingThread.lastChecking().isBefore(offsetDateTimeBeforeRun));
    databaseCheckingThread.run();

    // Verify that ChatStateMachines process method have been called with newTransactionsArrayList
    assertEquals(1, chatStateMachineMock1.processNewTransactionsEventCalls.size());
    assertEquals(newTransactionsArrayList, chatStateMachineMock1.processNewTransactionsEventCalls.get(0).getTransactions());

    assertEquals(1, chatStateMachineMock2.processNewTransactionsEventCalls.size());
    assertEquals(newTransactionsArrayList, chatStateMachineMock2.processNewTransactionsEventCalls.get(0).getTransactions());

    // Verify lastChecking update
    assertTrue(databaseCheckingThread.lastChecking().isAfter(offsetDateTimeBeforeRun));
  }

  @Test 
  public void shouldNotCallAllChatStateMachinesWhenThereIsNoTransactions() {
    final Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    final TelegramBot bot = new TelegramBot(BOT_TOKEN);
    final ChatStateMachinesManagerMock chatStateMachinesManagerMock = new ChatStateMachinesManagerMock(configuration, bot);
    final ArrayList<Transaction> newTransactionsArrayList = new ArrayList<Transaction>();
    final StorageMock storageMock = new StorageMock(newTransactionsArrayList);

    assertNotNull(chatStateMachinesManagerMock);
    DatabaseCheckingThread databaseCheckingThread = new DatabaseCheckingThread(chatStateMachinesManagerMock, storageMock);

    // Create two ChatStateMachines
    ChatStateMachineMock chatStateMachineMock1 = (ChatStateMachineMock)chatStateMachinesManagerMock.getOrCreateChatStateMachine(CHAT_ID_1);
    ChatStateMachineMock chatStateMachineMock2 = (ChatStateMachineMock)chatStateMachinesManagerMock.getOrCreateChatStateMachine(CHAT_ID_2);

    // Run DatabaseCheckingThread
    final OffsetDateTime offsetDateTimeBeforeRun = OffsetDateTime.now(ZoneOffset.UTC);
    assertNotNull(databaseCheckingThread);
    assertTrue(databaseCheckingThread.lastChecking().isBefore(offsetDateTimeBeforeRun));
    databaseCheckingThread.run();

    // Verify that ChatStateMachines process method have not been called with newTransactionsArrayList
    assertEquals(0, chatStateMachineMock1.processNewTransactionsEventCalls.size());
    assertEquals(0, chatStateMachineMock2.processNewTransactionsEventCalls.size());

    // Verify lastChecking update
    assertTrue(databaseCheckingThread.lastChecking().isAfter(offsetDateTimeBeforeRun));
  }
}
