package stockfile.models;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;

import stockfile.security.StockFileSession;

/**
 * Stockfile extends a Java File object and adds metadata of a file in our system.
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
@SuppressWarnings("serial")
public class StockFile extends File {

	// Home path of the client that is connected.
    private static final String HOME_PATH = StockFileSession.getInstance().getCurrentClient().getFullDir();
    
    // The remote location of the files.
    private String remoteHomePath = "/stockfiles/" + StockFileSession.getInstance().getCurrentUser().getUserName();
    
    // Relative file path to the users directory.
    private String relFilePath;
    
    // Current version number.
    private float version = (float) 1.0;
    
    // The last synced time / last database record modified time on the server.
    private DateTime lastModifiedDB;
    
    // The last modified time on load of this object.
    private Long lastModifiedOnLoad;
    
    // User information.
    private String lastSyncBy = StockFileSession.getInstance().getCurrentUser().getUserName();
    private String createdBy = StockFileSession.getInstance().getCurrentUser().getUserName();
    
    // In sync flag... When a file is changed, this flag is set to false.
    private boolean inSync;
    
    // When this flag is set, the file is removed from the database.
    private boolean removeMarker;

    /**
     * Stock file constructor creates a new stock file object.
     * @param relativePath The relative path to the users home directory... [user-home]relative/path.file
     * @param remotePath The remote path of the file.
     * @param version The version number
     * @param lastMod The last time synced.
     * @param lastSyncBy The last sync person.
     * @param createdBy The person who created the file.
     */
    public StockFile(String relativePath, String remotePath, float version,
            Timestamp lastMod, String lastSyncBy, String createdBy) {
        super(fixAbsPath(relativePath));
        //System.out.println("LONG"+" | HOME_PATH: "+HOME_PATH+ " | Rel: " + relativePath);
        resetSync();
        this.setRelativePath(relativePath);
        
        this.setVersion(version);
        this.setLastSyncTimeDB(lastMod);
        this.setLastSyncBy(lastSyncBy);
        this.setCreatedBy(createdBy);
        if (remotePath != null)
            this.setRemoteHomePath(remotePath);
    }

    /**
     * Stock file constructor with only a relative and remote path.
     * @param relativePath The relative path to the users home directory... [user-home]relative/path.file
     * @param remotePath The remote path of the file.
     */
    public StockFile(String relativePath, String remotePath) {
        super(fixAbsPath(relativePath));
        //System.out.println("SHORT"+" | HOME_PATH: "+HOME_PATH+ " | Rel: " + relativePath);
        resetSync();
        this.setRelativePath(relativePath);
        if (remotePath != null)
            this.setRemoteHomePath(remotePath);
    }

    /**
     * HELPER FUNCTION: Fix the absolute path for windows.
     * @param relativePath The relative path to the users home directory... [user-home]relative/path.file
     * @return The fixed absolute path.
     */
    public static String fixAbsPath(String relativePath) {
        return HOME_PATH + (filterPathForWindows(relativePath).replace(HOME_PATH, ""));
    }

    /**
     * HELPER FUNCTION: Filter paths for windows using backslashes.
     * @param path Path to filter.
     * @return The filtered path.
     */
    public static String filterPathForWindows(String path) {
        return FilenameUtils.separatorsToSystem(path);
    }

    /**
     * @return The relative path to the users home directory... [user-home]relative/path.file
     */
    public String getRelativePath() {
        return relFilePath;
    }

    /**
     * @param relativePath The relative path to the users home directory... [user-home]relative/path.file
     */
    public void setRelativePath(String relativePath) {
    	
    	this.relFilePath = filterPathForWindows(relativePath).replace(HOME_PATH, "");

    }

    /**
     * @return the version
     */
    public float getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(float version) {
        this.version = version;
    }

    /**
     * increments the version of the attached StockFile
     */
    public void incrementVersion() {
        if (inSync()) {
            this.version = this.version + ((float) 0.1);
            System.out.println("Incremented file " + getName() + " to " + getVersion());
            this.inSync = false;
        }
    }

    /**
     * @return the lastModified
     */
    public DateTime getLastSyncTimeDB() {
        return lastModifiedDB;
    }

    /**
     * @param lastModified the lastModified to set
     */
    public void setLastSyncTimeDB(Date lastModified) {
        this.lastModifiedDB = new DateTime(lastModified);
    }

    /**
     * @return the lastSyncBy
     */
    public String getLastSyncBy() {
        return lastSyncBy;
    }

    /**
     * @param lastSyncBy the lastSyncBy to set
     */
    public void setLastSyncBy(String lastSyncBy) {
        this.lastSyncBy = lastSyncBy;
    }

    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        String output = "=============\n" + "FILE DETAILS: \n";
        output += "Name: " + getName() + "\n";
        output += "Path: " + getPath() + "\n";
        output += "Relative Path: " + getRelativePath() + "\n";
        output += "Remote Home Path: " + getRemoteHomePath() + "\n";
        output += "Remote Path: " + getFullRemotePath() + "\n";
        output += "Absolute Path: " + getAbsolutePath() + "\n";
        output += "Version: " + getVersion() + "\n";
        output += "Is Directory: " + isDirectory() + "\n";
        output += "Exists: " + exists() + "\n";
        output += "============";
        return output;

    }
    
    /**
     * @return The in sync flag.
     */
    public boolean inSync() {
        return inSync;
    }

    /**
     * Resets the flag to "in sync" or true.
     */
    public void resetSync() {
        this.inSync = true;
    }

    /**
     * @return The remote path.
     */
    public String getRemoteHomePath() {
        return remoteHomePath;
    }

    /**
     * @return The absolute remote path.
     */
    public String getFullRemotePath() {
        return FilenameUtils.separatorsToUnix(getRemoteHomePath() + getRelativePath());
    }

    /**
     * Sets the absolute remote path.
     * @param remotePath The absolute remote path.
     */
    public void setRemoteHomePath(String remotePath) {
        this.remoteHomePath = remotePath;
    }

    /**
     * 
     * @return The delete / remove marker.
     */
    public boolean hasRemoveMarker() {
        return removeMarker;
    }

    /**
     * 
     * @param removeMarker Set the flag to remove / delete a file.
     */
    public void setRemoveMarker(boolean removeMarker) {
        this.removeMarker = removeMarker;
    }

    /**
     * 
     * @return The last modified date on last sync (last load of this object).
     */
    public Long getLastModifiedOnLoad() {
        return lastModifiedOnLoad;
    }

    /**
     * 
     * @param lastModifiedOnLoad The last modified date on load.
     */
    public void setLastModifiedOnLoad(Long lastModifiedOnLoad) {
        this.lastModifiedOnLoad = lastModifiedOnLoad;
    }
}
