package sandbox.gateway.GatewayController;

import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import sandbox.gateway.models.Servers.Server;
import sandbox.gateway.models.Servers.ServerList;

/**
 * @author Bahman
 */
public class Gateway
{

    private int numServers;

    public Gateway(int numberOfServers)
    {
        this.numServers = numberOfServers;
    }

    public void initializeServers(int numServers) throws IOException
    {
        String input;

        InputStreamReader istream = new InputStreamReader(System.in);
        BufferedReader bufRead = new BufferedReader(istream);

        int portNumber = 2020;

        for (int i = 1; i <= numServers; i++)
        {
            String serverName, masterName;

            System.out.println("Server" + i + " Address(Name): ");
            input = bufRead.readLine();
            InetAddress myAddress = InetAddress.getByName(input);

            if (i == 1)
            {
                serverName = "Master";
                masterName = null;
            }
            else
            {
                serverName = "Backup" + i;

                if (i == 2)
                {
                    masterName = "Master";
                }
                else
                {
                    masterName = "Backup" + (i - 1);
                }
            }

            Server newServer = new Server(serverName, myAddress, masterName, portNumber++, portNumber++, "START");
            ServerList.getInstance().getServerMap().put(newServer.getServerName(), newServer);

        }
    }

    public HashMap<String, Server> getServerMap()
    {
        return ServerList.getInstance().getServerMap();
    }

    public void printServersInfo()
    {
        HashMap<String, Server> map = ServerList.getInstance().getServerMap();
        for (Map.Entry<String, Server> entry : map.entrySet())
        {
            System.out.println("Server Name = " + entry.getKey() + ", IP Address = " + entry.getValue().getMyIpAddress().toString()
                    + ", Master's Name = " + entry.getValue().getMyMastersName()
                    + ", HBSendPort = " + entry.getValue().getHbSendPort()
                    + ", HBReceivePort = " + entry.getValue().getHbReceivePort()
                    + ", Slave IP Address = " + entry.getValue().getStatus());
        }
    }
}
