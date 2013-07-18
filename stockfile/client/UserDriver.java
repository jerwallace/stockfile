/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.client;

import java.rmi.UnknownHostException;

import stockfile.api.UserApi;
import static stockfile.client.AbstractClient.connectToServer;
import stockfile.client.protocol.UserProtocol;
import stockfile.security.UserSession;

/**
 * UserDriver class is the driver for User type client
 */
public class UserDriver extends AbstractClient
{

    public static void main(String[] args) throws Exception
    {
        //Create a new Client control protocol to pass to the new connected user
        thisProtocol = new UserProtocol();

        boolean isConnected = false;

        while (!isConnected)
        {
            //Attempt to connect to Server
            connectToServer();
            try
            {
                //Load the RMI registry for the user session
                UserSession.loadRegistry();
                UserSession.setRemoteApi((UserApi) UserSession.registry.lookup(UserApi.class.getSimpleName()));
                System.out.println("Welcome to Stock File @ " + UserSession.getHost());
                isConnected = true;

            }
            catch (UnknownHostException uhex)
            {
                System.err.println("Server could not be found or was not running Stock File.");
            }
            catch (Exception conex)
            {
                System.err.println("Error connecting. Please ensure that the server is on and running.");
            }

        }

        //Create a thread to run FileScanner class separetly to update stock prices frequently
        //Thread userFileScannerThread = new Thread(new FileScanner("C:\\Users\\Bahman\\Documents\\User StockFile Repo"));

        //Start the FileScanner thread
        //userFileScannerThread.start();


        //Runs the protocol
        run();

    }
}
