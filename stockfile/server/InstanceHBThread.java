package stockfile.server;

import java.io.*;
import java.net.*;

/**
 * Class InstanceHBThread extends Thread. This thread opens a UDP socket and
 * listens to heartbeat messages from the Gateway. Once it receives a heartbeat
 * it immediately responds back to the Gateway to indicate that its alive.
 * <p/>
 * @author Bahman
 */
public class InstanceHBThread extends Thread
{

    protected static boolean alive = true;
    private int receivePortNumber;

    //Public constructor for the InstanceHBThread that takes in the listening port
    public InstanceHBThread(int receivePort)
    {
        super();
        this.receivePortNumber = receivePort;
    }

    @Override
    public void run()
    {
        try
        {
            //Create a UDP socket on the pre-specified port to listen for heartbeat
            DatagramSocket socket = new DatagramSocket(this.receivePortNumber);

            System.out.print("Heartbeat send/receive status->");

            while (alive)
            {
                byte[] buf = new byte[256];

                //Create receive datagram and read it from socket when available
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String response = "heartbeat";
                buf = response.getBytes();

                //create response packet to Gateway using the receive packet's IP address and Port
                packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());

                //send response packet
                socket.send(packet);

                System.out.println(" <3 ");
            }

            //If failure, close the UDP socket
            socket.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
