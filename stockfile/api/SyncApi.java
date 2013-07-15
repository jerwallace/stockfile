package stockfile.api;

import java.util.HashMap;

/**
 * Interface class for the User(Trader) type client containing the methods
 * remotely invoked by the User clients
 */
public interface SyncApi extends AbstractApi
{
    
    public void syncronize();
    
}