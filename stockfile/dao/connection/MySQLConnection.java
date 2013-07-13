package stockfile.dao.connection;
 
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import stockfile.dao.connection.Utils;


/**
 * This is a singleton class connecting to a MySQL database. 
 * To get a instance of the connection, use getConnection().
 */ 
public class MySQLConnection {
    
    private static MySQLConnection stockfile_sql = null;
    protected Connection connection = null;
    protected boolean driverLoaded = false;
    private Properties props;
    
    protected MySQLConnection() {
 
    }

    /**
     * Static method returns a single instance of MySQLConnection.
     * @return  a single instance of MySQLConnection
     */
    public static MySQLConnection getInstance()  {
        if (stockfile_sql == null) {
            stockfile_sql = new MySQLConnection(); 
        }
        return stockfile_sql;
    }
    /**
     * Loads the MySQL JDBC driver and connects to the database.
     * @return  true if the connection is successful; false otherwise.
     */
    public boolean connect() {
       try {
            props = Utils.readProperties("/stockfile/config/stockfile.properties");
        } catch (IOException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {      
            if (!driverLoaded)  {
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                driverLoaded = true; 
            }
            
            connection = DriverManager.getConnection(props.getProperty("jdbcUrl"), 
                                                     props.getProperty("username"),
                                                     props.getProperty("password"));
            
            return true; 
        } catch (SQLException sqlex) {
            //System.err.println("SQLException: " + sqlex.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the connection.
     * @return  the Connection object
     */
    public Connection getConnection() {
        //System.err.println("This Connection: " + connection);
        try {
            if (connection == null||(!connection.isValid(0))) {
                this.connect(); 
            } else {
                return this.connection; 
            }
        } catch (SQLException ex) {
            
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }

    /**
     * Check whether the JDBC driver is loaded.
     * @return  true if the driver is loaded; false otherwise
     */
    public boolean isDriverLoaded() {
        return driverLoaded; 
    }

    /**
     * This method allows members of this class to clean up after itself 
     * before it is garbage collected. It is called by the garbage collector.
     */ 
//    @Override
//    protected void finalize() throws Throwable {
//        if (connection != null) {
//            connection.close();
//        }
//        // finalize() must call super.finalize() as the last thing it does
//        super.finalize();     
//    }
}