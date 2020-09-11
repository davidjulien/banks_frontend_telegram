package banks.frontend.telegram;

import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

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
    assertNull(storage.getNewTransactionsSince(LocalDateTime.of(2020,9,6,8,0,0)));
  }


  // Tests on real test database
 
  org.postgresql.ds.PGSimpleDataSource setupDatabase() {
    org.postgresql.ds.PGSimpleDataSource realDataSource = new org.postgresql.ds.PGSimpleDataSource();
    realDataSource.setDatabaseName("banks_fetch_test");
    realDataSource.setUser("banks_fetch_user");
    return realDataSource;
  }


  @Test 
  public void shouldConnectToRealTestDatabase() throws Exception {
    org.postgresql.ds.PGSimpleDataSource realDataSource = setupDatabase();

    Storage storage = new Storage(realDataSource);
    assertTrue(storage.connect());
  }

  public static void importSQL(Connection conn, InputStream in) throws SQLException {
    Scanner s = new Scanner(in);
    s.useDelimiter("(;(\r)?\n)|(--\n)");
    Statement st = null;
    try {
      st = conn.createStatement();
      while (s.hasNext()) {
        String line = s.next();
        if (line.trim().length() > 0) {
          st.execute(line);
        }
      }
    } catch (SQLException e) {
      System.err.println("Invalid SQL test file : " + e);
    } finally {
      if (st != null) {
        st.close();
      }
    }
  }

  @Test
  public void shouldGetNewTransactionsSinceLastChecking() throws Exception {
    org.postgresql.ds.PGSimpleDataSource realDataSource = setupDatabase();
    Storage storage = new Storage(realDataSource);

    // Setup database content
    File file = new File("src/test/resources/setup_db_for_tests.sql");
    InputStream in = new FileInputStream(file);
    Connection con = realDataSource.getConnection();
    importSQL(con, in);

    // Tests
    ArrayList<Transaction> transactions = storage.getNewTransactionsSince(LocalDateTime.of(2020,9,6,8,0,0));
    assertEquals(1, transactions.size());

    transactions = storage.getNewTransactionsSince(LocalDateTime.of(2020,9,6,0,0,0));
    assertEquals(2, transactions.size());
  }
}
