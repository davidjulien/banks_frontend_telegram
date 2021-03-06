package banks.frontend.telegram;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

/**
 * Mock TelegramBot to verify calls of "execute" function
 */
public class TelegramBotMock extends TelegramBot {
  public final ArrayList<BaseRequest> executeCalls;

  public TelegramBotMock(String botToken) {
    super(botToken);
    this.executeCalls = new ArrayList<BaseRequest>();
  }

  @Override
  @SuppressWarnings("unchecked") // workaround method clash
  public BaseResponse execute(BaseRequest request) {
    this.executeCalls.add(request); // Stores request
    return super.execute(request);
  }

  public boolean verifyExecuteCall(int numCall, long expectedChatId, String expectedMessage) {
    BaseRequest request = this.executeCalls.get(numCall);
    assertTrue(request instanceof SendMessage);                         // message is SendMessage
    Map<String,Object> params = ((SendMessage)request).getParameters();
    assertEquals(expectedChatId, params.get("chat_id"));                // verify chat id
    assertEquals(expectedMessage, params.get("text"));                  // verify message content
    return true;
  }
}
