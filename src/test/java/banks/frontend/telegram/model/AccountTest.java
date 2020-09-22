package banks.frontend.telegram.model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.Test;
import static org.junit.Assert.*;

public class AccountTest {
  @Test
  public void shouldBeEqualsOrNot() {
    final Account a1 = new Account(new Bank("ing", "ING"), "client", OffsetDateTime.of(2020,1,1,0,0,0,0, ZoneOffset.UTC), "account", 435.65, "NUMBER", "OWNER", Account.AccountOwnership.SINGLE, Account.AccountType.CURRENT, "Compte courant"); 
    final Account a2 = new Account(new Bank("ing", "ING"), "client", OffsetDateTime.of(2020,1,1,0,0,0,0,ZoneOffset.UTC), "account", 435.65, "NUMBER", "OWNER", Account.AccountOwnership.SINGLE, Account.AccountType.CURRENT, "Compte courant"); 
    final Account a3 = new Account(new Bank("ing", "ING"), "client2", OffsetDateTime.of(2020,9,12,0,0,0,0,ZoneOffset.UTC), "account", 435.65, "NUMBER", "OWNER", Account.AccountOwnership.SINGLE, Account.AccountType.CURRENT, "Compte courant"); 
    final Account a4 = new Account(new Bank("ing", "ING"), "client", OffsetDateTime.of(2020,1,1,0,0,0,0,ZoneOffset.UTC), "account1", 435.65, "NUMBER", "OWNER", Account.AccountOwnership.SINGLE, Account.AccountType.CURRENT, "Compte courant"); 
    final Account a5 = new Account(new Bank("ing2", "ING"), "client", OffsetDateTime.of(2020,1,1,0,0,0,0,ZoneOffset.UTC), "account1", 435.65, "NUMBER", "OWNER", Account.AccountOwnership.SINGLE, Account.AccountType.CURRENT, "Compte courant"); 
    final String s = "ING";

    assertEquals(a1, a1);
    assertEquals(a1, a2);
    assertNotEquals(a1, a3);
    assertNotEquals(a1, a4);
    assertNotEquals(a1, a5);
    assertNotEquals(a1, s);
    assertNotEquals(s, a1);
  }
}
