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
public class ServerFileDAO extends FileDAO {

    public ServerFileDAO() {
        super();
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
                    + "JOIN file ON user_file.file_path = file.file_path AND user_file.file_name = file.file_name ");

            rs = ps.executeQuery();

            while (rs.next()) {

                String filename = FilenameUtils.separatorsToSystem(rs.getString("file_path"));

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
