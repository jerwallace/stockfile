package stockfile.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Abstract Interface class for both client types containing the methods
 * remotely invoked by the either types
 */
public interface AbstractApi extends Remote
{

    /**
     * Public method that is invoked by clients to check whether a User object
     * exists and if not, creates anew User object taking userName as
     * identifying parameter
     * <p/>
     * @param username <p/>
     * @return <p/>
     * @throws RemoteException
     */
    public boolean userExists(String username) throws RemoteException;

    /**
     * Public method that is invoked by clients to which returns the appropriate
     * User object referenced to by their userName as identifying parameter
     * <p/>
     * @param username <p/>
     * @return <p/>
     * @throws RemoteException
     */
    public User getUser(String username) throws RemoteException;
}
