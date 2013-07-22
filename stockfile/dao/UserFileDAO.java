/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.dao;

import java.sql.SQLException;

import org.apache.commons.io.FilenameUtils;

import stockfile.models.Manifest;
import stockfile.models.StockFile;
import stockfile.security.StockFileSession;

/**
 *
 * @author MrAtheist
 */
public class UserFileDAO extends FileDAO {

    public UserFileDAO() {
        super();
    }

    /**
     * Creates a new StockFile entry in the database with the given StockFile object
     *  and establishes an association between the StockFile object with the current
     *  user within UserSession
     * @param file
     * @return
     * @throws SQLException 
     */
    public boolean createFile(StockFile file) throws SQLException {
        if (super.createFile(file)) {
            try {

                ps = conn.prepareStatement("INSERT INTO "
                        + "user_file (username,file_name,file_path,current_version) "
                        + "VALUES (?,?,?,?);");


                ps.setString(1, StockFileSession.getInstance().getCurrentUser().getUserName());
                ps.setString(2, FilenameUtils.separatorsToUnix(file.getRelativePath()));
                ps.setString(3, FilenameUtils.separatorsToUnix(file.getRemoteHomePath()));
                ps.setFloat(4, file.getVersion());

                ps.executeUpdate();
                System.out.println("Connected user to file in database.");

            } catch (SQLException sqlex) {
            	System.out.println("INS: Error code: "+sqlex.getErrorCode());
                throw sqlex;
            }
            
            this.psclose();
            return true;
            
        } else {
            updateFile(file);
            return false;
        }

        

    }


    /**
     * Updates the entry in file and user_file table that corresponds to the given 
     *  Stockfile object 
     * @param file
     * @throws SQLException 
     */
    public boolean updateFile(StockFile file) throws SQLException {
    	if (super.updateFile(file)) {

    		try {
            	
                ps = conn.prepareStatement("UPDATE user_file "
                        + "SET current_version = ? "
                        + "WHERE file_name = ? AND file_path = ? ");

                ps.setFloat(1, file.getVersion());
                ps.setString(2, FilenameUtils.separatorsToUnix(file.getRelativePath()));
                ps.setString(3, FilenameUtils.separatorsToUnix(file.getRemoteHomePath()));

                ps.executeUpdate();

            } catch (SQLException sqlex) {
            	System.out.println("UPD Error code: "+sqlex.getErrorCode());
                throw sqlex;
            }
            this.psclose();
            return true;
    	} else {
    		createFile(file);
    		return false;
    	}
    }

    /**
     * Removes the entry in the file table that corresponds to the given StockFile
     *  object provided
     * @param file
     * @throws SQLException 
     */
    public void removeFile(StockFile file) throws SQLException {
    	
    	super.removeFile(file);
    	
        try {
            
            ps = conn.prepareStatement("DELETE FROM user_file WHERE username = ? AND file_name = ? AND file_path = ?");

            ps.setString(1, FilenameUtils.separatorsToUnix(StockFileSession.getInstance().getCurrentUser().getUserName()));
            ps.setString(2, FilenameUtils.separatorsToUnix(file.getRelativePath()));
            ps.setString(3, FilenameUtils.separatorsToUnix(file.getRemoteHomePath()));

            ps.executeUpdate();

        } catch (SQLException sqlex) {
            throw sqlex;
        }

        this.psclose();

    }

    /**
     * Returns a Manifest object that corresponds to the current user within UserSession
     * @return
     * @throws SQLException 
     */
    public Manifest generateManifest() throws SQLException {

        Manifest manifest = new Manifest("Server Manifest");

        try {

            ps = conn.prepareStatement("SELECT * FROM user_file "
                    + "JOIN file ON user_file.file_path = file.file_path AND user_file.file_name = file.file_name "
                    + "WHERE username = ?");

            ps.setString(1, StockFileSession.getInstance().getCurrentUser().getUserName());

            rs = ps.executeQuery();

            while (rs.next()) {

                String filename = FilenameUtils.separatorsToSystem(rs.getString("file_name"));

                StockFile thisFile = new StockFile(
                        filename,
                        FilenameUtils.separatorsToUnix(rs.getString("file_path")),
                        rs.getFloat("version"),
                        rs.getTimestamp("last_modified"),
                        rs.getString("last_sync_by"),
                        rs.getString("created_by"));

                manifest.updateFile(filename, thisFile);

            }

        } catch (SQLException sqlex) {
            throw sqlex;
        }
        this.psclose();

        return manifest;
    }
}
