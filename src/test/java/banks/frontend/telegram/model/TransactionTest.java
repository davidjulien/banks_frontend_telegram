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
    final Transaction transaction = new Transaction(1, "ing", "client", "acccount", OffsetDateTime.now(ZoneOffset.UTC), "transaction", LocalDate.of(2020,9,11), LocalDate.of(2020,9,11), 123.45, "description", Transaction.TransactionType.SEPA_DEBIT);
    assertEquals("*2020-09-11*                        *123.45 €*\ndescription", transaction.toMarkdownString());
  }
}



