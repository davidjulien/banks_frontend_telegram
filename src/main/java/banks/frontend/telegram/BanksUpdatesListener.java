package banks.frontend.telegram;

import java.util.List;
import java.io.IOException;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
//import com.pengrad.telegrambot.response.SendResponse;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

public class BanksUpdatesListener implements UpdatesListener {
  private final TelegramBot bot;

  public BanksUpdatesListener(TelegramBot bot) {
    this.bot = bot;
  }

  @Override
  public int process(final List<Update> updates) {
    updates.forEach((update) -> {
      final long userId = update.message().from().id();
      final long chatId = update.message().chat().id();
      final String text = update.message().text();
      System.out.println("Message from " + userId + "\r\n" + update);
      BaseResponse r = bot.execute(new SendMessage(chatId, text + " !"));
      System.out.println("Reponse : " + r);
    });

    return UpdatesListener.CONFIRMED_UPDATES_ALL;
  }
}
