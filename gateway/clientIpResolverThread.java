package gateway;

import gateway.models.InstanceList;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @author Bahman
 */
public class clientIpResolverThread extends Thread
{

    protected static DatagramSocket socket;
    private int receivePortNumber;

    public clientIpResolverThread(int receivePort)
    {
        this.receivePortNumber = receivePort;
    }

    @Override
    public void run()
    {
        try
        {
            socket = new DatagramSocket(this.receivePortNumber);
        }
        catch (SocketException ex)
        {
            System.err.println("IP Resplver Socket Failure: " + ex.toString());
        }


        while (true)
        {

            byte[] buf = new byte[256];

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try
            {
                socket.receive(packet);
            }
            catch (IOException ex)
            {
                System.err.println("IP Resolver Packet Receive Failure: " + ex.toString());
            }

            String received = new String(packet.getData(), 0, packet.getLength());

            System.out.println("Client has sent the following request to Gateway: " + received);

            InetAddress responseAddress = packet.getAddress();

            String masterServerName = InstanceList.getInstance().getServerArrangement().get(0);

            System.out.println("New Master Name: " + masterServerName);

            String masterDnsAddress = InstanceList.getInstance().getServerMap().get(masterServerName).getPubliDnsAddress();

            buf = masterDnsAddress.getBytes();

            packet = new DatagramPacket(buf, buf.length, responseAddress, packet.getPort());

            System.out.println("IP Resolver sending response to client machine: " + masterServerName + " @ " + masterDnsAddress);

            try
            {
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
