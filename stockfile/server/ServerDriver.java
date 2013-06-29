package stockfile.server;

import stockfile.api.UserApi;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.Scanner;

/**
 * Main driver for the Server application. Creates the RMI registry and listens
 * to for connections. Accepts a new connection, passes the registry to it and
 * based on the protocol they are connecting from (UserApi or AdminApi), passes
 * the correct API Implementation to them. and listens to a new connection.
 */
public class ServerDriver
{

    private static final int PORT = 1099;
    private static Registry registry;
    private SaveState stateTools;

    /**
     * Public default constructor
     */
    public ServerDriver()
    {
        stateTools = new SaveState();
        stateTools.loadState();
    }

    /**
     * Public method that starts a new Java RMI registry at the pre-defined
     * Port# 1099
     * <p/>
     * @throws RemoteException
     */
    public static void startRegistry() throws RemoteException
    {
        registry = java.rmi.registry.LocateRegistry.createRegistry(PORT);
    }

    /**
     * Public method that binds a new remote Client object to the registry
     * <p/>
     * @param name
     * @param remoteUserObj <p/>
     * @throws RemoteException
     * @throws AlreadyBoundException
     */
    public static void registerObject(String name, Remote remoteUserObj)
            throws RemoteException, AlreadyBoundException
    {
        registry.bind(name, remoteUserObj);
        System.out.println("Registered: " + name + " -> "
                + remoteUserObj.getClass().getName() + "[" + remoteUserObj + "]");
    }

    /**
     * Main method for the ServerDriver that initializes and runs the RMI
     * registry, binds new connections and passes them to the appropriate API
     * Implementation, creates new threads for FileScanner and allows for remote
     * method calling between Client and Server
     * <p/>
     * @param args <p/>
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        //Create a new instance of ServerDriver object
        ServerDriver serverInstance = new ServerDriver();

        //Attach shutDownhook for data persistence after shutDown
        serverInstance.attachShutDownHook();

        //Create a Scanner object to read user input
        Scanner input = new Scanner(System.in);

        try
        {
            //Start the reigstry
            startRegistry();

            //Create new UserApiImpl
            registerObject(UserApi.class.getSimpleName(), new UserApiImpl());

        }
        catch (ExportException epex)
        {
            System.err.println("Error starting server. Please check the port or if another instance is already running.");
            System.exit(0);
        }

        //Create a thread to run FileScanner class separetly to update stock prices frequently
        Thread fileScannerThread = new Thread(new FileScanner());

        //Start the FileScanner thread
        fileScannerThread.start();

        String inputString = "";

        //Read command line input arguments from user and allow for communication
        //between Server and Client until User has entered "Exit"
        while (true)
        {
            System.out.print("StockFile Server > ");
            inputString = input.nextLine();
            if (inputString.equalsIgnoreCase("exit"))
            {
                System.exit(0);
            }
        }

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
