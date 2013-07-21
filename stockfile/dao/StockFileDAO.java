/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.dao;

import stockfile.controllers.DNSResolver.ServerType;
import stockfile.dao.connection.MySQLConnection;
import stockfile.exceptions.ApplicationFailedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/*
 *
 * @author MrAtheist
 */
public abstract class StockFileDAO {
    protected static Connection conn;
    protected static PreparedStatement ps;
    protected static ResultSet rs;
    protected static ResultSetMetaData rm;

	public enum Table{

		CLIENT, FILE, USER, USER_CLIENT, USER_FILE;
	}
	
    public StockFileDAO(boolean autoStartConnection) {
        conn = null;
        ps = null;
        rs = null;
        rm = null;
        
        if (autoStartConnection)
        	this.initConnection(ServerType.Master);
    }

    /**
     * Initialize a connection.
     * @param type TODO
     */
    protected void initConnection(ServerType type) {
        
        try {
			conn = MySQLConnection.getInstance().getConnection(type);
		} catch (ApplicationFailedException e) {
			System.out.println(e);
			System.exit(0);
		}
    }
    
    /**
     * This method closes the preparedstatement.
     */
    protected void psclose() {
        try {
            if (rs != null) {
                rs.close();
            }
            ps.close();
            //conn.close();
        } catch (SQLException sqlex) {
            System.err.println("SQLException: " + sqlex.getMessage());
            sqlex.printStackTrace();
        }
    }
}