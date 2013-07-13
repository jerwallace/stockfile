/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.dao;

import stockfile.dao.connection.MySQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	
    public StockFileDAO() {
        conn = null;
        ps = null;
        rs = null;
        rm = null;
        this.initConnection();
    }

    /**
     * Initialize a connection.
     */
    protected void initConnection() {
        
        conn = MySQLConnection.getInstance().getConnection();
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