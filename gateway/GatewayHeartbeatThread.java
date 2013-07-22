package gateway;

import java.io.*;
import java.net.*;
import gateway.models.InstanceList;

/**
 * Class GatewayHearbeatThread is a extends Threads class. Each instance of this
 * class is used to send and receive heart beats to and from a single EC2
 * instance. The thread first sends a packet, if send is successful, it sleeps
 * for a specified a period and listens to receive a response. Once it receives
 * a response, it immediately sends another heartbeat. While it's listening,
 * there is a arbitrary timer which in case it runs out, it means that no heart
 * beat is detected and therefore the server is down. In this case it updates
 * the state of the monitored instance from "ON" to "DOWN".
 * <p/>
 * @author Bahman
 */
public class GatewayHeartbeatThread extends Thread
{

    private String instanceName;
    private String iPaddress;
    private int sendPortNumber;
    private int receivePortNumber;
    private int heartbeatFailureInterval;
    private long sleepTimer;
    protected DatagramSocket socket;

    /**
     * Public class constructor for the Thread
     * <p/>
     * @param name                   - Name assigned to the instance
     * @param address                - Private DNS address of the instance
     * @param serverSendPort         - Port number that heartbeat is being sent
     *                               from
     * @param instanceReceivePort    - Port number that on the instance open to
     *                               listening for a heartbeat
     * @param heartbeatFailInterval  - Response receive time-out interval
     * @param heartbeatTransmitTimer - Time interval between each heartbeat
     * <p/>
     * @throws IOException
     */
    public GatewayHeartbeatThread(String name, String address, int serverSendPort, int instanceReceivePort, int heartbeatFailInterval, long heartbeatTransmitTimer) throws IOException
    {
        this.instanceName = name;
        this.iPaddress = address;
        this.receivePortNumber = instanceReceivePort;
        this.sendPortNumber = serverSendPort;
        this.sleepTimer = heartbeatTransmitTimer;
        this.heartbeatFailureInterval = heartbeatFailInterval;
        socket = new DatagramSocket(this.sendPortNumber);
    }

    @Override
    public void run()
    {
        while (true)
        {
            //First send a heartbeat to the instance and if successful continue
            if (sendPacket())
            {
                try
                {
                    // sleep for a while
                    Thread.sleep(this.sleepTimer);
                }
                catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
                //Listen for a response from the instance and if successfull repeat
                if (receivePacket())
                {
                    continue;
                }
                else
                {
                    //if heartbeat receive failed, Server is DOWN kills this thread
                    System.err.println(this.instanceName + ": Heartbeat failed..");
                    break;
                }
            }
        }
        //Close UDP socket if anything failed
        socket.close();
    }

    /**
     * Public method that is used to send a heartbeat to an instance
     * <p/>
     * @return - TRUE if send successful, FALSE otherwise
     */
    public boolean sendPacket()
    {
        try
        {
            byte[] buf = new byte[256];

            // construct quote
            String dString = "beat";
            buf = dString.getBytes();

            //Setup the address
            InetAddress address = InetAddress.getByName(this.iPaddress);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, this.receivePortNumber);

            // send it
            socket.send(packet);

            return true;
        }
        catch (IOException ex)
        {
            //Close socket if failure
            socket.close();
            return false;
        }
    }

    /**
     * Public method that is used to receive a heartbeat from an instance
     * <p/>
     * @return - TRUE if receive successful, FALSE otherwise
     */
    public boolean receivePacket()
    {
        try
        {
            byte[] buf = new byte[256];

            // receive request
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            try
            {
                //Wait on the response for the specified period of time and if
                socket.setSoTimeout(this.heartbeatFailureInterval);
            }
            catch (SocketException ex)
            {
                //If packet is not received, print error and return false
                System.err.println(this.instanceName + ": Heartbeat Receive Timed Out!");
                return false;
            }

            //Receive packet
            socket.receive(packet);

//            String received = new String(packet.getData(), 0, packet.getLength());

            //Return true for successful receive
            return true;
        }
        catch (IOException ex)
        {
            //If receive failed close the UDP socket, change the Server status to "DOWN" and return false
            System.err.println(this.instanceName + ": Heartbeat packet receive failed!");
            socket.close();
            InstanceList.getInstance().getServerMap().get(this.instanceName).setStatus("DOWN");
            return false;
        }
    }
}
