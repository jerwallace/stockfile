/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.gateway;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class GatewayClient
{

    public static void main(String args[]) throws SocketException, IOException
    {
        DatagramSocket socket = new DatagramSocket();

        // send request
        byte[] buf = new byte[2];
        String message = "Get New IP?";
        buf = message.getBytes();

        InetAddress address = InetAddress.getByName("gateway.stockfile.ca");

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 2010);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Client DNS Resolved to: " + received);

        socket.close();
    }
}
