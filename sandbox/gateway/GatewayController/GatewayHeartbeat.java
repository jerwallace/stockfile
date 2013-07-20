package sandbox.gateway.GatewayController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bahman
 */
public class GatewayHeartbeat implements Runnable
{

    private String name;
    private String instanceAddress;
    private int gatewayPort;
    private int instancePort;
    private long interval;
    //private static DatagramSocket socket = null;
    private static InetAddress address = null;

    public GatewayHeartbeat(String gatewayThreadName, String instanceIpAddress, int gatewayPortNumber, int instancePortNumber, long timer)
    {
        this.name = gatewayThreadName;
        this.instanceAddress = instanceIpAddress;
        this.gatewayPort = gatewayPortNumber;
        this.instancePort = instancePortNumber;
        this.interval = timer;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                DatagramSocket socket = new DatagramSocket();

                //socket = new DatagramSocket(this.gatewayPort);

                byte[] buf = new byte[256];

                String request = "beat";

                buf = request.getBytes();

                address = InetAddress.getByName(this.instanceAddress);

                System.out.println(this.name + ": Connected to Server - " + address.getHostAddress().toString());

                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, this.instancePort);

                // send request
                socket.send(packet);

                System.out.println(this.name + ": Heartbeat Sent..");

                // get response
                packet = new DatagramPacket(buf, buf.length);

                Thread.sleep(this.interval);

                System.out.println("waiting to receive response...");

                socket.receive(packet);

                System.out.println(this.name + ": Heartbeat received..");

                // display response
                String received = new String(packet.getData(), 0, packet.getLength());
//                System.out.println("Response packet received: " + received);

                if (!received.equals("beat"))
                {
                    System.err.println(this.name + ": Heartbeat response failure!");
                    System.exit(0);
                }

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
