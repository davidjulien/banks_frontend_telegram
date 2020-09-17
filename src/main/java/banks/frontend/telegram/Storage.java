package banks.frontend.telegram;

import banks.frontend.telegram.model.Transaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import javax.sql.DataSource;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.Instant;
import java.util.ArrayList;

public class Storage {
  private DataSource dataSource;

  public Storage(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public boolean connect() {
    try {
      Connection con = this.dataSource.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT version FROM apps WHERE name = 'banks_frontend_telegram'");

      String version;
      if (rs.next()) {
        version = rs.getString(1);
      } else {
        version = "0.0.0";
      }

      return true;
    } catch (SQLException ex) {
      System.err.println("Unable to connect : " + ex.getMessage());
      return false;
    }
  }

  //  Files.find("resources/",1, (path, basicFileAttributes) -> path.toFile().getName().matches(version+"_.*.sql"));

  /**
   * Fetch new transactions since lastChecking.
   * @param lastChecking        last date/time of transactions checking
   * @return list of transactions
   */
  public ArrayList<Transaction> getNewTransactionsSince(OffsetDateTime lastChecking) {
    try {
      Connection con = this.dataSource.getConnection();
      PreparedStatement st = con.prepareStatement("SELECT id, bank_id, client_id, account_id, fetching_at, transaction_id, accounting_date, effective_date, amount, description, type FROM transactions WHERE fetching_at > ?");
      st.setObject(1, lastChecking.toLocalDateTime()); // Because we store timestamp without time zone values
      System.out.println("Query="+st);
      ResultSet rs = st.executeQuery();

      ArrayList<Transaction> transactions = new ArrayList<Transaction>();
      while(rs.next()) {
        Transaction transaction = new Transaction(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), 
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(rs.getTimestamp(5).getTime()), ZoneOffset.UTC), // Convert timestamp to UTC
            rs.getString(6), rs.getDate(7).toLocalDate(), rs.getDate(8).toLocalDate(), rs.getDouble(9), rs.getString(10), Transaction.TransactionType.valueOf(rs.getString(11).toUpperCase()));
        transactions.add(transaction);
      }

      return transactions;
    } catch (SQLException ex) {
      System.err.println("Unable to get new transactions : " + ex.getMessage());
      return null;
    }
  }
}
