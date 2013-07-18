/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sandbox.gateway;

import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;

/**
 * @author Bahman
 */
public class Gateway
{

    private int numServers;
    private HashMap<String, Server> serverMap;

    public Gateway(int numberOfServers)
    {
        this.numServers = numberOfServers;
        this.serverMap = new HashMap<>();
    }

    public void initializeServers(int numServers)
    {
        String input;

        InputStreamReader istream = new InputStreamReader(System.in);
        BufferedReader bufRead = new BufferedReader(istream);

        for (int i = 1; i <= numServers; i++)
        {
            try
            {
                System.out.println("What is Server" + i + "\'s name: ");
                input = bufRead.readLine();
                String serverName = input;

                System.out.println("Server" + i + " Address(Name): ");
                input = bufRead.readLine();
                InetAddress myAddress = InetAddress.getByName(input);

                System.out.println("Server" + i + "\'s Slave Address(Name): ");
                input = bufRead.readLine();
                InetAddress slaveAddress = InetAddress.getByName(input);

                System.out.println("Server" + i + " HeartBeat send port number: ");
                input = bufRead.readLine();
                int hbSendPort = Integer.parseInt(input);

                System.out.println("Server" + i + " HeartBeat receive port number: ");
                input = bufRead.readLine();
                int hbReceivePort = Integer.parseInt(input);

                Server newServer = new Server(serverName, myAddress, slaveAddress, hbSendPort, hbReceivePort, "START");

                serverMap.put(newServer.getServerName(), newServer);
            }
            catch (IOException err)
            {
                System.out.println("Error reading line.");
            }
            catch (NumberFormatException err)
            {
                System.out.println("Error Converting Number.");
            }
        }
    }

    public HashMap<String, Server> getServerMap()
    {
        return this.serverMap;
    }
}
