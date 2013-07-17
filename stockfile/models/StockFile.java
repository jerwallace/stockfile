package stockfile.models;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;

/**
 * Class describing a File object
 * @author Bahman
 */
@SuppressWarnings("serial")
public class StockFile extends File {

    private String filePath;
    private String fileName;
    private float version;
    private String type;
    
    private DateTime lastModified;
    private String lastSyncBy;
    private String createdBy;
    
    public StockFile(String path, String name, float version, Timestamp lastMod, String lastSyncBy, String createdBy, String type){
        super(name);
    	this.filePath = path;
        this.fileName = name.replace(path,"");
        this.version = version;
        this.lastModified = new DateTime(lastMod);
		this.lastSyncBy = lastSyncBy;
		this.createdBy = createdBy;
		this.setType(type);
    }

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return this.filePath;
	}
        
        /**
	 * @return the filePath
	 */
	public String getFullPath() {
		return this.filePath+"\\"+this.fileName;
	}
        
//        /**
//	 * @return the filePath
//	 */
//	public String getFullPath() {
//		return this.filePath+"/"+this.fileName;
//	}
	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
        
        public void incrementVersion() {
            this.version = this.version+((float)0.1);
        }

	/**
	 * @return the lastModified
	 */
	public DateTime getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
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
        
        public String toString() {
            return getFileName()+" - "+getVersion();
            
        }

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
}
