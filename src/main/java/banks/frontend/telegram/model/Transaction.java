package banks.frontend.telegram.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction {
  public enum TransactionType {
    CARD_DEBIT,
    CARD_WITHDRAWAL,
    CHECK,
    SEPA_DEBIT,
    TRANSFER,
    INTERESTS
  }

  private long id;
  private String bankId;
  private String clientId;
  private String accountId;
  private LocalDateTime fetchingAt;
  private String transactionId;
  private LocalDate accountingDate;
  private LocalDate effectiveDate;
  private double amount;
  private String description;
  private TransactionType transactionType;

  public Transaction(long id, String bankId, String clientId, String accountId, LocalDateTime fetchingAt, String transactionId, LocalDate accountingDate, LocalDate effectiveDate, double amount, String description, TransactionType transactionType) {
    this.id = id;
    this.bankId = bankId;
    this.clientId = clientId;
    this.accountId = accountId;
    this.fetchingAt = fetchingAt;
    this.transactionId = transactionId;
    this.accountingDate = accountingDate;
    this.effectiveDate = effectiveDate;
    this.amount = amount;
    this.description = description;
    this.transactionType = transactionType;
  }
}
