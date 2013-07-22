package stockfile.controllers;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import stockfile.exceptions.ApplicationFailedException;
import stockfile.models.Client;
import stockfile.models.User;
import stockfile.security.StockFileSession;

public class ServerController {

	private static ServerController serverController;
	private static String macAddr;
	private Scanner scanner = new Scanner(System.in);
	
	private ServerController() throws SocketException, UnknownHostException {
		macAddr = Client.convertByteArrayString(
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
	
    public void run() {
    	
    	System.out.println("********Welcome to Stockfile********\n");
    	System.out.print("What server are you? ");
		String serverName;
		serverName = this.scanner.nextLine();
		Client serverInstance = new Client(serverName);
		User serverUser = new User("");
		StockFileSession.getInstance().setCurrentClient(serverInstance);
		StockFileSession.getInstance().setCurrentUser(serverUser);
		
    }
    
    public void copyDatabase() {
    	
    }
    
}
