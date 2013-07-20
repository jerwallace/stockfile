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
    private String name = "";
    public enum Operation {
        DOWNLOAD,UPLOAD,DUPLICATE,UPLOAD_AND_OVERWRITE,DOWNLOAD_AND_OVERWRITE, NO_ACTION,DELETE
    }
    
    public Manifest(String name)
    {
        manifest = new TreeMap<>();
        this.setName(name);
    }
    
    public void updateFile(String relativePath, StockFile thisFile) 
    {
    		relativePath = FilenameUtils.separatorsToSystem(relativePath);
    		thisFile.setRelativePath(relativePath);
    		if ((!this.manifest.containsKey(thisFile.getRelativePath()))) {
    			System.out.println("Added item in "+getName()+": "+thisFile.getRelativePath());
    		}
    		else if (thisFile.getVersion()==this.manifest.get(thisFile.getRelativePath()).getVersion()) {
    			System.out.println("Updated item in "+getName()+": "+thisFile.getRelativePath());
    		}
	        this.manifest.put(thisFile.getRelativePath(), thisFile);
    	
    }

    public void insertFile(String relativePath, StockFile thisFile) 
    {
    	relativePath = FilenameUtils.separatorsToSystem(relativePath);
    	
    	if (!this.manifest.containsKey(relativePath)||thisFile.getVersion()>this.manifest.get(relativePath).getVersion()) {
    		thisFile.setRelativePath(relativePath);
    		System.out.println("Added item to "+getName()+": "+thisFile.getRelativePath());
    		this.manifest.put(thisFile.getRelativePath(), thisFile);
    	} else if (thisFile.lastModified()>this.manifest.get(relativePath).lastModified()) {
    		System.out.println("File was modified, incremented version. Added item to "+getName()+": "+thisFile.getRelativePath());
    		this.manifest.put(thisFile.getRelativePath(), thisFile);
    		this.manifest.get(thisFile.getRelativePath()).incrementVersion();
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
        String output = "=========\n"+getName()+"=======\nKey (Version) || Type\n";
        for (String key : this.manifest.keySet())
            {
                output += key+" ("+this.manifest.get(key).getVersion()+") || Directory: "+this.manifest.get(key).isDirectory()+"\n";
            }
        return output;

    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
