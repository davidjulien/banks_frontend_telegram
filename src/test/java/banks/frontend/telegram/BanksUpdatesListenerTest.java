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

import com.pengrad.telegrambot.TelegramBot;

/**
 * Unit test for BanksUpdatesListener
 */
public class BanksUpdatesListenerTest 
{
  private static final String BOT_TOKEN = "fake_bot_token";

  @Test 
  public void shouldCreateBanksUpdateListener() {
    final TelegramBot bot = new TelegramBot(BOT_TOKEN);
    BanksUpdatesListener listener = new BanksUpdatesListener(bot);
    assertNotNull(listener);
  }

  /* Mock TelegramBot to verify calls of "execute" function
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
  };

  @Test
  public void shouldReplyToMessage() {
    final TelegramBotMock bot = new TelegramBotMock(BOT_TOKEN);
    BanksUpdatesListener listener = new BanksUpdatesListener(bot);

    ArrayList<Update> updatesList = new ArrayList<Update>();
    Update update = BotUtils.parseUpdate("{\"update_id\":874199391,\n" +
            "\"message\":{\"message_id\":33111,\"from\":{\"id\":1231231231,\"is_bot\":false,\"first_name\":\"RRRR\",\"username\":\"RRRR54321\"},\"chat\":{\"id\":123456,\"title\":\"hhh iiiiii ccccc\",\"type\":\"supergroup\"},\"date\":1579958705,\"text\":\"Hello World\"}}");
    updatesList.add(update);

    int ret = listener.process(updatesList);
    assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, ret);
    assertEquals(1, bot.calls.size());                                  // only 1 call
    BaseRequest request = bot.calls.get(0);
    assertTrue(request instanceof SendMessage);                         // message is SendMessage
    Map<String,Object> params = request.getParameters();
    assertEquals((long)123456, params.get("chat_id"));
    assertEquals("Hello World !", params.get("text"));
  }
}
