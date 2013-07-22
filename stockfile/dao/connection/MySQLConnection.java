package stockfile.dao.connection;
 
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import stockfile.controllers.DNSResolver.ServerType;
import stockfile.exceptions.ApplicationFailedException;


/**
 * Abstract connection class to be used by both client and server.
 */ 
public abstract class MySQLConnection {
    
    protected Connection connection = null;
    protected boolean driverLoaded = false;
    protected Properties props;
    
    
    /**
     * Loads the MySQL JDBC driver and connects to the database.
     * @return  true if the connection is successful; false otherwise.
     * @throws ApplicationFailedException 
     */
    public abstract boolean connect(ServerType type) throws ApplicationFailedException;
    
    /**
     * Gets the connection.
     * @return  the Connection object
     * @throws ApplicationFailedException 
     */
    public Connection getConnection(ServerType type) throws ApplicationFailedException {
        //System.err.println("This Connection: " + connection);
        try {
            if (connection == null||(!connection.isValid(0))) {
                this.connect(type); 
            } else {
                return this.connection; 
            }
        } catch (SQLException ex) {
            
            Logger.getLogger(RemoteMySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
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
    @Override
    protected void finalize() throws Throwable {
        if (connection != null) {
            connection.close();
       }
        // finalize() must call super.finalize() as the last thing it does
        super.finalize();     
    }
}