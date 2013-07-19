/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sandbox.gateway.GatewayController;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bahman
 */
public class GatewayIpResolverThread implements Runnable
{

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    private int portNumber;

    public GatewayIpResolverThread(int portNumber)
    {
        this.portNumber = portNumber;
    }

    @Override
    public void run()
    {
        byte[] buf = new byte[256];

        // receive request
        try
        {

            socket = new DatagramSocket(portNumber);

            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            socket.receive(packet);

            String message = new String(buf, 0, packet.getLength());

            System.out.println("Packet received from Client: " + message);

            if (message.equals("requestingIpAddress"))
            {
                // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();

                int port = packet.getPort();

                System.out.println("Received IP request message from Client: " + address.toString() + " on port#: " + port);

                String serverIpAddress = "Some IP Address";

                buf = serverIpAddress.getBytes();

                packet = new DatagramPacket(buf, buf.length, address, port);

                System.out.println("Sending the following IP address as response: " + '\"' + serverIpAddress + '\"');

                socket.send(packet);

                socket.close();
            }
        }
        catch (IOException ex)
        {
            System.err.println("Datagram packet receive/send unsuccessful!");
        }
    }
}
