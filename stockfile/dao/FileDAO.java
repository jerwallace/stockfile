
package stockfile.dao;

import java.sql.SQLException;

import org.apache.commons.io.FilenameUtils;

import stockfile.models.Manifest;
import stockfile.models.StockFile;
import stockfile.security.StockFileSession;

/**
 * File DAO
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public abstract class FileDAO extends StockFileDAO {

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
    public boolean createFile(StockFile file) throws SQLException {
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

            } catch (SQLException sqlex) {
            	System.out.println("INS: Error code: "+sqlex.getErrorCode());
                throw sqlex;
            }
        } else {
            updateFile(file);
            return false;
        }
        
        this.psclose();
        return true;

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
     * @return TODO
     * @throws SQLException 
     */
    public boolean updateFile(StockFile file) throws SQLException {

        if (!inDatabase(file)) {
            file.setRemoteHomePath("/stockfiles/" + StockFileSession.getInstance().getCurrentUser().getUserName());
            createFile(file);
            return false;
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

            } catch (SQLException sqlex) {
            	System.out.println("UPD Error code: "+sqlex.getErrorCode());
                throw sqlex;
            }
            this.psclose();
            return true;
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
    public abstract Manifest generateManifest() throws SQLException;
}
