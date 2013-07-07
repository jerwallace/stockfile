package stockfile.api;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Bahman
 */
public class Manifest
{

    private Map<String, File> manifest;

    public Manifest()
    {
        manifest = new HashMap<>();
    }

    public void insertFile(String fullPath, File metaData)
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

    public File getFile(String fullPath)
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
            for (Map.Entry<String, File> thisManifestEntry : this.manifest.entrySet())
            {
                if (!newManifest.containsFile(thisManifestEntry.getKey()))
                {
                    return false;
                }
            }
            return true;
        }
    }

    public Map<String, Float> diff(Manifest newManifest)
    {
        Map<String, Float> differenceList = new HashMap<>();

        if (newManifest.isEqual(this))
        {
            return null;
        }
        else
        {
            for (Map.Entry<String, File> thisManifestEntry : this.manifest.entrySet())
            {
                if (!newManifest.containsFile(thisManifestEntry.getKey()))
                {
                    differenceList.put(thisManifestEntry.getKey(), thisManifestEntry.getValue().getVersion());
                }
            }
            return differenceList;
        }
    }
}
