package ch.crb.showcase.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.enterprise.context.ApplicationScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DatabaseService {

  private static final String DATASOURCE_NAME = "java:jboss/datasources/" + System.getenv("DS_NAME");
  private static final String TEST_SQL = "Select 1 from Dual";

  private static Logger log = LoggerFactory.getLogger(DatabaseService.class);

  public boolean isDbConnected() {
    try {
      Context initialContext = new InitialContext();
      DataSource datasource = (DataSource) initialContext.lookup(DATASOURCE_NAME);
      return executeTestQuery(datasource);
    } catch (NamingException e) {
      log.error("An error occurred while trying to access the datasource " + DATASOURCE_NAME, e);
      return false;
    }
  }

  private static boolean executeTestQuery(DataSource datasource) {
    try (Connection conn = datasource.getConnection(); PreparedStatement stmt = conn.prepareStatement(TEST_SQL)) {
      stmt.executeQuery();
      return true;
    } catch (SQLException e) {
      log.error("An error occurred while trying to access the database and execute a test query: ", e);
    }
    return false;
  }
}