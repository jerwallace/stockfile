package stockfile.dao.connection;
 
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import stockfile.controllers.DNSResolver;
import stockfile.controllers.DNSResolver.ServerType;
import stockfile.dao.connection.Utils;
import stockfile.exceptions.ApplicationFailedException;


/**
 * This is a singleton class connecting to a MySQL database. 
 * To get a instance of the connection, use getConnection().
 */ 
public class RemoteMySQLConnection extends MySQLConnection {
    
    private static RemoteMySQLConnection stockfile_sql = null;
    
    protected RemoteMySQLConnection() {
 
    }

    /**
     * Static method returns a single instance of MySQLConnection.
     * @return  a single instance of MySQLConnection
     */
    public static RemoteMySQLConnection getInstance()  {
        if (stockfile_sql == null) {
            stockfile_sql = new RemoteMySQLConnection(); 
        }
        return stockfile_sql;
    }
    
    /**
     * Loads the MySQL JDBC driver and connects to the database.
     * @return  true if the connection is successful; false otherwise.
     * @throws ApplicationFailedException 
     */
    public boolean connect(ServerType type) throws ApplicationFailedException {
       try {
            props = Utils.readProperties("/stockfile/config/stockfile.properties");
        } catch (IOException ex) {
            Logger.getLogger(RemoteMySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {      
            if (!driverLoaded)  {
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                driverLoaded = true; 
            }
            
            connection = DriverManager.getConnection("jdbc:mysql://"+DNSResolver.getInstance(type).getServerDefault()+":3306/Stockfile", 
                                                     props.getProperty("username"),
                                                     props.getProperty("password"));
            
            return true; 
        } catch (SQLException sqlex) {
            System.err.println("SQLException: " + sqlex.getMessage());
            return false;
        }
    }
    
}