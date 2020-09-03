package banks.frontend.telegram;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import static org.junit.Assert.*;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

/**
 * Mock ChatStateMachinesManager to verify calls.
 */
public class ChatStateMachinesManagerMock extends ChatStateMachinesManager {
  public final HashMap<Long, ChatStateMachineMock> getOrCreateCalls;

  public ChatStateMachinesManagerMock(Configuration configuration, TelegramBot bot) {
    super(configuration, bot);
    this.getOrCreateCalls = new HashMap<Long, ChatStateMachineMock>();
  }

  @Override
  public ChatStateMachine getOrCreateChatStateMachine(long chatId) {
    ChatStateMachineMock chatStateMachineMock = new ChatStateMachineMock(chatId); 
    this.getOrCreateCalls.put(chatId, chatStateMachineMock);
    return chatStateMachineMock;
  }

  /* Mocking ChatStateMachine to verify calls to process method */
  public class ChatStateMachineMock extends ChatStateMachine {
    private final long chatId;
    public final ArrayList<Update> processCalls;
    
    public ChatStateMachineMock(long chatId) {
      super(null, null, chatId);
      this.chatId = chatId;
      this.processCalls = new ArrayList<Update>();
    }

    public void process(Update update) {
      this.processCalls.add(update);
    }
  }

}
