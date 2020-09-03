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
  public ArrayList<BaseRequest> calls;

  public TelegramBotMock(String botToken) {
    super(botToken);
    this.calls = new ArrayList<BaseRequest>();
  }

  @Override
  public BaseResponse execute(BaseRequest request) {
    this.calls.add(request); // Stores request
    //return gson.fromJson("{\"ok\":true}", BaseResponse.class);
    return super.execute(request);
  }

  public boolean verifyExecuteCall(int numCall, long expectedChatId, String expectedMessage) {
    BaseRequest request = this.calls.get(numCall);
    assertTrue(request instanceof SendMessage);                         // message is SendMessage
    Map<String,Object> params = request.getParameters();
    assertEquals(expectedChatId, params.get("chat_id"));                // verify chat id
    assertEquals(expectedMessage, params.get("text"));                  // verify message content
    return true;
  }
}
