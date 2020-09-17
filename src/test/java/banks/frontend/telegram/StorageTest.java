package banks.frontend.telegram;

import java.util.ArrayList;
import java.util.Scanner;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

import banks.frontend.telegram.model.Transaction;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit test for Storage
 */
@RunWith(MockitoJUnitRunner.class)
public class StorageTest {
  @Mock
  private DataSource ds;

  @Mock
  private Connection c;

  @Mock
  private PreparedStatement stmt;

  @Mock
  private ResultSet rs;

  private PGSimpleDataSource banksDataSource;

  @Test
  public void shouldConnectToAnInitializedDatabase() throws Exception {
    assertNotNull(ds);
    when(c.createStatement()).thenReturn(stmt);
    when(ds.getConnection()).thenReturn(c);

    when(rs.next()).thenReturn(true);
    when(rs.getString(1)).thenReturn("0.0.0");
    when(stmt.executeQuery(any(String.class))).thenReturn(rs);

    Storage storage = new Storage(ds);
    assertTrue(storage.connect());
  }

  @Test
  public void shouldConnectToAnUnitailizedDatabase() throws Exception {
    assertNotNull(ds);
    when(c.createStatement()).thenReturn(stmt);
    when(ds.getConnection()).thenReturn(c);

    when(rs.next()).thenReturn(false);
    when(stmt.executeQuery(any(String.class))).thenReturn(rs);

    Storage storage = new Storage(ds);
    assertTrue(storage.connect());
  }

  // Tests simulating an SQL error to verify that returned data are expected ones
  @Test
  public void shouldNotConnectToDatabase() throws Exception {
    assertNotNull(ds);
    when(ds.getConnection()).thenThrow(SQLException.class);

    Storage storage = new Storage(ds);
    assertFalse(storage.connect());
  }

  @Test
  public void shouldReturnNullIfgetNewTransactionsSinceFailed() throws Exception {
    assertNotNull(ds);
    when(ds.getConnection()).thenThrow(SQLException.class);

    // Verify that we don't have a list in case of SQL errors
    Storage storage = new Storage(ds);
    assertNull(storage.getNewTransactionsSince(OffsetDateTime.of(2020,9,6,8,0,0,0,ZoneOffset.UTC)));
  }


  // Tests on real test database

  PGSimpleDataSource setupDatabase() throws java.sql.SQLException, java.io.FileNotFoundException {
    PGSimpleDataSource postgresDataSource = new PGSimpleDataSource();
    postgresDataSource.setDatabaseName("postgres");
    postgresDataSource.setUser("banks_fetch_user");
    Connection postgresCon = postgresDataSource.getConnection();
    Statement st = postgresCon.createStatement();
    st.executeUpdate("DROP DATABASE IF EXISTS banks_fetch_test;");
    st.executeUpdate("CREATE DATABASE banks_fetch_test WITH OWNER banks_fetch_user;");
    st.close();
    postgresCon.close();

    PGSimpleDataSource banksDataSource = new PGSimpleDataSource();
    banksDataSource.setDatabaseName("banks_fetch_test");
    banksDataSource.setUser("banks_fetch_user");
    importSQLFromFile(banksDataSource, "src/test/resources/setup_banks_fetch_db.sql");

    return banksDataSource;
  }

  public static void importSQLFromFile(PGSimpleDataSource dataSource, String fileName) throws java.io.FileNotFoundException, java.sql.SQLException {
    File file = new File(fileName);
    InputStream in = new FileInputStream(file);

    Scanner s = new Scanner(in);
    s.useDelimiter("(;(\r)?\n)|(--\n)");
    Statement st = null;
    try {
      Connection conn = dataSource.getConnection();
      st = conn.createStatement();
      while (s.hasNext()) {
        String line = s.next();
        if (line.trim().length() > 0) {
          st.execute(line);
        }
      }
      st.close();
      conn.close();
    } catch (SQLException e) {
      System.err.println("Invalid SQL test file : " + e);
    } finally {
      if (st != null) {
        st.close();
      }
    }
  }


  @Test
  public void shouldConnectToRealTestDatabase() throws Exception {
    PGSimpleDataSource banksDataSource = setupDatabase();

    Storage storage = new Storage(banksDataSource);
    assertTrue(storage.connect());
  }

  @Test
  public void shouldGetNewTransactionsSinceLastChecking() throws Exception {
    PGSimpleDataSource banksDataSource = setupDatabase();
    Storage storage = new Storage(banksDataSource);
    assertTrue(storage.connect());

    // Setup tests content
    importSQLFromFile(banksDataSource, "src/test/resources/setup_db_for_tests.sql");

    // Tests
    ArrayList<Transaction> transactions = storage.getNewTransactionsSince(OffsetDateTime.of(2020,9,6,8,0,0, 0, ZoneOffset.UTC));
    assertEquals(1, transactions.size());

    transactions = storage.getNewTransactionsSince(OffsetDateTime.of(2020,9,6,6,0,0,0, ZoneOffset.UTC));
    assertEquals(2, transactions.size());
  }
}
