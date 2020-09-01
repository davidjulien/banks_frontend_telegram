package banks.frontend.telegram;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Configuration {
  public final String botToken;
  public final String securityCode;

  /**
   * Create a configuration object from properties
   * @param botToken            Token of our bot
   * @param securityCode        Code expected from user at init
   * @return Configuration object
   */
  public Configuration(String botToken, String securityCode) {
    this.botToken = botToken;
    this.securityCode = securityCode;
  }

  public String botToken() {
    return this.botToken;
  }

  public String securityCode() {
    return this.securityCode;
  }

  /**
   * Create a configuration object from a properties file. This constructor will throw an exception if file is not found or if an expected key is missing
   * @param fileName    file to load
   * @return Configuration object
   * @throws FileNotFoundException      configuration file does not exist
   * @throws IOException                unable to read configuration file
   * @throws InvalidFormatException     an expected key is missing
   */
  public static Configuration loadFromFile(String fileName) throws FileNotFoundException, IOException, InvalidFormatException {
    Properties properties = new Properties();
    FileInputStream inputStream = new FileInputStream(fileName);
    properties.load(inputStream);
    inputStream.close();

    String botToken = properties.getProperty("bot_token");
    if (botToken == null) {
      throw new InvalidFormatException("'bot_token' key not found in configuration file");
    }

    String securityCode = properties.getProperty("security_code");
    if (securityCode == null) {
      throw new InvalidFormatException("'security_code' key not found in configuration file");
    }

    return new Configuration(botToken, securityCode);
  }

}
