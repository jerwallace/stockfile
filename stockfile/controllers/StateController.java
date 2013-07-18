/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.controllers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import stockfile.models.Manifest;
import stockfile.models.StockFile;
import stockfile.models.FileList;
import stockfile.security.UserSession;

/**
 *
 * @author WallaceJ
 */
public class StateController
{
	private final String DATA_FILE_NAME = "/stockdata.pbj";
    private String homeDir = UserSession.getInstance().getCurrentUser().getHomeDirectory();
    private Manifest currentManifest = FileList.getInstance().getManifest();

    /**
     *
     */
    public void saveState()
    {

        try
        {
            try (
                    FileOutputStream fileOut = new FileOutputStream(this.homeDir + DATA_FILE_NAME);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut))
            {
                out.writeObject(currentManifest);
            }
        }
        catch (IOException i)
        {
            i.printStackTrace();
        }
    }
    
    
    public void loadDirectoryState() {
    	
    	try {
    	    
    		Path startPath = Paths.get(UserSession.getInstance().getCurrentUser().getHomeDirectory());
    	    Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
    	        
    	    	@Override
    	        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
    	    		
    	    		StockFile thisFile = new StockFile(UserSession.getInstance().getCurrentUser().getHomeDirectory(), dir.toString(), 1, null, "", "");		
	    	        if (!thisFile.getRelativePath().equals(""))
    	    		FileList.getInstance().getManifest().insertFile(thisFile.getRelativePath(), thisFile);
    	            return FileVisitResult.CONTINUE;
    	        }

    	        @Override
    	        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
    	        	StockFile thisFile = new StockFile(UserSession.getInstance().getCurrentUser().getHomeDirectory(), file.toString(), 1, null, "", "");
    	        	FileList.getInstance().getManifest().insertFile(thisFile.getRelativePath(), thisFile);
    	            return FileVisitResult.CONTINUE;
    	        }

    	        @Override
    	        public FileVisitResult visitFileFailed(Path file, IOException e) {
    	            return FileVisitResult.CONTINUE;
    	        }
    	        
    	    });
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    }

    /**
     *
     */
    public void loadState()
    {

        File f = new File(this.homeDir+DATA_FILE_NAME);

        if (f.exists())
        {
            try
            {
                //use buffering
                InputStream file = new FileInputStream(this.homeDir+DATA_FILE_NAME);
                InputStream buffer = new BufferedInputStream(file);
                try (ObjectInput input = new ObjectInputStream(buffer))
                {
                    //deserialize the List
                    currentManifest = (Manifest) input.readObject();
                    FileList.getInstance().loadManifest(currentManifest);
                    
                    //display its data
                    System.out.println("Current Manifest Imported:");
                    System.out.println(FileList.getInstance());

                }
            }
            catch (ClassNotFoundException ex)
            {
                System.err.println("Cannot perform input. Class not found.");
            }
            catch (IOException ex)
            {
                System.err.println("Cannot perform input."+ex);
            }
        }
        else
        {
            System.err.println("No saved state could be found. Creating new instance.");
        }
    }
}
