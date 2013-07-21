package stockfile;

import stockfile.controllers.LoginController;
import stockfile.controllers.StateController;
import stockfile.exceptions.ApplicationFailedException;

public class StockFileDriver
{
	private static final int SYNC_DELAY = 10000;

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
