/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.dao;

import java.io.File;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import stockfile.client.UserSession;
import stockfile.models.Client;
import stockfile.models.FileList;
import stockfile.models.Manifest;
import stockfile.models.StockFile;
import stockfile.models.User;
import static stockfile.dao.StockFileDAO.ps;
/**
 *
 * @author MrAtheist
 */
public class FileDAO extends StockFileDAO{
	
	public FileDAO () {
		super();
	}
	
	public int createFile(StockFile file) throws SQLException {
		
		try {
			
			ps = conn.prepareStatement("INSERT INTO "
					+ "file (version,last_sync_by,created_by,file_name,file_path) "
					+ "VALUES (?,?,?,?,?);");
			
			ps.setFloat(1, file.getVersion());
			ps.setString(2, file.getLastSyncBy());
			ps.setString(3, file.getCreatedBy());
			ps.setString(4, file.getFileName());
			ps.setString(5, file.getFilePath());
			
			ps.executeUpdate();
			System.out.println("Added file to database.");
			
            ps = conn.prepareStatement("INSERT INTO "
					+ "user_file (username,file_name,file_path,current_version) "
					+ "VALUES (?,?,?,?);");
			
			
			ps.setString(1, UserSession.getInstance().getCurrentUser().getUserName());
			ps.setString(2, file.getFileName());
			ps.setString(3, file.getFilePath());			
			ps.setFloat(4, file.getVersion());

			ps.executeUpdate();
			System.out.println("Connected user to file in database.");
			
		} catch (SQLException sqlex) {
			throw sqlex;
		}
		
		this.psclose();
		return 0;
		
	}
	
	public void getFiles() throws SQLException {
		
		try {
			
			ps = conn.prepareStatement("SELECT * FROM file");

                        rs = ps.executeQuery();
                        System.out.println("res = ");

                        while (rs.next()) {
                           //
                        }
                        
 		} catch (SQLException sqlex) {
			throw sqlex;
		}
		
		this.psclose();
		
	}
	
	public boolean inDatabase(StockFile file) throws SQLException {
		
		try {
			
			ps = conn.prepareStatement("SELECT * FROM file WHERE file_name = ? AND file_path = ?");
			
			ps.setString(1, file.getFileName());
			ps.setString(2, file.getFilePath());
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				this.psclose();
				return true;
			} else {
				this.psclose();
				return false;
			}
			
		} catch (SQLException sqlex) {
			throw sqlex;
		}
		
	}
	
	public void updateFile(StockFile file) throws SQLException {
	
                if (!inDatabase(file)) {
                    createFile(file);
                } else {
                    try {

                            ps = conn.prepareStatement("UPDATE file "
                                            + "SET version = ?, last_sync_by = ?,created_by = ? "
                                            + "WHERE file_name = ? AND file_path = ? ");

                            ps.setFloat(1, file.getVersion());
                            ps.setString(2, file.getLastSyncBy());
                            ps.setString(3, file.getCreatedBy());
                            ps.setString(4, file.getFileName());
                            ps.setString(5, file.getFilePath());

                            ps.executeUpdate();

                    } catch (SQLException sqlex) {
                            throw sqlex;
                    }

                    this.psclose();
                }
	}
	
	public void removeFile(StockFile file) throws SQLException {
		
		try {
			
			ps = conn.prepareStatement("DELETE FROM file WHERE file_name = ? AND file_path = ?");
			
			ps.setString(1, file.getFileName());
			ps.setString(2, file.getFilePath());
			
			ps.executeUpdate();
			
		} catch (SQLException sqlex) {
			throw sqlex;
		}
		
		this.psclose();
		
	}
	
	public Manifest generateManifest() throws SQLException {
		
		Manifest manifest = new Manifest();
		System.out.println(conn);
		try {
			
			ps = conn.prepareStatement("SELECT * FROM user_file "
					+ "JOIN file ON user_file.file_path = file.file_path AND user_file.file_name = file.file_name "
					+ "WHERE username = 'testuser'");
			
			//ps.setString(1, UserSession.getInstance().getCurrentUser().getUserName());
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
			 
				String filename = rs.getString("file_name");
				String filepath = rs.getString("file_path");
				
				StockFile thisFile = new StockFile(
						filepath,
						filename,
						rs.getFloat("version"),
						new DateTime(rs.getString("last_modified")),
						rs.getString("last_sync_by"),
						rs.getString("created_by")
						);
				
				System.out.println("run ");
				System.out.println(thisFile);
				
				manifest.insertFile((filepath+"/"+filename), thisFile);
				
			}
			
		} catch (SQLException sqlex) {
			throw sqlex;
		}
		this.psclose();
		
		return manifest;
	}
}
