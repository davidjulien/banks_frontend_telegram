package banks.frontend.telegram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
 * Unit test for BanksUpdatesListener
 */
public class ChatStateMachinesManagerTest
{
  private static final String BOT_TOKEN = "fake_bot_token";
  private static final String SECURITY_CODE = "security_code";
  private static final long CHAT_ID_1 = 1;
  private static final long CHAT_ID_2 = 2;
  private static final long CHAT_ID_3 = 3;
  private static final long CHAT_ID_4 = 4;

  @Test
  public void shouldNotDuplicateChatStateMachineForSameChatIds() {
    final Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    final TelegramBot bot = new TelegramBot(BOT_TOKEN);
    final ChatStateMachinesManager chatStateMachinesManager = new ChatStateMachinesManager(configuration, bot);

    final ChatStateMachine csm1 = chatStateMachinesManager.getOrCreateChatStateMachine(CHAT_ID_1);
    assertNotNull(csm1);

    final ChatStateMachine csm1b = chatStateMachinesManager.getOrCreateChatStateMachine(CHAT_ID_1);
    assertEquals(csm1, csm1b);          // get previously returned ChatStateMachine
  }

  @Test
  public void shouldCreate2ChatStateMachinesFor2DifferentChatIds() {
    final Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    final TelegramBot bot = new TelegramBot(BOT_TOKEN);
    final ChatStateMachinesManager chatStateMachinesManager = new ChatStateMachinesManager(configuration, bot);

    final ChatStateMachine csm1 = chatStateMachinesManager.getOrCreateChatStateMachine(CHAT_ID_1);
    assertNotNull(csm1);

    final ChatStateMachine csm2 = chatStateMachinesManager.getOrCreateChatStateMachine(CHAT_ID_2);
    assertNotEquals(csm1, csm2);          // ChatStateMachines are different
  }

  @Test
  public void shouldGetAllChatStateMachines() {
    final Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    final TelegramBot bot = new TelegramBot(BOT_TOKEN);
    final ChatStateMachinesManager chatStateMachinesManager = new ChatStateMachinesManager(configuration, bot);

    final ChatStateMachine csm1 = chatStateMachinesManager.getOrCreateChatStateMachine(CHAT_ID_1);
    final ChatStateMachine csm2 = chatStateMachinesManager.getOrCreateChatStateMachine(CHAT_ID_2);
    final ChatStateMachine csm3 = chatStateMachinesManager.getOrCreateChatStateMachine(CHAT_ID_3);
    final ChatStateMachine csm4 = chatStateMachinesManager.getOrCreateChatStateMachine(CHAT_ID_4);

    // Convert to ArrayList to verify that all ChatStateMachines exist
    final ChatStateMachine[] allChatStateMachines = chatStateMachinesManager.getAllChatStateMachines();
    final List<ChatStateMachine> allChatStateMachinesArrayList = Arrays.asList(allChatStateMachines);
    assertTrue(allChatStateMachinesArrayList.contains(csm1));
    assertTrue(allChatStateMachinesArrayList.contains(csm2));
    assertTrue(allChatStateMachinesArrayList.contains(csm3));
    assertTrue(allChatStateMachinesArrayList.contains(csm4));
  }


}
