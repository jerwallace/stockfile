package sandbox.gateway.models.Servers;

import java.util.HashMap;

/**
 * Class describing the Singleton ServerList
 */
public class ServerList
{

    private static ServerList currentServerList = null;
    private HashMap<String, Server> serverMap;

    /**
     * Singleton class constructor
     */
    protected ServerList()
    {
        serverMap = new HashMap<>();
    }

    /**
     * Returns the only userList instance object
     */
    public static ServerList getInstance()
    {

        if (currentServerList == null)
        {

            synchronized (ServerList.class)
            {

                ServerList inst = currentServerList;

                if (inst == null)
                {

                    synchronized (ServerList.class)
                    {
                        currentServerList = new ServerList();
                    }
                }
            }
        }

        return currentServerList;
    }

    public HashMap<String, Server> getServerMap()
    {
        return serverMap;
    }

    public void setServerMap(HashMap<String, Server> serverMap)
    {
        this.serverMap = serverMap;
    }
}
