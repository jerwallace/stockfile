/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.dao;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import stockfile.api.User;
import stockfile.api.StockFile;
import stockfile.api.Manifest;
import stockfile.client.Client;
import static stockfile.dao.StockFileDAO.ps;
/**
 *
 * @author MrAtheist
 */
public class FileDAO extends StockFileDAO{
	
	public FileDAO () {
		super();
	}

	public void createFile(StockFile file) throws SQLException {
                
		this.initConnection();
                
                try {
                    
			ps = conn.prepareStatement("INSERT INTO "
                                + "file (version,last_sync_by,created_by,file_name,file_path,) "
                                + "VALUES (?,?,?,?,?);");
                        
                        ps.setFloat(1, file.getVersion());
			ps.setString(2, file.getLastSyncBy());
                        ps.setString(3, file.getCreatedBy());
                        ps.setString(4, file.getFileName());
			ps.setString(5, file.getFilePath());

			ps.executeQuery();
                        
		} catch (SQLException sqlex) {
			throw sqlex;
		}

	}

        public boolean inDatabase(StockFile file) throws SQLException {
                
		this.initConnection();
                
                try {
                    
			ps = conn.prepareStatement("SELECT * FROM file WHERE file_name = ? AND file_path = ?");
                        
                        ps.setString(1, file.getFileName());
			ps.setString(2, file.getFilePath());
                        
                        ps.executeQuery();
                        if (rs.next()) {
                            return true;
                        } else {
                            return false;
                        }
                        
 		} catch (SQLException sqlex) {
			throw sqlex;
		}

	}
        
	public void updateFile(StockFile file) throws SQLException {

		this.initConnection();
                
                try {
                    
			ps = conn.prepareStatement("UPDATE file "
                                + "SET version = ?, last_sync_by = ?,created_by = ? "
                                + "WHERE file_name = ? AND file_path = ? ");
                        
                        ps.setFloat(1, file.getVersion());
			ps.setString(2, file.getLastSyncBy());
                        ps.setString(3, file.getCreatedBy());
                        ps.setString(4, file.getFileName());
			ps.setString(5, file.getFilePath());

			rs = ps.executeQuery();

		} catch (SQLException sqlex) {
			throw sqlex;
		}
	}

	public void removeFile(StockFile file) throws SQLException {

		this.initConnection();
                
                try {
                    
			ps = conn.prepareStatement("DELETE FROM file WHERE file_name = ? AND file_path = ?");
                        
                        ps.setString(1, file.getFileName());
			ps.setString(2, file.getFilePath());
                        
                        ps.executeQuery();
		} catch (SQLException sqlex) {
			throw sqlex;
		}

	}

	public Manifest generateManifest(User user) throws SQLException {

		this.initConnection();
		Manifest manifest = new Manifest();
                
                try {
                    
			ps = conn.prepareStatement("SELECT file FROM user_file "
                                + "JOIN file ON user_file.file_path = file.file_path AND user_file.file_name = file.file_name "
                                + "WHERE username = ?");
                        ps.setString(1, user.getUserName());
                        
                        rs = ps.executeQuery();

                        while (rs.next()) {
                            
                            String filename = rs.getString("file_name");
                            String filepath = rs.getString("file_path");
                            StockFile thisFile = new StockFile(
                                    rs.getString("file_path"),
                                    rs.getString("file_name"),
                                    rs.getFloat("version"),
                                    new DateTime(rs.getString("last_modified")),
                                    rs.getString("last_sync_by"),
                                    rs.getString("last_created_by")
                                    );
                            
                            manifest.insertFile((filepath+"/"+filename), thisFile);
                            
                        }
                        
		} catch (SQLException sqlex) {
			throw sqlex;
		}

		return manifest;
	}
}