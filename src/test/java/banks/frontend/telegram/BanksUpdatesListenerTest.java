package banks.frontend.telegram;

import java.util.ArrayList;
import java.util.Map;

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
 * Unit tests for BanksUpdatesListener
 */
public class BanksUpdatesListenerTest 
{
  private static final String BOT_TOKEN = "fake_bot_token";
  private static final String SECURITY_CODE = "security_code";
  private static final long CHAT_ID = 123456;

  @Test 
  public void shouldCreateBanksUpdateListener() {
    final Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    final TelegramBot bot = new TelegramBot(BOT_TOKEN);
    final Storage storage = new Storage(null);
    final ChatStateMachinesManager chatStateMachinesManager = new ChatStateMachinesManager(configuration, bot, storage);
    BanksUpdatesListener listener = new BanksUpdatesListener(configuration, bot, chatStateMachinesManager);
    assertNotNull(listener);
  }

  @Test
  public void shouldCreateChatStateMachineAndProcessUpdate() {
    final Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    final TelegramBot bot = new TelegramBot(BOT_TOKEN);
    final Storage storage = new Storage(null);
    final ChatStateMachinesManagerMock chatStateMachinesManagerMock = new ChatStateMachinesManagerMock(configuration, bot, storage);
    BanksUpdatesListener listener = new BanksUpdatesListener(configuration, bot, chatStateMachinesManagerMock);

    /* List of updates */
    ArrayList<Update> updatesList = new ArrayList<Update>();
    Update update = ChatStateMachineTest.buildUpdateWithMessage(CHAT_ID, "Hello World");
    updatesList.add(update);

    /* Processing list */
    int ret = listener.process(updatesList);

    /* Verify that we get or create a new ChatStateMachine and that we call its process function with update message */
    assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, ret);

    assertEquals(1, chatStateMachinesManagerMock.getOrCreateCalls.size());                                  // Call to create ChatStateMachine
    ChatStateMachineMock chatStateMachineMock = chatStateMachinesManagerMock.getOrCreateCalls.get(CHAT_ID);

    assertNotNull(chatStateMachineMock);
    assertEquals(1, chatStateMachineMock.processCalls.size());                                              // Call to process Update
    assertEquals(update, chatStateMachineMock.processCalls.get(0));
  }
}
