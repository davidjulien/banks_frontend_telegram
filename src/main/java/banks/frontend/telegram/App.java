package banks.frontend.telegram;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.pengrad.telegrambot.TelegramBot;

public class App {
  public static Configuration loadConfiguration() {
    try {
      return Configuration.loadFromFile("local.properties");
    } catch (FileNotFoundException e) {
      System.err.println("Configuration file <local.properties> not found");
      return null;
    } catch (IOException e) {
      System.err.println("Unable to read correctly <local.properties>");
      return null;
    } catch (InvalidFormatException e) {
      System.err.println("Invalid configuration file : " + e.getMessage());
      return null;
    }
  }

  public static void main(final String[] args) {
    Configuration configuration = loadConfiguration();
    if (configuration != null) {
      final TelegramBot bot = new TelegramBot(configuration.botToken());
      bot.setUpdatesListener(new BanksUpdatesListener(bot));
    }
  }
}
