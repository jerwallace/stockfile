/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.dao;

import stockfile.controllers.DNSResolver.ServerType;
import stockfile.dao.connection.RemoteMySQLConnection;
import stockfile.exceptions.ApplicationFailedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * StockFile DAO is the abstract high level DAO with generic commands.
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @projectestset StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
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
        	this.initConnection(ServerType.MASTER);
    }

    /**
     * Initialize a connection.
     * @param type TODO
     */
    protected void initConnection(ServerType type) {
        
        try {
			conn = RemoteMySQLConnection.getInstance().getConnection(type);
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