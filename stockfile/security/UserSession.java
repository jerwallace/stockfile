package stockfile.security;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

import stockfile.client.Session;
import stockfile.models.Client;
import stockfile.models.User;

/**
 * Class definition for the Singleton User type client session
 */
public class UserSession implements Serializable
{

    private static UserSession userSession = null;
    private User currentUser;
    private boolean validSession = false;
    private Timestamp last_sync;
    private HashMap<byte[], Client> userClientHashMap;

    protected UserSession()
    {
//    	System.out.println("Starting user session...");
    	
    	currentUser = new User("testuser");
    	
 //   	System.out.println("User "+currentUser.getUserName()+" session started...");
    }

    //Default constructor for the singleton instance of the UserSession
    public static UserSession getInstance()
    {

        if (userSession == null)
        {
        	
            synchronized (UserSession.class)
            {
                UserSession inst = userSession;

                if (inst == null)
                {

                    synchronized (UserSession.class)
                    {
                        userSession = new UserSession();
                    }
                }
            }
        }

        return userSession;
    }

    /**
     * @return the lastSyncTime
     */
    public Timestamp getLastSync()
    {
        return this.last_sync;
    }

    /**
     * @param lastSyncStime for the currentUser to set
     */
    public void setlastSync(Timestamp last_sync_time)
    {
        this.last_sync = last_sync_time;
    }

    /**
     * @return the currentUser
     */
    public User getCurrentUser()
    {
        return currentUser;
    }

    /**
     * @param currentUser the currentUser to set
     */
    public void setCurrentUser(User currentUser)
    {
        this.currentUser = currentUser;
    }

    /**
     * @return the validSession
     */
    public boolean isValidSession()
    {
        return validSession;
    }

    /**
     * @param validSession the validSession to set
     */
    public void setValidSession(boolean validSession)
    {
        this.validSession = validSession;
    }

    public HashMap<byte[], Client> getUserClientHashMap()
    {
        return userClientHashMap;
    }

    public void setUserClientHashMap(HashMap<byte[], Client> userClientHashMap)
    {
        this.userClientHashMap = userClientHashMap;
    }

}
