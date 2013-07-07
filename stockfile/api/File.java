package stockfile.api;

import java.util.Date;

/**
 *
 * @author Bahman
 */
public class File
{

    private String filePath;
    private String fileName;
    private Date lastModified;
    private float version;

    public File(String path, String name, Date lastMod, float ver)
    {
        this.filePath = path;
        this.fileName = name;
        this.lastModified = lastMod;
        this.version = ver;
    }

    public String getFileName()
    {
        return fileName;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public float getVersion()
    {
        return version;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    public void setVersion(float version)
    {
        this.version = version;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }
}
