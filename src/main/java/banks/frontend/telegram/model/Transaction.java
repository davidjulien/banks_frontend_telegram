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
    BANK_FEES,
    OTHER
  }

  public enum PeriodType {
    MONTH,
    BIMESTER,
    QUARTER,
    SEMESTER,
    ANNUAL
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

  private long extMappingId;
  private LocalDate extDate;
  private PeriodType extPeriod;
  private Budget extBudget;
  private Category[] extCategories;
  private Store extStore;

  public Transaction(long id, String bankId, String clientId, String accountId, OffsetDateTime fetchingAt, String transactionId, LocalDate accountingDate, LocalDate effectiveDate, double amount, String description, TransactionType transactionType, long extMappingId, LocalDate extDate, PeriodType extPeriod, Budget extBudget, Category[] extCategories, Store extStore) {
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
    this.extMappingId = extMappingId;
    this.extDate = extDate;
    this.extPeriod = extPeriod;
    this.extBudget = extBudget;
    this.extCategories = extCategories;
    this.extStore = extStore;
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
    // Replace transaction description with store name
    String description = this.extStore == null ? this.description : this.extStore.getName();
    // Add budget info
    if (this.extBudget != null) {
      description += "\n"+this.extBudget.getName();
    }
    // Add categories info
    if (this.extCategories != null) {
      description += this.extBudget == null ? "\n" : "";
      for(Category cat : this.extCategories) {
        description += " > "+cat.getName();
      }
    }
    // Use real date of transaction instead of bank date
    final LocalDate date = this.extDate != null ? this.extDate : this.effectiveDate;
    return String.format("*%s*                        *%.2f €*\n%s", date.toString(), this.amount, description);
  }
}
