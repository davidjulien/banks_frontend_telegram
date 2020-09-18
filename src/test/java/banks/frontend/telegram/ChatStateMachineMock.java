package banks.frontend.telegram;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import static org.junit.Assert.*;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;


/**
 * Mocking ChatStateMachine to verify calls to process methods.
 */
public class ChatStateMachineMock extends ChatStateMachine {
  private final long chatId;
  public final ArrayList<Update> processCalls;
  public final ArrayList<NewTransactionsEvent> processNewTransactionsEventCalls;

  public ChatStateMachineMock(long chatId) {
    super(null, null, null, chatId);
    this.chatId = chatId;
    this.processCalls = new ArrayList<Update>();
    this.processNewTransactionsEventCalls = new ArrayList<NewTransactionsEvent>();
  }

  public void process(Update update) {
    this.processCalls.add(update);
  }

  public void process(NewTransactionsEvent newTransactionsEvent) {
    this.processNewTransactionsEventCalls.add(newTransactionsEvent);
  }
}
