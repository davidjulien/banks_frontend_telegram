package banks.frontend.telegram.model;

import java.time.OffsetDateTime;

public class Account {

  public enum AccountOwnership {
    SINGLE,
    JOINT
  }

  public enum AccountType {
    CURRENT,
    SAVINGS,
    HOME_LOAN
  }

  private Bank bank;
  private String clientId;
  private OffsetDateTime fetchingAt;
  private String accountId;
  private double balance;
  private String number;
  private String owner;
  private AccountOwnership ownership;
  private AccountType type;
  private String name;

  public Account(Bank bank, String clientId, OffsetDateTime fetchingAt, String accountId, double balance, String number, String owner, AccountOwnership ownership, AccountType type, String name) {
    this.bank = bank;
    this.clientId = clientId;
    this.fetchingAt = fetchingAt;
    this.accountId = accountId;
    this.balance = balance;
    this.number = number;
    this.owner = owner;
    this.ownership = ownership;
    this.type = type;
    this.name = name;
  }

  public Bank getBank() {
    return this.bank;
  }

  public String getName() {
    return this.name;
  }

  public double getBalance() {
    return this.balance;
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o instanceof Account) {
      Account a = (Account)o;
      return this.bank.equals(a.bank) 
        && this.clientId.equals(a.clientId)
        && this.accountId.equals(a.accountId);
    } else {
      return false;
    }
  }
}
