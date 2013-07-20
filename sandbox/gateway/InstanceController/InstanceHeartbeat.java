package sandbox.gateway.InstanceController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Bahman
 */
public class InstanceHeartbeat implements Runnable
{

    private String name;
    private String gatewayAddress;
    private int instancePort;
    private int gatewayPort;
    private long interval;
    private static DatagramSocket socket = null;
    private static InetAddress address = null;

    public InstanceHeartbeat(String instanceName, String gatewayIpAddress, int instancePortNumber, int gatewayPortNumber, long timer)
    {
        this.name = instanceName;
        this.gatewayAddress = gatewayIpAddress;
        this.instancePort = instancePortNumber;
        this.gatewayPort = gatewayPortNumber;
        this.interval = timer;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {

                socket = new DatagramSocket(this.instancePort);

                byte[] buf = new byte[256];

                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                socket.receive(packet);

                address = InetAddress.getByName(packet.getAddress().getHostName());

                System.out.println(this.name + ": Connected to Server - " + address.getHostAddress().toString());

                String received = new String(packet.getData(), 0, packet.getLength());
                //System.out.println("Response packet received: " + received);

                System.out.println(this.name + ": Heartbeat received..");

                // get message
                if (!received.equals("beat"))
                {
                    System.err.println(this.name + ": Heartbeat message failure!");
                    System.exit(0);
                }

                String request = "beat";

                buf = request.getBytes();

                packet = new DatagramPacket(buf, buf.length, address, packet.getPort());

                Thread.sleep(this.interval);

                // send request
                socket.send(packet);

                System.out.println(this.name + ": Heartbeat Sent..");

                socket.close();
            }
            catch (IOException ex)
            {
                System.err.println(this.name + ": Heartbeat Thread Failed!");
            }
            catch (InterruptedException ex)
            {
                System.err.println(this.name + ": Thread.sleep Error!");
            }
        }
    }
}
