package stockfile.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Bahman
 */
public class Manifest implements Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = -6177083960456115163L;
	public Map<String, StockFile> manifest;
    
    public enum Operation {
        DOWNLOAD,UPLOAD,DUPLICATE,UPLOAD_AND_OVERWRITE,DOWNLOAD_AND_OVERWRITE, NO_ACTION
    }
    
    public Manifest()
    {
        manifest = new TreeMap<>();
    }
    
    public void updateFile(String fullPath, StockFile thisFile)
    {
    	System.out.println("Updated item in FileList "+fullPath);
    	thisFile.setRelativePath(fullPath);
        this.manifest.put(fullPath, thisFile);
    }

    public void insertFile(String fullPath, StockFile thisFile)
    {
    	if (!this.manifest.containsKey(fullPath)) {
    		System.out.println("Added item to FileList "+fullPath);
    		thisFile.setRelativePath(fullPath);
    		this.manifest.put(fullPath, thisFile);
    	}
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
    	Set<StockFile> values1 = new HashSet<StockFile>(newManifest.manifest.values());
    	Set<StockFile> values2 = new HashSet<StockFile>(newManifest.manifest.values());
    	return values1.equals(values2);
    }
    
    @Override
    public String toString() {
        String output = "\nKey (Version) || Type\n";
        for (String key : this.manifest.keySet())
            {
                output += key+" ("+this.manifest.get(key).getVersion()+") || Directory: "+this.manifest.get(key).isDirectory()+"\n";
            }
        return output;

    }

}
