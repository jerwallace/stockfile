package stockfile.api;

import org.joda.time.DateTime;

/**
 *
 * @author Bahman
 */
public class File
{

    private String filePath;
    private String fileName;
    private DateTime lastModified;
    private float version;

    public File(String path, String name, DateTime lastMod, float ver)
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

    public DateTime getLastModified()
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

    public void setLastModified(DateTime lastModified)
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
