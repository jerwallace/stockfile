package stockfile.models;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;

import stockfile.security.UserSession;

/**
 * Class describing a File object
 * 
 * @author Bahman
 */
@SuppressWarnings("serial")
public class StockFile extends File {

	private static final String HOME_PATH = UserSession.getInstance().getCurrentUser().getHomeDirectory();
	
	private String remoteHomePath = "/stockfiles/"+UserSession.getInstance().getCurrentUser().getUserName();
	private String relFilePath;
	private float version = (float) 1.0;
	private DateTime lastModifiedDB;
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
		this.setLastModifiedDB(lastMod);
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
		return HOME_PATH+relativePath.replace(HOME_PATH, "");
	}
	
	public String filterPathForWindows(String path) {
		System.out.println(System.getProperty("os.name").toLowerCase());
		if (System.getProperty("os.name").toLowerCase().equals("win")) {
			path.replace("/", "\\");
		}
		return path;
	}

	/**
	 * @return the fileName
	 */
	public String getRelativePath() {
		return filterPathForWindows(relFilePath);
	}

	/**
	 * @param relativePath
	 *            the relative path to set
	 */
	public void setRelativePath(String relativePath) {
		this.relFilePath = relativePath.replace(HOME_PATH, "");
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
			this.inSync = false;
		}
	}

	/**
	 * @return the lastModified
	 */
	public DateTime getLastModifiedDB() {
		return lastModifiedDB;
	}

	/**
	 * @param lastModified
	 *            the lastModified to set
	 */
	public void setLastModifiedDB(Date lastModified) {
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
		output += "============";
		return output;

	}

	private boolean inSync() {
		return inSync;
	}

	private void resetSync() {
		this.inSync = true;
	}

	public String getRemoteHomePath() {
		return remoteHomePath;
	}
	
	public String getFullRemotePath() {
		return getRemoteHomePath()+getRelativePath();
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
}
