/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.dao;

import java.sql.SQLException;

import org.apache.commons.io.FilenameUtils;

import stockfile.models.Manifest;
import stockfile.models.StockFile;

/**
 * ServerFileDAO has specialized methods for a server instance.
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class ServerFileDAO extends FileDAO {

    public ServerFileDAO() {
        super();
    }

    /**
     * Returns a Manifest object that has all files in the system.
     * @return
     * @throws SQLException 
     */
    @Override
	public Manifest generateManifest() throws SQLException {

        Manifest manifest = new Manifest("Server Manifest");

        try {

            ps = conn.prepareStatement("SELECT * FROM file; ");

            rs = ps.executeQuery();

            while (rs.next()) {

                String filename = FilenameUtils.separatorsToSystem(rs.getString("file_path").replace("/stockfiles/",""))+rs.getString("file_name");

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
