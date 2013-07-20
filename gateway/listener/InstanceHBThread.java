package gateway.listener;

import java.io.*;
import java.net.*;

public class InstanceHBThread extends Thread
{

    protected static boolean alive = true;
    private int receivePorNumber;

    public InstanceHBThread(int receivePort)
    {
        super();
        this.receivePorNumber = receivePort;
    }

    @Override
    public void run()
    {
        try
        {
            DatagramSocket socket = new DatagramSocket(this.receivePorNumber);

            System.out.print("Heartbeat send/receive.");

            while (alive)
            {

                byte[] buf = new byte[256];

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);


                InetAddress responseAddress = packet.getAddress();

                String response = "heartbeat";
                buf = response.getBytes();
                packet = new DatagramPacket(buf, buf.length, responseAddress, packet.getPort());

                socket.send(packet);

                System.out.println(".");
            }

            socket.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
