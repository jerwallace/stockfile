package stockfile.server;

import stockfile.api.User;
import stockfile.api.UserList;
import stockfile.api.AbstractApi;
import java.rmi.*;
import java.rmi.server.*;

/**
 * Implementation of the Abstract API class for the Server that extends the
 * UnicastRemoteObject class which is a sublass of the RMI RemoteServer class,
 * implements the AbstractApi class defined in the API package and is further
 * extended in the UserAiImpl & AdminApiImpl
 */
public abstract class AbstractApiImpl extends UnicastRemoteObject implements AbstractApi
{

    /**
     * Public default constructor overriding the UnicastRemoteObject superclass
     * <p/>
     * @throws RemoteException
     */
    public AbstractApiImpl() throws RemoteException
    {
        super();
    }

    /**
     * Public method that returns true if the User already exists in UserList
     * and false if User is not created already
     * <p/>
     * @param username <p/>
     * @return true/user exists - false/user does not exist
     * <p/>
     * @throws RemoteException
     */
    @Override
    public boolean userExists(String username) throws RemoteException
    {
        User currentUser = UserList.getInstance().getUser(username);

        //Creates a new User object if a User with the given userName
        // does not already exist in the UserList
        if (currentUser == null)
        {
            UserList.getInstance().addUser(username);
            return false;
        }
        else
        {
            return true;
        }

    }

    /**
     * Public method that returns the User object referred to by String userName
     * <p/>
     * @param username <p/>
     * @return - User Object
     * <p/>
     * @throws RemoteException
     */
    @Override
    public User getUser(String username) throws RemoteException
    {
        return UserList.getInstance().getUser(username);
    }
}
