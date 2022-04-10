import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DBUtils {
    /**
     * Connects to the database if it exists, creates it if it does not, and returns
     * the connection object.
     *
     * @param databaseFileName the database file name
     * @return a connection object to the designated database
     */
    public static Connection initializeConnection(String databaseFileName) {
	/**
	 * The "Connection String" or "Connection URL".
	 *
	 * "jdbc:sqlite:" is the "subprotocol". (If this were a SQL Server database it
	 * would be "jdbc:sqlserver:".)
	 */
	String url = "jdbc:sqlite:" + databaseFileName;
	Connection conn = null; // If you create this variable inside the Try block it will be out of scope
	try {
	    conn = DriverManager.getConnection(url);
	    if (conn != null) {
		// Provides some positive assurance the connection and/or creation was
		// successful.
		DatabaseMetaData meta = conn.getMetaData();
		System.out.println("The connection to the database was successful.");
	    } else {
		// Provides some feedback in case the connection failed but did not throw an
		// exception.
		System.out.println("Null Connection");
	    }
	} catch (SQLException e) {
	    System.out.println(e.getMessage());
	    System.out.println("There was a problem connecting to the database.");
	}
	return conn;
    }

    public static ResultSet queryConnection(Connection conn, PreparedStatement ps) {
	ResultSet rs = null;
	try {
	    rs = ps.executeQuery();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return rs;
    }

    public static void updateQueryConnection(Connection conn, PreparedStatement ps) {
	try {
	    ps.executeUpdate();
	} catch (SQLException e) {
	    try {
		conn.rollback();
	    } catch (SQLException e1) {
		e1.printStackTrace();
	    }
	    e.printStackTrace();
	}
    }

    public static void printResultSet(ResultSet rs) {
	try {
	    ResultSetMetaData rsmd = rs.getMetaData();
	    int columnCount = rsmd.getColumnCount();
	    for (int i = 1; i <= columnCount; i++) {
		String value = rsmd.getColumnName(i);
		System.out.print(value);
		if (i < columnCount) {
		    System.out.print(",  ");
		}
	    }
	    System.out.print("\n");
	    while (rs.next()) {
		for (int i = 1; i <= columnCount; i++) {
		    String columnValue = rs.getString(i);
		    System.out.print(columnValue);
		    if (i < columnCount) {
			System.out.print(",  ");
		    }
		}
		System.out.print("\n");
	    }
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
