package Gateway.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class describing the Singleton InstanceList
 */
public class InstanceList
{

    private static InstanceList currentServerList = null;
    private HashMap<String, ServerInstance> serverMap;
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

    public synchronized HashMap<String, ServerInstance> getServerMap()
    {
        return this.serverMap;
    }

    public synchronized ArrayList<String> getServerArrangement()
    {
        return serverArrangement;
    }
}
