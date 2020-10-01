package banks.frontend.telegram.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class Transaction {
  public enum TransactionType {
    CARD_DEBIT,
    CARD_WITHDRAWAL,
    CHECK,
    SEPA_DEBIT,
    TRANSFER,
    INTERESTS,
    OTHER
  }

  private long id;
  private String bankId;
  private String clientId;
  private String accountId;
  private OffsetDateTime fetchingAt;
  private String transactionId;
  private LocalDate accountingDate;
  private LocalDate effectiveDate;
  private double amount;
  private String description;
  private TransactionType transactionType;

  public Transaction(long id, String bankId, String clientId, String accountId, OffsetDateTime fetchingAt, String transactionId, LocalDate accountingDate, LocalDate effectiveDate, double amount, String description, TransactionType transactionType) {
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

  public String getBankId() {
    return this.bankId;
  }

  public String getClientId() {
    return this.clientId;
  }

  public String getAccountId() {
    return this.accountId;
  }

  public String toMarkdownString() {
    return String.format("*%s*                        *%.2f €*\n%s", this.effectiveDate.toString(), this.amount, this.description);
  }

}
