package gateway;

import java.io.*;
import java.net.*;
import gateway.models.InstanceList;

public class GatewayHeartbeatThread extends Thread
{

    private String instanceName;
    private String iPaddress;
    private int sendPortNumber;
    private int receivePortNumber;
    private int heartbeatFailureInterval;
    private long sleepTimer;
    protected DatagramSocket socket;

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
                if (receivePacket())
                {
                    continue;
                }
                else
                {
                    System.err.println(this.instanceName + ": Heartbeat failed..");
                    break;
                }
            }
        }
        socket.close();
    }

    public boolean sendPacket()
    {
        try
        {
            byte[] buf = new byte[256];

            // construct quote
            String dString = "beat";
            buf = dString.getBytes();

            // send it
            InetAddress address = InetAddress.getByName(this.iPaddress);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, this.receivePortNumber);

//            System.out.println("Sending heartbeats to: " + this.instanceName + " @ " + address + " : " + this.receivePortNumber
//                    + " -- from: " + InetAddress.getLocalHost() + " : " + this.sendPortNumber);

            socket.send(packet);

            return true;
        }
        catch (IOException ex)
        {
            socket.close();
            //InstanceList.getInstance().getServerMap().get(this.instanceName).setStatus("DOWN");
            return false;
        }
    }

    public boolean receivePacket()
    {
        try
        {
            byte[] buf = new byte[256];
            // receive request
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            //System.out.println("Waiting for heartbeat response from: " + this.instanceName);

            try
            {
                socket.setSoTimeout(this.heartbeatFailureInterval);
            }
            catch (SocketException ex)
            {
                System.err.println(this.instanceName + ": Heartbeat Receive Timed Out!");
                return false;
            }

            socket.receive(packet);

            String received = new String(packet.getData(), 0, packet.getLength());

            //System.out.println(received + " received from: " + this.instanceName);

            return true;
        }
        catch (IOException ex)
        {
            System.err.println(this.instanceName + ": Heartbeat packet receive failed!");
            socket.close();
            InstanceList.getInstance().getServerMap().get(this.instanceName).setStatus("DOWN");
            return false;
        }
    }
}
