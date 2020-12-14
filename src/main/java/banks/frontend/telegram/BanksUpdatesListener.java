package banks.frontend.telegram;

import java.util.List;
import java.util.HashMap;
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
  private final Configuration configuration;
  private final TelegramBot bot;
  private final ChatStateMachinesManager chatStateMachinesManager;

  public BanksUpdatesListener(Configuration configuration, TelegramBot bot, ChatStateMachinesManager chatStateMachinesManager) {
    this.configuration = configuration;
    this.bot = bot;
    this.chatStateMachinesManager = chatStateMachinesManager;
  }

  @Override
  public int process(final List<Update> updates) {
    try {
      updates.forEach((update) -> {
        final long chatId = update.message().chat().id();
        final ChatStateMachine csm = this.chatStateMachinesManager.getOrCreateChatStateMachine(chatId);
        csm.process(update);
      });

      return UpdatesListener.CONFIRMED_UPDATES_ALL;
    } catch (Exception e) {
      System.err.println("Exception : ");
      e.printStackTrace();
      return UpdatesListener.CONFIRMED_UPDATES_NONE;
    }
  }
}
