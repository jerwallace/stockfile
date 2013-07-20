package stockfile.models;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;

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
        DOWNLOAD,UPLOAD,DUPLICATE,UPLOAD_AND_OVERWRITE,DOWNLOAD_AND_OVERWRITE, NO_ACTION,DELETE
    }
    
    public Manifest()
    {
        manifest = new TreeMap<>();
    }
    
    public void updateFile(String relativePath, StockFile thisFile) 
    {
    		relativePath = FilenameUtils.separatorsToSystem(relativePath);
    		thisFile.setRelativePath(relativePath);
    		System.out.println("Updated item in FileList "+thisFile.getRelativePath());
	        this.manifest.put(thisFile.getRelativePath(), thisFile);
    	
    }

    public void insertFile(String relativePath, StockFile thisFile) 
    {
    	relativePath = FilenameUtils.separatorsToSystem(relativePath);
    	if (!this.manifest.containsKey(relativePath)) {
    		thisFile.setRelativePath(relativePath);
    		System.out.println("Added item to FileList "+thisFile.getRelativePath());
    		this.manifest.put(thisFile.getRelativePath(), thisFile);
    	}
    }
    
    public void removeFile(String relativePath)
    {
        this.manifest.remove(FilenameUtils.separatorsToSystem(relativePath));
    }

    public boolean containsFile(String relativePath)
    {
        return this.manifest.containsKey(FilenameUtils.separatorsToSystem(relativePath));
    }

    public StockFile getFile(String relativePath)
    {  
        return this.manifest.get(FilenameUtils.separatorsToSystem(relativePath));
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
