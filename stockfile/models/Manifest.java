package stockfile.models;

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
public class Manifest implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6177083960456115163L;
    public Map<String, StockFile> manifest;
    private String name = "";

    public enum Operation {

        DOWNLOAD, UPLOAD, DUPLICATE, UPLOAD_AND_OVERWRITE, DOWNLOAD_AND_OVERWRITE, NO_ACTION, DELETE
    }

    /**
     * Default constructor accepts a manifest identifier
     * @param name 
     */
    public Manifest(String name) {
        manifest = new TreeMap<>();
        this.setName(name);
    }

    /**
     * Updates the manifest with the relativePath and StockFile (thisFile)
     * provided. thisFile is added to the manifest if the corresponding
     * relativePath is not found
     *
     * @param relativePath
     * @param thisFile
     */
    public void updateFile(String relativePath, StockFile thisFile) {

        relativePath = FilenameUtils.separatorsToSystem(relativePath);
        thisFile.setRelativePath(relativePath);
        if (!this.manifest.containsKey(thisFile.getRelativePath())) {
            System.out.println("Added item in " + getName() + ": " + thisFile.getRelativePath());
        } else if (thisFile.getVersion() == this.manifest.get(thisFile.getRelativePath()).getVersion()) {
            System.out.println("Updated item in " + getName() + ": " + thisFile.getRelativePath());
        }
        this.manifest.put(thisFile.getRelativePath(), thisFile);
    }

    /**
     * Adds an entry <relativePath, thisFile> to the manifest. 
     * 1) If the given relativePath is not found in the manifest or the version 
     * of thisFile is greater than that of the one exists in the manifest,
     * update the manifest accordingly. 
     *
     * @param relativePath
     * @param thisFile
     */
    public void insertFile(String relativePath, StockFile thisFile) {

        relativePath = FilenameUtils.separatorsToSystem(relativePath);

        if (!this.manifest.containsKey(relativePath) || thisFile.getVersion() > this.manifest.get(relativePath).getVersion()) {
            thisFile.setRelativePath(relativePath);
            System.out.println("Added item to " + getName() + ": " + thisFile.getRelativePath());
            this.manifest.put(thisFile.getRelativePath(), thisFile);
        }
        
    }

    /**
     * Removes the given relativePath provided from the manifest
     *
     * @param relativePath
     */
    public void removeFile(String relativePath) {
        this.manifest.remove(FilenameUtils.separatorsToSystem(relativePath));
    }

    /**
     * Checks if the given relativePath exists in the manifest
     * @param relativePath
     * @return 
     */
    public boolean containsFile(String relativePath) {
        return this.manifest.containsKey(FilenameUtils.separatorsToSystem(relativePath));
    }

    /**
     * Returns a StockFile object that corresponds to the given relativePath
     * @param relativePath
     * @return 
     */
    public StockFile getFile(String relativePath) {
        return this.manifest.get(FilenameUtils.separatorsToSystem(relativePath));
    }

    /**
     * Checks if the manifest provided is the same as this
     * @param newManifest
     * @return 
     */
    public boolean isEqual(Manifest newManifest) {
        Set<StockFile> values1 = new HashSet<StockFile>(this.manifest.values());
        Set<StockFile> values2 = new HashSet<StockFile>(newManifest.manifest.values());
        return values1.equals(values2);
    }

    @Override
    public String toString() {
        String output = "=========\n" + getName() + "=======\nKey (Version) || Type\n";
        for (String key : this.manifest.keySet()) {
            output += key + " (" + this.manifest.get(key).getVersion() + ") || Directory: " + this.manifest.get(key).isDirectory() + "\n";
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
