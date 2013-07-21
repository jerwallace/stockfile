package gateway;

import gateway.models.InstanceList;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author Bahman
 */
public class serverDNSResolverThread extends Thread
{

    private int listenPort;
    protected DatagramSocket socket;

    public serverDNSResolverThread(int portNumber)
    {
        this.listenPort = portNumber;
    }

    @Override
    public void run()
    {
        try
        {
            //Create new socket to listen to the pre-specified port
            socket = new DatagramSocket(this.listenPort);
        }
        catch (SocketException ex)
        {
            System.err.println("DNS Resolver Socket Failure: " + ex.toString());
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
                System.err.println("DNS Resolver Packet Receive Failure: " + ex.toString());
            }

            String received = new String(packet.getData(), 0, packet.getLength());

            System.out.println("Server instance has sent the following request to Gateway: " + received);

            //get the IP address of the client from the received packet
            InetAddress responseAddress = packet.getAddress();

            //get the position of the slave server behind the asking server
            int masterPosition = InstanceList.getInstance().getServerArrangement().indexOf(received);

            String response;

            if (masterPosition < InstanceList.getInstance().getServerArrangement().size())
            {
                response = InstanceList.getInstance().getServerArrangement().get(masterPosition + 1);
            }
            else
            {
                response = "none";
            }

            //retrieve the PUBLIC DNS prefix name of the slave server from the server list
            String slaveDNSname = InstanceList.getInstance().getServerMap().get(response).getPubliDnsAddress();

            buf = slaveDNSname.getBytes();

            //create a packet with the response information (resolved DNS)
            packet = new DatagramPacket(buf, buf.length, responseAddress, packet.getPort());

            System.out.println("Response sent to asking server: " + slaveDNSname);

            try
            {
                //Send packet back to server
                socket.send(packet);
            }
            catch (IOException ex)
            {
                System.err.println("IP Resolver Packet Send Failure: " + ex.toString());
            }
        }
    }
}
