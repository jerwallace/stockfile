package stockfile.server;

import stockfile.api.UserApi;
import java.rmi.*;
import stockfile.api.FileList;

/**
 * Extends the AbstractApiImpl by adding Server side remotely invoked methods
 * that are used by User type Client
 */
public class UserApiImpl extends AbstractApiImpl implements UserApi
{

    private static final long serialVersionUID = 1L;

    /**
     * Public default constructor overriding the AbstractApiImpl superclass
     * <p/>
     * @throws RemoteException
     */
    public UserApiImpl() throws RemoteException
    {
        super();

    }

    @Override
    public FileList getServerFileList()
    {
        return FileList.getInstance();
    }
}
