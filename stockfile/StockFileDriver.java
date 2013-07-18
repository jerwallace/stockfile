package stockfile;

import stockfile.controllers.LoginController;
import stockfile.controllers.SFTPController;
import stockfile.controllers.StateController;
import stockfile.controllers.SyncController;

public class StockFileDriver {
	
	StateController stateTools;
	
    public StockFileDriver() throws Exception
    {
    	LoginController.run();
    	stateTools = new StateController();
        stateTools.loadState();
        stateTools.loadDirectoryState();

    }
    
    public static void main(String[] args) throws Exception
    {
   
    	StockFileDriver stockfileInstance = new StockFileDriver();
    	
    	//Attach shutDownhook for data persistence after shutDown
    	stockfileInstance.attachShutDownHook();
    	
    	// Make FTP connection to server.
    	SFTPController.getInstance().connect();
    	
    	SyncController syncTools = new SyncController();
        syncTools.syncronize();
    	
    	// Create a thread to run FileScanner class separately to update stock prices frequently.
        // Thread watcherThread = new Thread(new DirectoryWatcher());

        // Start the FileScanner thread.
        // watcherThread.start();
        
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
