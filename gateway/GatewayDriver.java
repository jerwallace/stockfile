package gateway;

import Gateway.models.InstanceList;
import Gateway.models.Properties;
import Gateway.models.ServerInstance;
import java.net.InetAddress;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Bahman
 */
public class GatewayDriver
{

    protected static Properties myProperties;   //Stores PRIVATE DNS NAMEs of all EC2 instances
    protected static ArrayList<Thread> threadPool;  //Stores all the threads in charge of monitoring the heartbeats per instance

    public static void main(String[] args) throws java.io.IOException
    {
        //initialize myProerties class and give it an arbitrary port number to use for assigning ports to instances and gateway
        myProperties = new Properties(2050, 2010, 1000, 100);

        //Initializes the server instances and the thread pool
        initialize();

        //Print information about all instances that are going to be monitored
        for (int i = 0; i < InstanceList.getInstance().getServerArrangement().size(); i++)
        {
            System.out.println("Server Name: " + InstanceList.getInstance().getServerArrangement().get(i) + " Server Position: " + i);
        }

        //Create ONE thread that will monitor the status change of any of threads and inform of failure
        Thread failCheck = new Thread(new InstanceFailCheckThread(100));

        //Create ONE thread that will resolve the master server DNS name for the client
        Thread ipResolver = new Thread(new clientIpResolverThread(myProperties.getGatewayIpResolverPort()));

        //Start each thread
        for (int i = 0; i < threadPool.size(); i++)
        {
            threadPool.get(i).start();
        }

        //start failCheck thread
        failCheck.start();

        //start ipResolver thread
        ipResolver.start();

        //wait on all threads to return
        try
        {
            for (int i = 0; i < threadPool.size(); i++)
            {
                threadPool.get(i).join();
            }

            failCheck.join();

            ipResolver.join();
        }
        catch (InterruptedException ex)
        {
            System.err.println("Instance DOWN..");
        }
    }

    /**
     *
     * @throws IOException
     */
    public static void initialize() throws IOException
    {
        threadPool = new ArrayList<>();

        for (int i = 0; i < myProperties.getNumberofIps(); i++)
        {
            //Create a new string name for each new server instance
            String serverName = "Server" + i;

            //Get the PRIVATE DNS NAME for each instance stored in myProperties
            String serverPrivateDnsName = myProperties.getPrivateDnsAddress(i);

            //Get the PUBLIC DNS NAME for each instance stored in myProperties
            String serverPublicDnsName = myProperties.getPublicDnsAddress(i);

            //Get IP address of all servers using their PRIVATE DNS NAME
            InetAddress serverPrivateIpAddress = InetAddress.getByName(serverPrivateDnsName);

            //All instances can use same port number to respond to heartbeat on themselves
            int sendPortNumber = myProperties.getGatewayPort();

            //Generate a unique port number for the Gaetway to listen to each incoming heartbeat
            int receivePortNumber = myProperties.getGatewayPort() + i;

            //Set the starting status for all instances to "ON"
            String status = "ON";

            //Create a ServerInstance object per server instance
            ServerInstance newInstance = new ServerInstance(serverName, serverPrivateIpAddress, serverPublicDnsName, sendPortNumber, receivePortNumber, status);

            //Insert each server instance into the HashMap stored in the singleton InstanceList class
            InstanceList.getInstance().getServerMap().put(serverName, newInstance);

            //Insert each server instance into the Arraylist of positions stored in singleton InstanceList class
            InstanceList.getInstance().getServerArrangement().add(serverName);

            //Create a heartbeat communication thread per server
            Thread newThread = new Thread(
                    new GatewayHeartbeatThread(serverName, newInstance.getMyIpAddress().getHostName(),
                    newInstance.getHbReceivePort(), newInstance.getHbSendPort(), myProperties.getFailCheckInterval(),
                    myProperties.getHeartbeatInterval()));

            //Add each thread to the thread pool
            threadPool.add(newThread);
        }
    }
}
