/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.dao;

import java.sql.SQLException;

import org.apache.commons.io.FilenameUtils;

import stockfile.models.Manifest;
import stockfile.models.StockFile;
import stockfile.security.UserSession;

/**
 *
 * @author MrAtheist
 */
public class FileDAO extends StockFileDAO {

    public FileDAO() {
        super(true);
    }

    /**
     * Creates a new StockFile entry in the database with the given StockFile object
     *  and establishes an association between the StockFile object with the current
     *  user within UserSession
     * @param file
     * @return
     * @throws SQLException 
     */
    public int createFile(StockFile file) throws SQLException {
        if (!inDatabase(file)) {
            try {

                ps = conn.prepareStatement("INSERT INTO "
                        + "file (version,last_sync_by,created_by,file_name,file_path) "
                        + "VALUES (?,?,?,?,?);");

                ps.setFloat(1, file.getVersion());
                ps.setString(2, file.getLastSyncBy());
                ps.setString(3, file.getCreatedBy());
                ps.setString(4, FilenameUtils.separatorsToUnix(file.getRelativePath()));
                ps.setString(5, FilenameUtils.separatorsToUnix(file.getRemoteHomePath()));

                ps.executeUpdate();
                System.out.println("Added file to database.");

                ps = conn.prepareStatement("INSERT INTO "
                        + "user_file (username,file_name,file_path,current_version) "
                        + "VALUES (?,?,?,?);");


                ps.setString(1, UserSession.getInstance().getCurrentUser().getUserName());
                ps.setString(2, FilenameUtils.separatorsToUnix(file.getRelativePath()));
                ps.setString(3, FilenameUtils.separatorsToUnix(file.getRemoteHomePath()));
                ps.setFloat(4, file.getVersion());

                ps.executeUpdate();
                System.out.println("Connected user to file in database.");

            } catch (SQLException sqlex) {
            	System.out.println("INS: Error code: "+sqlex.getErrorCode());
                throw sqlex;
            }
        } else {
            updateFile(file);
        }

        this.psclose();
        return 0;

    }

    /**
     * Checks if the given StockFile already exists in the database
     * @param file
     * @return true if the given StockFile already exists in the database
     * @throws SQLException 
     */
    public boolean inDatabase(StockFile file) throws SQLException {

        try {

            ps = conn.prepareStatement("SELECT * FROM file WHERE file_name = ? AND file_path = ?");

            ps.setString(1, FilenameUtils.separatorsToUnix(file.getRelativePath()));
            ps.setString(2, FilenameUtils.separatorsToUnix(file.getRemoteHomePath()));

            rs = ps.executeQuery();

            if (rs.next()) {
                this.psclose();
                return true;
            } else {
                this.psclose();
                return false;
            }

        } catch (SQLException sqlex) {
        	System.out.println("FIND: Error code: "+sqlex.getErrorCode());
            throw sqlex;
        }

    }

    /**
     * Updates the entry in file and user_file table that corresponds to the given 
     *  Stockfile object 
     * @param file
     * @throws SQLException 
     */
    public void updateFile(StockFile file) throws SQLException {

        if (!inDatabase(file)) {
            file.setRemoteHomePath("/stockfiles/" + UserSession.getInstance().getCurrentUser().getUserName());
            createFile(file);
        } else {
            try {

                ps = conn.prepareStatement("UPDATE file "
                        + "SET version = ?, last_sync_by = ?,created_by = ? "
                        + "WHERE file_name = ? AND file_path = ? ");

                ps.setFloat(1, file.getVersion());
                ps.setString(2, file.getLastSyncBy());
                ps.setString(3, file.getCreatedBy());
                ps.setString(4, FilenameUtils.separatorsToUnix(file.getRelativePath()));
                ps.setString(5, FilenameUtils.separatorsToUnix(file.getRemoteHomePath()));

                ps.executeUpdate();

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
        }
    }

    /**
     * Removes the entry in the file table that corresponds to the given StockFile
     *  object provided
     * @param file
     * @throws SQLException 
     */
    public void removeFile(StockFile file) throws SQLException {

        try {

            ps = conn.prepareStatement("DELETE FROM file WHERE file_name = ? AND file_path = ?");

            ps.setString(1, FilenameUtils.separatorsToUnix(file.getRelativePath()));
            ps.setString(2, FilenameUtils.separatorsToUnix(file.getRemoteHomePath()));

            ps.executeUpdate();
            
            ps = conn.prepareStatement("DELETE FROM user_file WHERE username = ? AND file_name = ? AND file_path = ?");

            ps.setString(1, FilenameUtils.separatorsToUnix(UserSession.getInstance().getCurrentUser().getUserName()));
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

            ps.setString(1, UserSession.getInstance().getCurrentUser().getUserName());

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
