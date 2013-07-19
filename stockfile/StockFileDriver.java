package stockfile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import stockfile.controllers.DirectoryWatcher;
import stockfile.controllers.SFTPController;
import stockfile.controllers.StateController;
import stockfile.controllers.SyncController;
import stockfile.dao.FileDAO;
import sandbox.gateway.models.Servers.ServerList;
import stockfile.models.StockFile;
import stockfile.security.UserSession;

public class StockFileDriver {
	
	StateController stateTools;
	
    public StockFileDriver() throws Exception
    {
    	
    	stateTools = new StateController();
        stateTools.loadState();
        stateTools.loadDirectoryState();

    }
    
    public static void main(String[] args) throws Exception
    {
    	
    	StockFileDriver stockfileInstance = new StockFileDriver();
    	
    	//Attach shutDownhook for data persistence after shutDown
    	stockfileInstance.attachShutDownHook();
    	System.out.println(ServerList.getInstance().getManifest());
    	
    	// Make FTP connection to server.
    	SFTPController.getInstance().connect();
    	
    	SyncController syncTools = new SyncController();
        syncTools.syncronize();
    	
    	// Create a thread to run FileScanner class separately to update stock prices frequently.
        Thread watcherThread = new Thread(new DirectoryWatcher());

        // Start the FileScanner thread.
        watcherThread.start();
        
    }
    
    /**
     * Public method that creates a Runtime ShutDownHook thread to maintain the
     * State object values in case of system shut down to allow for data
     * persistence
     */
    public void attachShutDownHook()
    {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                stateTools.saveState();
            }
        });
    }
}
