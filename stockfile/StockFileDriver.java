package stockfile;

import stockfile.controllers.LoginController;
import stockfile.controllers.StateController;
import stockfile.exceptions.ApplicationFailedException;

/**
 * Stockfile driver is the main class for the StockFile application.
 * @author jeremywallace
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class StockFileDriver
{
	private static final int SYNC_DELAY = 10000;

	/**
	 * Initializes the application by requiring the user to login and then loading the previous state
	 * from the saved .pbj file in the users default directory.
	 */
    public StockFileDriver() throws Exception
    {
        try {
            LoginController.getInstance().run();
        } catch (ApplicationFailedException ex) {
            System.err.println(ex);
            System.exit(0);
        } 
        
        StateController.getInstance().loadState();
        StateController.getInstance().loadDirectoryState();
        
    }
    
    /**
     * The main class launches the stockfile application.
     * Shutdown hooks are established to save the state of the application.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {

        StockFileDriver stockfileInstance = new StockFileDriver();

        //Attach shutDownhook for data persistence after shutDown
        stockfileInstance.attachShutDownHook();
        
        System.out.println("Initializing periodic sync...");
        Thread periodicSync = new Thread(new PeriodicSync(SYNC_DELAY));
        periodicSync.start();
        
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
            	System.out.println("Saving state... Goodbye!");
            	StateController.getInstance().saveState();
            }
        });
    }
}
