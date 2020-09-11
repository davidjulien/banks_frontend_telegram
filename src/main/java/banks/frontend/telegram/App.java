package banks.frontend.telegram;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.*;

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

  private final static int DATABASECHECKING_DELAY = 60;
  private final static int DATABASECHECKING_PERIOD = 5*60; // Every 5 minutes

  static org.postgresql.ds.PGSimpleDataSource setupDatabase() {
    org.postgresql.ds.PGSimpleDataSource realDataSource = new org.postgresql.ds.PGSimpleDataSource();
    realDataSource.setDatabaseName("banks_fetch");
    realDataSource.setUser("banks_fetch_user");
    return realDataSource;
  }

  public static void main(final String[] args) {
    Configuration configuration = loadConfiguration();
    if (configuration != null) {
      final TelegramBot bot = new TelegramBot(configuration.botToken());
      final ChatStateMachinesManager chatStateMachinesManager = new ChatStateMachinesManager(configuration, bot);
      final Storage storage = new Storage(setupDatabase());

      // Try to connect to database
      if (!storage.connect()) {
        System.err.println("Unable to connect to database");
      } else {
        // Check database periodically
        final DatabaseCheckingThread databaseCheckingThread = new DatabaseCheckingThread(chatStateMachinesManager, storage);
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(databaseCheckingThread, DATABASECHECKING_DELAY, DATABASECHECKING_PERIOD, TimeUnit.SECONDS);

        // Start bot
        bot.setUpdatesListener(new BanksUpdatesListener(configuration, bot, chatStateMachinesManager));
      }
    }
  }
}
