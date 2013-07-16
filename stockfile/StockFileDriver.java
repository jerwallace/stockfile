package stockfile;

import stockfile.api.sync.SFTP;
import stockfile.controllers.DirectoryWatcher;
import stockfile.server.Sync;

public class StockFileDriver {
	
    public static void main(String[] args) throws Exception
    {
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
