package stockfile.api;

/**
 * Interface class for the User(Trader) type client containing the methods
 * remotely invoked by the User clients
 */
public interface UserApi extends AbstractApi
{

    public FileList getServerFileList();
}
