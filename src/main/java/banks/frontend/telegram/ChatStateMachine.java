package banks.frontend.telegram;

import banks.frontend.telegram.model.Transaction;
import banks.frontend.telegram.model.Account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

/**
 * Each ChatStateMachine manages one conversation. ChatStateMachine are created and managed by ChatStateMachinesManager.
 * There are 2 states : 
 *  - INIT : wait for the security code stored in local.properties. If security code is correct, it switches to ECHO state.
 *  - ECHO : reply to any received message by adding an ' !' at the end of the message, sends message when new transactions are available
 */
public class ChatStateMachine {
  /* Telegram max message length %*/
  private static int MAX_MESSAGE_LENGTH = 4096;

  enum StateName {
    INIT,
    ECHO,

    // Workaround: JaCoCo is not yet able to analyze switch command correctly for enum values.
    // State will go back to INIT after receiving a command in INVALID_STATE.
    INVALID_STATE
  }

  private final Configuration configuration;
  private final TelegramBot bot;
  private final Storage storage;
  private final long chatId;
  private StateName currentStateName;

  // Expected command in INIT state.
  private static Pattern INIT_PATTERN = Pattern.compile("^/init (.*)$");

  public ChatStateMachine(Configuration configuration, TelegramBot bot, Storage storage, long chatId) {
    this(configuration, bot, storage, chatId, StateName.INIT);
  }

  ChatStateMachine(Configuration configuration, TelegramBot bot, Storage storage, long chatId, StateName currentStateName) {
    this.configuration = configuration;
    this.bot = bot;
    this.storage = storage;
    this.chatId = chatId;
    this.currentStateName = currentStateName;
  }


  public StateName currentStateName() {
    return this.currentStateName;
  }

  public void process(Update update) {
    switch(this.currentStateName) {
      case INIT:
        process_state_init(update);
        break;
      case ECHO:
        process_state_echo(update);
        break;

      // Only previous states are valid
      default:
        bot.execute(new SendMessage(chatId, "Bot is in an unknown state. Init it again with /init YOUR_SECURITY_CODE"));
        this.currentStateName = StateName.INIT;
    }
  }

  public void process(NewTransactionsEvent event) {
    switch(this.currentStateName) {
      case INIT:
        break;
      case ECHO:
        process_state_echo(event);
        break;

      default:
        this.currentStateName = StateName.INIT;
    }
  }

  private void process_state_init(Update update) {
    final String text = update.message().text();
    final long chatId = update.message().chat().id();
    final Matcher matcher = INIT_PATTERN.matcher(text);
    if (matcher.find() == false) {
      bot.execute(new SendMessage(chatId, "Security code required to access data."));
    } else {
      String initSecurityCode = matcher.group(1);
      String expectedSecurityCode = configuration.securityCode();
      if (expectedSecurityCode.equals(initSecurityCode)) {
        this.currentStateName = StateName.ECHO;
        bot.execute(new SendMessage(chatId, "Security code validated."));
      } else {
        bot.execute(new SendMessage(chatId, "Security code invalid."));
      }
    }
  }

  private void process_state_echo(Update update) {
    final String text = update.message().text();
    final long chatId = update.message().chat().id();
    bot.execute(new SendMessage(chatId, text + " !"));
  }

  private void process_state_echo(NewTransactionsEvent newTransactionsEvent) {
    // Send how many new transactions have been identified
    final int nbrTransactions = newTransactionsEvent.getTransactions().size();
    final String withOrWithoutS = nbrTransactions <= 1 ? "" : "s";
    bot.execute(new SendMessage(chatId, newTransactionsEvent.getTransactions().size() + " new transaction" + withOrWithoutS + " identified."));

    // Send one message with account balance each time current transaction not belongs to the same account than previous transaction.
    // Send one message for each transaction
    String bankId = null;
    String clientId = null;
    String accountId = null;
    for(Transaction transaction : newTransactionsEvent.getTransactions()) {
      if (! (transaction.getBankId().equals(bankId) && transaction.getClientId().equals(clientId) && transaction.getAccountId().equals(accountId))) {
        bankId = transaction.getBankId();
        clientId = transaction.getClientId();
        accountId = transaction.getAccountId();
        Account account = storage.getAccount(bankId, clientId, accountId);
        bot.execute(new SendMessage(chatId, String.format("%s/%s: %.2f €", account.getBank().getName(), account.getName(), account.getBalance())));
      }

      final String transactionDescription = transaction.toMarkdownString();
      // We suppose that we don't have description longer than MAX_MESSAGE_LENGTH..
      bot.execute(new SendMessage(chatId, transactionDescription).parseMode(ParseMode.Markdown));
    }
  }
}
