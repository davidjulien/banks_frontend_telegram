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
    Connection con = null;
    Statement st = null;
    try {
      con = this.dataSource.getConnection();
      st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT version FROM apps WHERE name = 'banks_frontend_telegram'");

      String version;
      if (rs.next()) {
        version = rs.getString(1);
      } else {
        version = "0.0.0";
      }
      con.close();

      return true;
    } catch (SQLException ex) {
      System.err.println("Unable to connect : " + ex.getMessage());
      return false;
    } finally {
      close(con, st);
    }
  }

  //  Files.find("resources/",1, (path, basicFileAttributes) -> path.toFile().getName().matches(version+"_.*.sql"));

  /**
   * This method closes Connection and Statement objects.
   */
  private void close(Connection con, Statement st) {
    try {
      con.close();
    } catch (Exception e) {
    }
    try {
      st.close();
    } catch (Exception e) {
    }
  }


  /**
   * Fetch new transactions since lastChecking.
   * @param lastChecking        last date/time of transactions checking
   * @return list of transactions
   */
  public ArrayList<Transaction> getNewTransactionsSince(OffsetDateTime lastChecking) {
    Connection con = null;
    PreparedStatement st = null;
    try {
      con = this.dataSource.getConnection();
      st = con.prepareStatement("SELECT id, bank_id, client_id, account_id, fetching_at, transaction_id, accounting_date, effective_date, amount, description, type FROM transactions WHERE fetching_at > ?");
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
    } finally {
      close(con, st);
    }
  }

  /**
   * Fetch current balance related to a bank account.
   * @param bankId      bankId
   * @param clientId    clientId
   * @param accountId   accountId
   * @return Balance value or NaN (account not found or SQL error)
   */
  public double getAccountBalance(String bankId, String clientId, String accountId) {
    Connection con = null;
    PreparedStatement st = null;
    try {
      con = this.dataSource.getConnection();
      st = con.prepareStatement("SELECT balance FROM accounts WHERE bank_id = ? and client_id = ? and account_id = ? ORDER BY fetching_at DESC LIMIT 1");
      st.setString(1, bankId);
      st.setString(2, clientId);
      st.setString(3, accountId);
      ResultSet rs = st.executeQuery();
      if(rs.next()) {
        return rs.getDouble(1);
      } else {
        return Double.NaN;
      }
    } catch (SQLException ex) {
      System.err.println("Unable to get balance : " + ex.getMessage());
      return Double.NaN;
    } finally {
      close(con, st);
    }
  }
}
