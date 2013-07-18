/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sandbox.gateway;

import java.io.IOException;
import java.net.*;
import java.util.logging.*;

/**
 *
 * @author Bahman
 */
public class HeartBeatListener implements Runnable
{

    private String receiveIp;
    private int receivePort;
    private int time;
    private int messageSize = 256;

    public HeartBeatListener(String receiverIpAddress, int receiverPortNum, int sleepTime)
    {
        this.receiveIp = receiverIpAddress;
        this.receivePort = receiverPortNum;
        this.time = sleepTime;
    }

    public void listenForHeartBeat(String rIp, int rPort, int messageSize) throws UnknownHostException, SocketException, IOException
    {
        byte[] bufferIn = new byte[messageSize];

        try (DatagramSocket socketReceive = new DatagramSocket(rPort))
        {
            DatagramPacket packetIn = new DatagramPacket(bufferIn, bufferIn.length);

            socketReceive.receive(packetIn);

            String received = new String(packetIn.getData(), 0, packetIn.getLength());

            System.out.println("Heartbeat Received: " + received);

            socketReceive.disconnect();

            socketReceive.close();
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                listenForHeartBeat(this.receiveIp, this.receivePort, this.messageSize);
                Thread.sleep(this.time);
            }
            catch (SocketException ex)
            {
                Logger.getLogger(HBSenderListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (UnknownHostException ex)
            {
                Logger.getLogger(HBSenderListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (IOException | InterruptedException ex)
            {
                Logger.getLogger(HBSenderListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
