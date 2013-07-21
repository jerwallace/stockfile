package stockfile.server;

import stockfile.models.Client;
import stockfile.security.UserSession;

public class ServerDriver {
	
	private static final int SYNC_DELAY = 10000;
	
	public ServerDriver() {
		
	}
	
	public void startHeartBeat(int port) {

        System.out.println("Port Number: " + port);
        new InstanceHBThread(port).start();
	}
	
	/**
     * The main class launches the stockfile application.
     * Shutdown hooks are established to save the state of the application.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
    	ServerDriver sd = new ServerDriver();
    	
    	if (args.length!=3) {
    		System.err.println("Please enter a value for backup server address. Use 'none' for no backup.");
    		System.exit(0);
    	}
    	 
    	int port = Integer.parseInt(args[1]); 
        ServerDriver serverInstance = new ServerDriver();
        
        Client serverClient = new Client();
        System.out.println("Initializing periodic sync...");
        Thread serverTransfer = new Thread(new ServerTransfer(SYNC_DELAY));
        serverTransfer.start();
        sd.startHeartBeat(new Integer(args[2]));
        
    }
	
}
