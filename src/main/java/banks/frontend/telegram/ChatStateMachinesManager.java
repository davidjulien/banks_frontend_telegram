package banks.frontend.telegram;

import java.util.HashMap;

import com.pengrad.telegrambot.TelegramBot;

/**
 * This class manages ChatStateMachines. 
 * It associates an unique ChatStateMachine for each telegram chat.
 */
public class ChatStateMachinesManager {
  private final Configuration configuration;
  private final TelegramBot bot;
  private final Storage storage;

  /* Map telegram chatId to ChatStateMachine */
  private final HashMap<Long, ChatStateMachine> chatIdToChatStateMachines;

  public ChatStateMachinesManager(Configuration configuration, TelegramBot bot, Storage storage) {
    this.configuration = configuration;
    this.bot = bot;
    this.storage = storage;
    this.chatIdToChatStateMachines = new HashMap<Long, ChatStateMachine>();
  }

  /**
   * Get ChatStateMachine corresponding to chatId if exist, else create a new one.
   * @param chatId      telegram chatId
   * @return ChatStateMachine
   */
  public ChatStateMachine getOrCreateChatStateMachine(long chatId) {
    ChatStateMachine csm = this.chatIdToChatStateMachines.get(chatId);
    if (csm != null) {
      return csm;
    } else {
      csm = new ChatStateMachine(this.configuration, this.bot, this.storage, chatId);
      this.chatIdToChatStateMachines.put((Long)chatId, csm);
      return csm;
    }
  }

  public ChatStateMachine[] getAllChatStateMachines() {
    ChatStateMachine[] all = new ChatStateMachine[this.chatIdToChatStateMachines.size()];
    return this.chatIdToChatStateMachines.values().toArray(all);
  }
}
