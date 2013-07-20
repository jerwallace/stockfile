package gateway.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class describing the Singleton InstanceList
 */
public class InstanceList
{

    private static InstanceList currentServerList = null;
    //Hashmap that stores the name and ServeInstance object of all of the "ON" instances
    private HashMap<String, ServerInstance> serverMap;
    /*
     * ArrayList that reflects the position of the "ON" instances in the order
     * chain by their name server name at position "0" is always name of the
     * master server. The following are backups
     */
    private ArrayList<String> serverArrangement;

    /**
     * Singleton class constructor
     */
    protected InstanceList()
    {
        serverMap = new HashMap<>();
        serverArrangement = new ArrayList<>();
    }

    /**
     * Returns the only userList instance object
     */
    public synchronized static InstanceList getInstance()
    {

        if (currentServerList == null)
        {

            synchronized (InstanceList.class)
            {

                InstanceList inst = currentServerList;

                if (inst == null)
                {

                    synchronized (InstanceList.class)
                    {
                        currentServerList = new InstanceList();
                    }
                }
            }
        }

        return currentServerList;
    }

    //Public method synchronized to lock at simultaneous access, returns the serverMap.
    public synchronized HashMap<String, ServerInstance> getServerMap()
    {
        return this.serverMap;
    }

    //Public method synchronized to lock at simultaneous access, returns the serverArrangement list.
    public synchronized ArrayList<String> getServerArrangement()
    {
        return serverArrangement;
    }
}
