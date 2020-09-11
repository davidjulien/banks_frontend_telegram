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

  @Override
  public ChatStateMachine[] getAllChatStateMachines() {
    ChatStateMachineMock[] all = new ChatStateMachineMock[this.getOrCreateCalls.size()];
    return this.getOrCreateCalls.values().toArray(all);
  }
}
