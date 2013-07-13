package stockfile.api;

/**
 * Interface class for the User(Trader) type client containing the methods
 * remotely invoked by the User clients
 */
public interface SyncApi extends AbstractApi
{

    public Manifest getServerManifest();
    
}