package stockfile.api;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Bahman
 */
public class Manifest
{

    private Map<String, StockFile> manifest;
    
    public enum Operation {
        DOWNLOAD,UPLOAD,DUPLICATE
    }
    
    public Manifest()
    {
        manifest = new HashMap<>();
    }
    
    public Map<String,StockFile> getManifestMap() {
        return this.manifest;
    }

    public void insertFile(String fullPath, StockFile metaData)
    {
        this.manifest.put(fullPath, metaData);
    }

    public void removeFile(String fullPath)
    {
        this.manifest.remove(fullPath);
    }

    public boolean containsFile(String fullPath)
    {
        return this.manifest.containsKey(fullPath);
    }

    public StockFile getFile(String fullPath)
    {
        return this.manifest.get(fullPath);
    }
    
    public boolean isEqual(Manifest newManifest)
    {
        if (this.manifest.size() != newManifest.manifest.size())
        {
            return false;
        }
        else
        {
            for (Map.Entry<String, StockFile> thisManifestEntry : this.manifest.entrySet())
            {
                if (!newManifest.containsFile(thisManifestEntry.getKey()))
                {
                    return false;
                }
            }
            return true;
        }
    }
    
    @Override
    public String toString() {
        String output = "";
        for (Map.Entry<String, StockFile> thisManifestEntry : this.manifest.entrySet())
            {
                output += thisManifestEntry.getKey()+"\n";
            }
        return output;

    }
}
