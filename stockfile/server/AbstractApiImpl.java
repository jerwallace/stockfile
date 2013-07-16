package stockfile.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import stockfile.api.AbstractApi;

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
        
    }

}
