package banks.frontend.telegram.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class BankTest {
  @Test
  public void shouldBeEqualsOrNot() {
    Bank b1 = new Bank("ing", "ING");
    Bank b2 = new Bank("ing", "ING");
    Bank b3 = new Bank("ing", "ING2");
    Bank b4 = new Bank("ing2", "ING");
    String s = "ING";

    assertEquals(b1, b1);
    assertEquals(b1, b2);
    assertNotEquals(b1, b3);
    assertNotEquals(b1, b4);
    assertNotEquals(b1, s);
    assertNotEquals(s, b1);
  }
}
