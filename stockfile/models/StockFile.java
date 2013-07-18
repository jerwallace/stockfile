package stockfile.models;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;

/**
 * Class describing a File object
 * 
 * @author Bahman
 */
@SuppressWarnings("serial")
public class StockFile extends File {

	private String homePath;
	private String relFilePath;
	private float version;
	private DateTime lastModified;
	private String lastSyncBy;
	private String createdBy;
	private boolean inSync;

	public StockFile(String homePath, String fullPath, float version,
			Timestamp lastMod, String lastSyncBy, String createdBy) {
		super(fullPath);
		resetSync();
		this.homePath = homePath;
		this.relFilePath = fullPath.replace(homePath, "");
		this.version = version;
		this.lastModified = new DateTime(lastMod);
		this.lastSyncBy = lastSyncBy;
		this.createdBy = createdBy;
	}

	public StockFile(String homePath, String fullPath) {
		super(fullPath);
		resetSync();
		this.homePath = fullPath;
		this.relFilePath = fullPath.replace(homePath, "");
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return this.homePath;
	}

	// /**
	// * @return the filePath
	// */
	// public String getFullPath() {
	// return this.filePath+"/"+this.fileName;
	// }
	/**
	 * @param homePath
	 *            the filePath to set
	 */
	public void setHomePath(String homePath) {
		this.homePath = homePath;
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
		this.relFilePath = relativePath;
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
	public DateTime getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified
	 *            the lastModified to set
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = new DateTime(lastModified);
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

	public String toString() {
		return getRelativePath() + " - " + getVersion();

	}

	private boolean inSync() {
		return inSync;
	}

	private void resetSync() {
		this.inSync = true;
	}
}
