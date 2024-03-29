package stockfile.security;

import java.sql.Timestamp;
import java.util.HashMap;

import stockfile.models.Client;
import stockfile.models.User;

/**
 * Class definition for the Singleton User type client session
 */
public class StockFileSession {

    private static StockFileSession stockFileSession = null;
    private User currentUser;
    private Client currentClient;
    private boolean validSession = false;
    private Timestamp last_sync;
    private HashMap<String, Client> userClientHashMap;

    protected StockFileSession() {
        // empty constructor
    }

    //Default constructor for the singleton instance of the UserSession
    public static StockFileSession getInstance() {

        if (stockFileSession == null) {

            synchronized (StockFileSession.class) {

                StockFileSession inst = stockFileSession;

                if (inst == null) {

                    synchronized (StockFileSession.class) {

                        stockFileSession = new StockFileSession();
                    }
                }
            }
        }
        return stockFileSession;
    }

    /**
     * @return the lastSyncTime
     */
    public Timestamp getLastSync() {
        return this.last_sync;
    }

    /**
     * @param lastSyncStime for the currentUser to set
     */
    public void setlastSync(Timestamp last_sync_time) {
        this.last_sync = last_sync_time;
    }

    /**
     * @return the currentUser
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * @param currentUser the currentUser to set
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * @return the validSession
     */
    public boolean isValidSession() {
        return validSession;
    }

    /**
     * @param validSession the validSession to set
     */
    public void setValidSession(boolean validSession) {
        this.validSession = validSession;
    }

    public HashMap<String, Client> getUserClientHashMap() {
        return userClientHashMap;
    }

    public void setUserClientHashMap(HashMap<String, Client> userClientHashMap) {
        this.userClientHashMap = userClientHashMap;
    }

    /**
     * @return the currentClient
     */
    public Client getCurrentClient() {
        return currentClient;
    }

    /**
     * @param currentClient the currentClient to set
     */
    public void setCurrentClient(Client currentClient) {
    	
        this.currentClient = currentClient;
    }
    
    @Override
	public String toString() {
    	return "User:"+this.currentUser+"\n Client"+this.currentClient+"\n";
    }
}
