package gateway;

import gateway.models.InstanceList;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Public clientIpResolverThread extends Thread class. This thread is
 * continuously listening to a pre-specified port number for DNS resolve
 * requests from clients. It looks at the instance list and selects the instance
 * at the head position and returns its PUBLIC DNS prefix name to the client.
 * <p/>
 * @author Bahman
 */
public class clientIpResolverThread extends Thread
{

    protected static DatagramSocket socket;
    private int receivePortNumber;

    /**
     * Public constructor for the resolver thread
     * <p/>
     * @param receivePort -UDP port listening to client requests
     */
    public clientIpResolverThread(int receivePort)
    {
        this.receivePortNumber = receivePort;
    }

    @Override
    public void run()
    {
        try
        {
            //Create new socket to listen to the pre-specified port
            socket = new DatagramSocket(this.receivePortNumber);
        }
        catch (SocketException ex)
        {
            System.err.println("IP Resolver Socket Failure: " + ex.toString());
        }


        while (true)
        {

            byte[] buf = new byte[256];

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try
            {
                //Wait to receive a packet at the request socket
                socket.receive(packet);
            }
            catch (IOException ex)
            {
                System.err.println("IP Resolver Packet Receive Failure: " + ex.toString());
            }

            String received = new String(packet.getData(), 0, packet.getLength());

            System.out.println("Client has sent the following request to Gateway: " + received);

            //get the IP address of the client from the received packet
            InetAddress responseAddress = packet.getAddress();

            //get the name of the master server at position "0" inside the server arrangement list
            String masterServerName = InstanceList.getInstance().getServerArrangement().get(0);

            System.out.println("New Master Name: " + masterServerName);

            //retrieve the PUBLIC DNS prefix name of the master server from the server list
            String masterDnsAddress = InstanceList.getInstance().getServerMap().get(masterServerName).getPubliDnsAddress();

            buf = masterDnsAddress.getBytes();

            //create a packet with the response information (resolved DNS)
            packet = new DatagramPacket(buf, buf.length, responseAddress, packet.getPort());

            System.out.println("IP Resolver sending response to client machine: " + masterServerName + " @ " + masterDnsAddress);

            try
            {
                //Send packet back to client
                socket.send(packet);
            }
            catch (IOException ex)
            {
                System.err.println("IP Resolver Packet Send Failure: " + ex.toString());
            }

            System.out.println("Response: " + masterDnsAddress + " sent back to MulticastServer at: "
                    + packet.getAddress() + " : " + packet.getPort());
        }
    }
}
