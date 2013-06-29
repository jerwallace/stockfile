package stockfile.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import stockfile.api.AbstractApi;
import stockfile.client.protocol.AbstractProtocol.State;

/**
 * Abstract class that contains the information about a connected client's
 * session including the state, stocks, username etc.
 */
public abstract class Session
{

    private String username = "";
    private State currentState = State.LOGIN;
    private static AbstractApi remoteApi;
    private static String host = "localhost";
    private static int port = 1099;
    protected static Registry registry;

    /**
     * Public method that returns the current state of the user inside the
     * protocol
     * <p/>
     * @return the currentState
     */
    public State getCurrentState()
    {
        return currentState;
    }

    /**
     * Public method that sets the current state of the user inside the protocol
     * <p/>
     * @param currentState the currentState to set
     */
    public void setCurrentState(State currentState)
    {
        this.currentState = currentState;
    }

    /**
     * Public method that returns the current user's userName
     * <p/>
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Public method that sets the current user's userName
     * <p/>
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Public method that returns the remote API object that the client is
     * interfacing with
     * <p/>
     * @return the remoteApi
     */
    public static AbstractApi getRemoteApi()
    {
        return remoteApi;
    }

    /**
     * Public method that sets the remote API object that the client is
     * interfacing with
     * <p/>
     * @param aRemoteApi the remoteApi to set
     */
    public static void setRemoteApi(AbstractApi aRemoteApi)
    {
        remoteApi = aRemoteApi;
    }

    /**
     * Public method that loads the RMI registry that client is communicating
     * <p/>
     * @throws RemoteException
     */
    public static void loadRegistry() throws RemoteException
    {
        registry = LocateRegistry.getRegistry(getHost(), getPort());
    }

    /**
     * Public method returns a String containing the hostName
     * <p/>
     * @return the host
     */
    public static String getHost()
    {
        return host;
    }

    /**
     * Public method that set the hostName
     * <p/>
     * @param aHost the host to set
     */
    public static void setHost(String aHost)
    {
        host = aHost;
    }

    /**
     * Public method that returns the integer port number
     * <p/>
     * @return the port
     */
    public static int getPort()
    {
        return port;
    }

    /**
     * public method that sets the integer host number
     * <p/>
     * @param aPort the port to set
     */
    public static void setPort(int aPort)
    {
        port = aPort;
    }
}
