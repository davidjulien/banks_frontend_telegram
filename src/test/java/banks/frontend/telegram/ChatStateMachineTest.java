package banks.frontend.telegram;

import banks.frontend.telegram.model.Transaction;

import java.util.ArrayList;
import java.util.Map;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.Test;
import static org.junit.Assert.*;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;

/**
 * Unit tests for ChatStateMachine
 */
public class ChatStateMachineTest {
  final static long CHAT_ID = 123456;
  final static String BOT_TOKEN = "fake_bot_token";
  final static String SECURITY_CODE = "valid secure code";

  /**
   * Build an Update object. This function is used in different tests to quickly build a message.
   */
  public static Update buildUpdateWithMessage(long chatId, String message) {
    return  BotUtils.parseUpdate("{\"update_id\":874199391,\n" +
        "\"message\":{\"message_id\":33111,\"from\":{\"id\":1231231231,\"is_bot\":false,\"first_name\":\"RRRR\",\"username\":\"RRRR54321\"},\"chat\":{\"id\":"+String.valueOf(chatId)+",\"title\":\"hhh iiiiii ccccc\",\"type\":\"supergroup\"},\"date\":1579958705,\"text\":\"" + message + "\"}}");
  }

  @Test
  public void shouldInitChatStateMachine() {
    Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    TelegramBotMock bot = new TelegramBotMock(BOT_TOKEN);
    ChatStateMachine csm = new ChatStateMachine(configuration, bot, CHAT_ID);

    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());
    assertEquals(0, bot.executeCalls.size());
  }

  // Tests in INIT state

  @Test
  public void shouldSwitchToEchoFromInitAfterSecurityCodeValidation() {
    Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    TelegramBotMock bot = new TelegramBotMock(BOT_TOKEN);
    ChatStateMachine csm = new ChatStateMachine(configuration, bot, CHAT_ID);

    Update initUpdate = buildUpdateWithMessage(CHAT_ID, "/init " + SECURITY_CODE);

    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());

    csm.process(initUpdate);

    assertEquals(ChatStateMachine.StateName.ECHO, csm.currentStateName());

    assertEquals(1, bot.executeCalls.size());                                  // only 1 call
    bot.verifyExecuteCall(0, CHAT_ID, "Security code validated.");      // Message sent
  }

  @Test
  public void shouldRemainInInitAfterInvalidSecurityCode() {
    Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    TelegramBot bot = new TelegramBot(BOT_TOKEN);
    ChatStateMachine csm = new ChatStateMachine(configuration, bot, CHAT_ID);

    Update initUpdate = buildUpdateWithMessage(CHAT_ID, "/init blabla");

    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());
    csm.process(initUpdate);
    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());
  }

  @Test
  public void shouldRemainInInitAfterInvalidCommand() {
    Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    TelegramBotMock bot = new TelegramBotMock(BOT_TOKEN);
    ChatStateMachine csm = new ChatStateMachine(configuration, bot, CHAT_ID);

    Update initUpdate = buildUpdateWithMessage(CHAT_ID, "other message");

    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());
    csm.process(initUpdate);

    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());

    assertEquals(1, bot.executeCalls.size());                                                // only 1 call
    bot.verifyExecuteCall(0, CHAT_ID, "Security code required to access data.");      // Message sent
  }

  @Test
  public void shouldGoBackToInitFromAnInvalidState() {
    Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    TelegramBotMock bot = new TelegramBotMock(BOT_TOKEN);
    ChatStateMachine csm = new ChatStateMachine(configuration, bot, CHAT_ID, ChatStateMachine.StateName.INVALID_STATE);

    assertEquals(ChatStateMachine.StateName.INVALID_STATE, csm.currentStateName());
    assertEquals(0, bot.executeCalls.size());

    Update initUpdate = buildUpdateWithMessage(CHAT_ID, "blabla");
    csm.process(initUpdate);

    // Verify that ChatStateMachine returns in INIT state
    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());

    // Verify that bot sends a message to user
    assertEquals(1, bot.executeCalls.size());
    bot.verifyExecuteCall(0, CHAT_ID, "Bot is in an unknown state. Init it again with /init YOUR_SECURITY_CODE");
  }

  @Test
  public void shouldDoNothingInInitWhenNewTransactionsAreAvailable() {
    Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    TelegramBotMock bot = new TelegramBotMock(BOT_TOKEN);
    ChatStateMachine csm = new ChatStateMachine(configuration, bot, CHAT_ID);

    NewTransactionsEvent newTransactionsEvent = new NewTransactionsEvent(new ArrayList<Transaction>());

    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());
    csm.process(newTransactionsEvent);

    // Verify that we remain in INIT state
    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());

    // Verify that bot does not send a message to user
    assertEquals(0, bot.executeCalls.size());
  }



  // Tests in ECHO state


  // ChatStateMachine processes user request
  @Test
  public void shouldEchoMessage() {
    final Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    final TelegramBotMock bot = new TelegramBotMock(BOT_TOKEN);
    final ChatStateMachine csm = new ChatStateMachine(configuration, bot, CHAT_ID);

    // Verify that ChatStateMachine is in INIT state
    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());

    // User sends /init with valid code
    final Update initUpdate = buildUpdateWithMessage(CHAT_ID, "/init "+SECURITY_CODE);
    csm.process(initUpdate);

    // Verify that ChatStateMachine is in ECHO state
    assertEquals(ChatStateMachine.StateName.ECHO, csm.currentStateName());
    assertEquals(1, bot.executeCalls.size());
    bot.verifyExecuteCall(0, CHAT_ID, "Security code validated.");

    // User sends a message
    final String MESSAGE = "Hello world";
    final Update messageUpdate = buildUpdateWithMessage(CHAT_ID, MESSAGE);
    csm.process(messageUpdate);

    // Verify that ChatStateMachine is still in ECHO state
    assertEquals(ChatStateMachine.StateName.ECHO, csm.currentStateName());

    // Verify that ChatStateMachine answers correctly
    assertEquals(2, bot.executeCalls.size());
    bot.verifyExecuteCall(1, CHAT_ID, MESSAGE + " !");
  }

  // ChatStateMachine processes NewTransactionsEvent with 1 transaction
  @Test
  public void shouldFeedbackNewTransactionsEventWith1TransactionToUser() {
    final Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    final TelegramBotMock bot = new TelegramBotMock(BOT_TOKEN);
    final ChatStateMachine csm = new ChatStateMachine(configuration, bot, CHAT_ID, ChatStateMachine.StateName.ECHO);

    // Verify that ChatStateMachine is in ECHO state
    assertEquals(ChatStateMachine.StateName.ECHO, csm.currentStateName());

    // Process NewTransactionsEvent
    final ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    final Transaction transaction = new Transaction(1, "ing", "client", "acccount", OffsetDateTime.now(ZoneOffset.UTC), "transaction", LocalDate.of(2020,9,11), LocalDate.of(2020,9,11), 123.45, "description", Transaction.TransactionType.SEPA_DEBIT);
    transactions.add(transaction);
    final NewTransactionsEvent newTransactionsEvent = new NewTransactionsEvent(transactions);

    csm.process(newTransactionsEvent);

    // Verify that ChatStateMachine remains in ECHO state
    assertEquals(ChatStateMachine.StateName.ECHO, csm.currentStateName());

    // Verify that ChatStateMachine sends a message containing information related to new transactions
    assertEquals(2, bot.executeCalls.size());
    bot.verifyExecuteCall(0, CHAT_ID, "1 new transaction identified.");
    bot.verifyExecuteCall(1, CHAT_ID, "*2020-09-11*                        *123.45 €*\ndescription");
  }

  // ChatStateMachine processes NewTransactionsEvent with 2 transactions
  @Test
  public void shouldFeedbackNewTransactionsEventWith2TransactionsToUser() {
    final Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    final TelegramBotMock bot = new TelegramBotMock(BOT_TOKEN);
    final ChatStateMachine csm = new ChatStateMachine(configuration, bot, CHAT_ID, ChatStateMachine.StateName.ECHO);

    // Verify that ChatStateMachine is in ECHO state
    assertEquals(ChatStateMachine.StateName.ECHO, csm.currentStateName());

    // Process NewTransactionsEvent
    final ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    transactions.add(new Transaction(1, "ing", "client", "acccount", OffsetDateTime.now(ZoneOffset.UTC), "transaction1", LocalDate.of(2020,9,11), LocalDate.of(2020,9,11), 123.45, "description 1", Transaction.TransactionType.SEPA_DEBIT));
    transactions.add(new Transaction(2, "ing", "client", "acccount", OffsetDateTime.now(ZoneOffset.UTC), "transaction2", LocalDate.of(2020,9,11), LocalDate.of(2020,9,11), 98.12, "description 2", Transaction.TransactionType.SEPA_DEBIT));
    final NewTransactionsEvent newTransactionsEvent = new NewTransactionsEvent(transactions);

    csm.process(newTransactionsEvent);

    // Verify that ChatStateMachine remains in ECHO state
    assertEquals(ChatStateMachine.StateName.ECHO, csm.currentStateName());

    // Verify that ChatStateMachine sends a message containing information related to new transactions and one message for each transactions descriptions
    assertEquals(3, bot.executeCalls.size());
    bot.verifyExecuteCall(0, CHAT_ID, "2 new transactions identified.");
    bot.verifyExecuteCall(1, CHAT_ID, "*2020-09-11*                        *123.45 €*\ndescription 1");
    bot.verifyExecuteCall(2, CHAT_ID, "*2020-09-11*                        *98.12 €*\ndescription 2");
  }

  // ChatStateMachine processes NewTransactionsEvent with 0 transactions. It allows to test that ChatStateMachine does not send a message containing only "Transactions:"
  @Test
  public void shouldFeedbackNewTransactionsEventWith0TransactionToUser() {
    final Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    final TelegramBotMock bot = new TelegramBotMock(BOT_TOKEN);
    final ChatStateMachine csm = new ChatStateMachine(configuration, bot, CHAT_ID, ChatStateMachine.StateName.ECHO);

    // Verify that ChatStateMachine is in ECHO state
    assertEquals(ChatStateMachine.StateName.ECHO, csm.currentStateName());

    // Process NewTransactionsEvent
    final ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    final NewTransactionsEvent newTransactionsEvent = new NewTransactionsEvent(transactions);

    csm.process(newTransactionsEvent);

    // Verify that ChatStateMachine remains in ECHO state
    assertEquals(ChatStateMachine.StateName.ECHO, csm.currentStateName());

    // Verify that ChatStateMachine sends a message containing information related to new transactions
    assertEquals(1, bot.executeCalls.size());
    bot.verifyExecuteCall(0, CHAT_ID, "0 new transaction identified.");
  }


  // INVALID_STATE cases
  // Always go back to INIT state


  // In case of user event, ChatStateMachine goes back to INIT state and sends a message to user
  @Test
  public void shouldGoBackToInitWithFeedbackFromAnInvalidStateWithUserEvent() {
    Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    TelegramBotMock bot = new TelegramBotMock(BOT_TOKEN);
    ChatStateMachine csm = new ChatStateMachine(configuration, bot, CHAT_ID, ChatStateMachine.StateName.INVALID_STATE);

    assertEquals(ChatStateMachine.StateName.INVALID_STATE, csm.currentStateName());
    assertEquals(0, bot.executeCalls.size());

    Update initUpdate = buildUpdateWithMessage(CHAT_ID, "blabla");
    csm.process(initUpdate);

    // Verify that ChatStateMachine returns in INIT state
    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());

    // Verify that bot sends a message to user
    assertEquals(1, bot.executeCalls.size());
    bot.verifyExecuteCall(0, CHAT_ID, "Bot is in an unknown state. Init it again with /init YOUR_SECURITY_CODE");
  }


  // In case of database event, ChatStateMachine goes back to INIT state and no message is sent
  @Test
  public void shouldGoBackToInitWithoutFeedbackFromAnInvalidStateWithDatabaseEvent() {
    Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    TelegramBotMock bot = new TelegramBotMock(BOT_TOKEN);
    ChatStateMachine csm = new ChatStateMachine(configuration, bot, CHAT_ID, ChatStateMachine.StateName.INVALID_STATE);

    assertEquals(ChatStateMachine.StateName.INVALID_STATE, csm.currentStateName());
    assertEquals(0, bot.executeCalls.size());

    NewTransactionsEvent newTransactionsEvent = new NewTransactionsEvent(new ArrayList<Transaction>());
    csm.process(newTransactionsEvent);

    // Verify that ChatStateMachine returns in INIT state
    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());

    // Verify that bot does not send a message to user
    assertEquals(0, bot.executeCalls.size());
  }

}
