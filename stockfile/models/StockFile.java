package stockfile.models;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;

import stockfile.security.UserSession;

/**
 * Class describing a File object
 * 
 * @author Bahman
 */
@SuppressWarnings("serial")
public class StockFile extends File {

	private static final String HOME_PATH = UserSession.getInstance().getCurrentClient().getFullDir();
	
	private String remoteHomePath = "/stockfiles/"+UserSession.getInstance().getCurrentUser().getUserName();
	private String relFilePath;
	private float version = (float) 1.0;
	private DateTime lastModifiedDB;
	private Long lastModifiedOnLoad;
	private String lastSyncBy = UserSession.getInstance().getCurrentUser().getUserName();
	private String createdBy = UserSession.getInstance().getCurrentUser().getUserName();
	private boolean inSync;
	private boolean removeMarker;

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
		if (remotePath!=null)
			this.setRemoteHomePath(remotePath);
	}

	public StockFile(String relativePath, String remotePath) {
		super(fixAbsPath(relativePath));
		//System.out.println("SHORT"+" | HOME_PATH: "+HOME_PATH+ " | Rel: " + relativePath);
		resetSync();
		this.setRelativePath(relativePath);
		if (remotePath!=null)
			this.setRemoteHomePath(remotePath);
	}

	// /**
	// * @return the filePath
	// */
	// public String getFullPath() {
	// return this.filePath+"/"+this.fileName;
	// }

	public static String fixAbsPath(String relativePath) {
		return HOME_PATH+(filterPathForWindows(relativePath).replace(HOME_PATH, ""));
	}
	
	public static String filterPathForWindows(String path) {
		return FilenameUtils.separatorsToSystem(path);
	}

	/**
	 * @return the fileName
	 */
	public String getRelativePath() {
		return relFilePath;
	}

	/**
	 * @param relativePath
	 *            the relative path to set
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
	 * @param version
	 *            the version to set
	 */
	public void setVersion(float version) {
		this.version = version;
	}

	public void incrementVersion() {
		if (inSync()) {
			this.version = this.version + ((float) 0.1);
			System.out.println("Incremented file "+getName()+" to "+getVersion());
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
	 * @param lastModified
	 *            the lastModified to set
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
	 * @param lastSyncBy
	 *            the lastSyncBy to set
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
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public String toString() {
		String output = "=============\n"+"FILE DETAILS: \n";
		output += "Name: "+getName()+"\n";
		output += "Path: "+getPath()+"\n";
		output += "Relative Path: "+getRelativePath()+"\n";
		output += "Remote Home Path: "+getRemoteHomePath()+"\n";
		output += "Remote Path: "+getFullRemotePath()+"\n";
		output += "Absolute Path: "+getAbsolutePath()+"\n";
		output += "Version: "+getVersion()+"\n";
		output += "Is Directory: "+isDirectory()+"\n";
		output += "Exists: "+exists()+"\n";
		output += "============";
		return output;

	}

	public boolean inSync() {
		return inSync;
	}

	public void resetSync() {
		this.inSync = true;
	}

	public String getRemoteHomePath() {
		return remoteHomePath;
	}
	
	public String getFullRemotePath() {
		return FilenameUtils.separatorsToUnix(getRemoteHomePath()+getRelativePath());
	}

	public void setRemoteHomePath(String remotePath) {
		this.remoteHomePath = remotePath;
	}

	public boolean hasRemoveMarker() {
		return removeMarker;
	}

	public void setRemoveMarker(boolean removeMarker) {
		this.removeMarker = removeMarker;
	}

	public Long getLastModifiedOnLoad() {
		return lastModifiedOnLoad;
	}

	public void setLastModifiedOnLoad(Long lastModifiedOnLoad) {
		this.lastModifiedOnLoad = lastModifiedOnLoad;
	}
}
