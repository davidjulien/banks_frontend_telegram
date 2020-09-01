package banks.frontend.telegram;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import com.pengrad.telegrambot.TelegramBot;

/**
 * Unit test for BanksUpdatesListener
 */
public class ConfigurationTest 
{

  @Test
  public void shouldLoadConfiguration()
  {
    try {
      Configuration config = Configuration.loadFromFile("src/test/resources/valid.properties");
      assertEquals("valid_bot_token", config.botToken());
      assertEquals("valid_security_code", config.securityCode());
    } catch (Exception e) {
      fail("Unexpected exception " + e);
    }
  }

  @Test
  public void shouldNotLoadUnknownFile()
  {
    try {
      Configuration config = Configuration.loadFromFile("src/test/resources/unknown_file.properties");
      fail("Load an unknown configuration file");
    } catch (FileNotFoundException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail("Unexpected exception " + e);
    }
  }

  @Test
  public void shouldNotLoadIfBotTokenIsMissing()
  {
    try {
      Configuration config = Configuration.loadFromFile("src/test/resources/invalid.no_bot_token.properties");
      fail("Load an invalid configuration file");
    } catch (InvalidFormatException e) {
      assertEquals("'bot_token' key not found in configuration file", e.getMessage());
    } catch (Exception e) {
      fail("Unexpected exception " + e);
    }
  }

  @Test
  public void shouldNotLoadIfSecurityCodeIsMissing()
  {
    try {
      Configuration config = Configuration.loadFromFile("src/test/resources/invalid.no_security_code.properties");
      fail("Load an invalid configuration file");
    } catch (InvalidFormatException e) {
      assertEquals("'security_code' key not found in configuration file", e.getMessage());
    } catch (Exception e) {
      fail("Unexpected exception " + e);
    }
  }
}
