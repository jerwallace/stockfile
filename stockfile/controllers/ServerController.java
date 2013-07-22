package stockfile.controllers;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import stockfile.controllers.DNSResolver.ServerType;
import stockfile.exceptions.ApplicationFailedException;
import stockfile.models.Client;
import stockfile.models.User;
import stockfile.security.StockFileSession;

public class ServerController {

	private static ServerController serverController;
	private Scanner scanner = new Scanner(System.in);
	
	private ServerController() throws SocketException, UnknownHostException {
		Client.convertByteArrayString(
                NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress());
	}
	
	/**
     * Static method returns a single instance of Login Controller
     * @return a single instance of Login Controller
     * @throws ApplicationFailedException 
     */
    public static ServerController getInstance() throws ApplicationFailedException {
        if (serverController == null) {
            try {
                serverController = new ServerController();
            } catch (UnknownHostException | SocketException ex) {
                throw new ApplicationFailedException("Mac address could not be determined. Exiting.");
            }
        }
        return serverController;
    }
	
    /**
     * The run method initiates the server backup process.
     */
    public void run() {
    	
    	System.out.println("********Welcome to Stockfile********\n");
    	System.out.print("What server are you? ");
    	
    	String serverName;
		serverName = this.scanner.nextLine();
    	
		// Do not run the backup if you are master!
    	while (true) {
    		
    		try {
				if (!DNSResolver.getInstance(ServerType.MASTER).getServerDefault().equals(serverName)) {
					break;
				}
				System.out.println("You are the master! Waiting for more information.");
				Thread.sleep(2000);
			} catch (ApplicationFailedException e) {
				System.err.println("Gateway server is down.");
				System.exit(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		
    	// Set the client to a server client and set the user to a blank user to get
    	// the entire directory contents.
		Client serverInstance = new Client(serverName);
		User serverUser = new User("");
		StockFileSession.getInstance().setCurrentClient(serverInstance);
		StockFileSession.getInstance().setCurrentUser(serverUser);
		
    }
    
}
