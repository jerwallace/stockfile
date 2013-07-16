package stockfile;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import stockfile.client.UserSession;
import stockfile.controllers.DirectoryWatcher;
import stockfile.controllers.SFTP;
import stockfile.dao.FileDAO;
import stockfile.models.FileList;
import stockfile.models.StockFile;
import stockfile.server.Sync;

public class StockFileDriver {
	
	Collection<?> files;
    File thisDir;
    String directory;
    FileDAO dbFiles = new FileDAO();
    
    public StockFileDriver() throws Exception
    {
        thisDir = new File(UserSession.getInstance().getCurrentUser().getHomeDirectory());
        files = FileUtils.listFiles(
                thisDir,
                new RegexFileFilter("^(.*?)"),
                TrueFileFilter.INSTANCE);
        System.out.println(files);
        Iterator<?> iterator = files.iterator();
        
        while (iterator.hasNext())
        {
            StockFile thisFile = new StockFile(thisDir.toString(), iterator.next().toString(), 1, null, "", "");
			FileList.getInstance().getManifest().insertFile(thisFile.getFileName(), thisFile);
        }
        
    }
    
    public static void main(String[] args) throws Exception
    {
    	new StockFileDriver();
    	System.out.println(FileList.getInstance().getManifest());
    	// Make FTP connection to server.
    	SFTP.getInstance().connect();
    	
    	Sync syncTools = new Sync();
        syncTools.syncronize();
    	
    	// Create a thread to run FileScanner class separately to update stock prices frequently.
        Thread watcherThread = new Thread(new DirectoryWatcher());

        // Start the FileScanner thread.
        watcherThread.start();
        
    }
}
