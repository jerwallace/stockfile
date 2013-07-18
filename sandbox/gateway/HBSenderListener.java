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
public class HBSenderListener implements Runnable
{

    private String sendIp;
    private String receiveIp;
    private int sendPort;
    private int receivePort;
    private String message;
    private int time;
    private int messageSize = 256;

    public HBSenderListener(String senderIpAddress, String receiverIpAddress, int senderPortNum,
            int receiverPortNum, String heartBeatMessage, int sleepTime)
    {
        this.sendIp = senderIpAddress;
        this.receiveIp = receiverIpAddress;
        this.sendPort = senderPortNum;
        this.receivePort = receiverPortNum;
        this.message = heartBeatMessage;
        this.time = sleepTime;
    }

    public void sendHeartBeat(String sIp, String rIp, int sPort, int rPort, String hbMessage, int messageSize) throws UnknownHostException, SocketException, IOException
    {
        byte[] bufferOut = new byte[messageSize];

        bufferOut = hbMessage.getBytes();

        InetAddress receiveAddress = InetAddress.getByName(rIp);

        try (DatagramSocket socketSend = new DatagramSocket(sPort))
        {
            DatagramPacket packetOut = new DatagramPacket(bufferOut, bufferOut.length, receiveAddress, rPort);

            socketSend.send(packetOut);

            System.out.println("Heartbeat Sent: " + this.message + " - to: " + rIp + ": " + rPort + " - from: " + sIp + ": " + sPort);

            socketSend.close();
        }
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
                sendHeartBeat(sendIp, receiveIp, sendPort, receivePort, message, messageSize);
                listenForHeartBeat(receiveIp, receivePort, messageSize);
                Thread.sleep(time);
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
