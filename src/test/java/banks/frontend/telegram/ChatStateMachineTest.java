package banks.frontend.telegram;

import java.util.ArrayList;
import java.util.Map;

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

    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());

    assertEquals(1, bot.executeCalls.size());                                                // only 1 call
    bot.verifyExecuteCall(0, CHAT_ID, "Bot is in an unknown state. Init it again with /init YOUR_SECURITY_CODE");      // Message sent
  }


  // Tests in ECHO state


  @Test
  public void shouldEchoMessage() {
    final Configuration configuration = new Configuration(BOT_TOKEN, SECURITY_CODE);
    final TelegramBotMock bot = new TelegramBotMock(BOT_TOKEN);
    final ChatStateMachine csm = new ChatStateMachine(configuration, bot, CHAT_ID);

    // Init ChatStateMachine
    final Update initUpdate = buildUpdateWithMessage(CHAT_ID, "/init "+SECURITY_CODE);
 
    assertEquals(ChatStateMachine.StateName.INIT, csm.currentStateName());

    csm.process(initUpdate);

    assertEquals(ChatStateMachine.StateName.ECHO, csm.currentStateName());
    assertEquals(1, bot.executeCalls.size());                                  // only 1 call
    bot.verifyExecuteCall(0, CHAT_ID, "Security code validated.");

    // Send message
    final String MESSAGE = "Hello world";
    final Update messageUpdate = buildUpdateWithMessage(CHAT_ID, MESSAGE);
    csm.process(messageUpdate);
    assertEquals(ChatStateMachine.StateName.ECHO, csm.currentStateName());

    // Verify 
    assertEquals(2, bot.executeCalls.size());                                  // one more call
    bot.verifyExecuteCall(1, CHAT_ID, MESSAGE + " !");
  }

}
