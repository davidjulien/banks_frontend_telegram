package banks.frontend.telegram.model;

import banks.frontend.telegram.model.Transaction;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.Test;
import static org.junit.Assert.*;

public class TransactionTest {
  @Test
  public void shouldTransformTransactionToMarkdownString() {
    final Transaction transaction = new Transaction(1, "ing", "client", "acccount", OffsetDateTime.now(ZoneOffset.UTC), "transaction", LocalDate.of(2020,9,11), LocalDate.of(2020,9,11), 123.45, "description", Transaction.TransactionType.SEPA_DEBIT, 0, null, Transaction.PeriodType.MONTH, null, null, null);
    assertEquals("*2020-09-11*                        *123.45 €*\ndescription", transaction.toMarkdownString());
  }

  @Test
  public void shouldTransformTransactionToMarkdownStringWithStoreInfoAndBudget() {
    final Transaction transaction = new Transaction(1, "ing", "client", "acccount", OffsetDateTime.now(ZoneOffset.UTC), "transaction", LocalDate.of(2020,9,11), LocalDate.of(2020,9,11), 123.45, "description", Transaction.TransactionType.SEPA_DEBIT, 0, null, Transaction.PeriodType.MONTH, new Budget(1, "courant"), null, null);
    assertEquals("*2020-09-11*                        *123.45 €*\ndescription\ncourant", transaction.toMarkdownString());
  }

  @Test
  public void shouldTransformTransactionToMarkdownStringWithStoreInfo() {
    final Transaction transaction = new Transaction(1, "ing", "client", "acccount", OffsetDateTime.now(ZoneOffset.UTC), "transaction", LocalDate.of(2020,9,11), LocalDate.of(2020,9,11), 123.45, "description", Transaction.TransactionType.SEPA_DEBIT, 0, null, Transaction.PeriodType.MONTH, null, null, new Store(1, "BOUTIQUE"));
    assertEquals("*2020-09-11*                        *123.45 €*\nBOUTIQUE", transaction.toMarkdownString());
  }

  @Test
  public void shouldTransformTransactionToMarkdownStringWithCategoriesInfo() {
    Category[] categories = {new Category(1,"Alimentation"), new Category(2,"Supermarché")};
    final Transaction transaction = new Transaction(1, "ing", "client", "acccount", OffsetDateTime.now(ZoneOffset.UTC), "transaction", LocalDate.of(2020,9,11), LocalDate.of(2020,9,11), 123.45, "description", Transaction.TransactionType.SEPA_DEBIT, 0, null, Transaction.PeriodType.MONTH, null, categories, null);
    assertEquals("*2020-09-11*                        *123.45 €*\ndescription\n > Alimentation > Supermarché", transaction.toMarkdownString());
  }

  @Test
  public void shouldTransformTransactionToMarkdownStringWithBudgetCategoriesInfo() {
    Category[] categories = {new Category(1,"Alimentation"), new Category(2,"Supermarché")};
    final Transaction transaction = new Transaction(1, "ing", "client", "acccount", OffsetDateTime.now(ZoneOffset.UTC), "transaction", LocalDate.of(2020,9,11), LocalDate.of(2020,9,11), 123.45, "description", Transaction.TransactionType.SEPA_DEBIT, 0, null, Transaction.PeriodType.MONTH, new Budget(1, "courant"), categories, null);
    assertEquals("*2020-09-11*                        *123.45 €*\ndescription\ncourant > Alimentation > Supermarché", transaction.toMarkdownString());
  }


}
