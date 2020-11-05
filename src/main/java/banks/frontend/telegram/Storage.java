package banks.frontend.telegram;

import banks.frontend.telegram.model.Transaction;
import banks.frontend.telegram.model.Account;
import banks.frontend.telegram.model.Bank;
import banks.frontend.telegram.model.Budget;
import banks.frontend.telegram.model.Store;
import banks.frontend.telegram.model.Category;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Array;

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
      st = con.prepareStatement("SELECT transactions.id, bank_id, client_id, account_id, fetching_at, transaction_id, accounting_date, effective_date, amount, description, type, ext_mapping_id, ext_date, ext_period, budgets.id, budgets.name, case when ext_categories_id is null then null else ARRAY(select id FROM categories where ext_categories_id @> ARRAY[id] order by id) end as ext_categories_id, ARRAY(select name FROM categories where ext_categories_id @> ARRAY[id] order by id) as ext_categories_name, stores.id, stores.name FROM transactions LEFT JOIN budgets ON ext_budget_id = budgets.id LEFT JOIN stores ON ext_store_id = stores.id WHERE fetching_at > ? ORDER BY bank_id, client_id, account_id, effective_date DESC, fetching_position DESC;");
      st.setObject(1, lastChecking.toLocalDateTime()); // Because we store timestamp without time zone values
      ResultSet rs = st.executeQuery();

      ArrayList<Transaction> transactions = new ArrayList<Transaction>();
      while(rs.next()) {
        Category[] categories = build_categories(rs.getArray(17), rs.getArray(18));

        Transaction transaction = new Transaction(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(rs.getTimestamp(5).getTime()), ZoneOffset.UTC), // Convert timestamp to UTC
            rs.getString(6), rs.getDate(7).toLocalDate(), rs.getDate(8).toLocalDate(), rs.getDouble(9), rs.getString(10), Transaction.TransactionType.valueOf(rs.getString(11).toUpperCase()),
            rs.getLong(12),
            rs.getObject(13) == null ? null : rs.getDate(13).toLocalDate(),
            Transaction.PeriodType.valueOf(rs.getString(14).toUpperCase()),
            rs.getObject(15) == null ? null : new Budget(rs.getInt(15), rs.getString(16)),
            categories,
            rs.getObject(19) == null ? null : new Store(rs.getInt(19), rs.getString(20)));
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

  private Category[] build_categories(Array ids0, Array names0) throws java.sql.SQLException {
    if (ids0 == null) {
      return null;
    } else {
      Integer[] ids = (Integer[])ids0.getArray();
      String[] names = (String[])names0.getArray();

      Category[] categories = new Category[ids.length];
      for(int i = 0; i < names.length; i++) {
        categories[i] = new Category(ids[i], names[i]);
      }

      return categories;
    }
  }

  /**
   * Fetch bank account data.
   * @param bankId      bankId
   * @param clientId    clientId
   * @param accountId   accountId
   * @return Bank account or null
   */
  public Account getAccount(String bankId, String clientId, String accountId) {
    Connection con = null;
    PreparedStatement st = null;
    try {
      con = this.dataSource.getConnection();
      st = con.prepareStatement("SELECT bank_id, b.name, client_id, fetching_at, account_id, balance, number, owner, ownership, type, a.name FROM accounts a, banks b WHERE a.bank_id = b.id AND bank_id = ? and client_id = ? and account_id = ? ORDER BY a.fetching_at DESC LIMIT 1");
      st.setString(1, bankId);
      st.setString(2, clientId);
      st.setString(3, accountId);
      ResultSet rs = st.executeQuery();
      if(rs.next()) {
        return new Account(new Bank(rs.getString(1), rs.getString(2)),
            rs.getString(3),
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(rs.getTimestamp(4).getTime()), ZoneOffset.UTC), // Convert timestamp to UTC
            rs.getString(5),
            rs.getDouble(6),
            rs.getString(7),
            rs.getString(8),
            Account.AccountOwnership.valueOf(rs.getString(9).toUpperCase()),
            Account.AccountType.valueOf(rs.getString(10).toUpperCase()),
            rs.getString(11));
      } else {
        return null;
      }
    } catch (SQLException ex) {
      System.err.println("Unable to get account info: " + ex.getMessage());
      return null;
    } finally {
      close(con, st);
    }
  }
}
