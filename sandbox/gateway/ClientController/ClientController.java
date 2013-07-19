package sandbox.gateway.InstanceController;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author Bahman
 */
public class InstanceController
{

    private static DatagramSocket socket = null;
    protected BufferedReader in = null;
    private static String serverAddress = "localhost";
    private static int cSendPort;// = 1350;
    private static int gRecPort;// = 1050;
    private static InetAddress address = null;
    private static String input = "";

    public static void main(String args[]) throws SocketException, UnknownHostException, IOException
    {

//        DatagramSocket socket = null;
//
//        socket = new DatagramSocket(1350);
//
//        // send request
//
//        byte[] buf = new byte[256];
//
//        String request = "requestingIpAddress";
//
//        buf = request.getBytes();
//
//        InetAddress address = null;
//
//        address = InetAddress.getByName("localhost");
//
//        System.out.println("Connecting to Server - " + address.getCanonicalHostName().toString());
//
//        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 1050);
//
//        socket.send(packet);
//
//        System.out.println("Packet sent to Gateway successfully.");
//
//        // get response
//        packet = new DatagramPacket(buf, buf.length);
//        socket.receive(packet);
//
//        // display response
//        String received = new String(packet.getData(), 0, packet.getLength());
//
//        System.out.println("Response packet received: " + received);
//
//        socket.close();

        InputStreamReader istream = new InputStreamReader(System.in);
        BufferedReader bufRead = new BufferedReader(istream);

        try
        {
            System.out.println("Enter the Server's Addres: ");
            serverAddress = bufRead.readLine();

            System.out.println("Enter Client's send port number: ");
            input = bufRead.readLine();
            cSendPort = Integer.parseInt(input);

            System.out.println("Enter the Gateway's receive port number: ");
            input = bufRead.readLine();
            gRecPort = Integer.parseInt(input);
        }
        catch (IOException err)
        {
            System.out.println("Error reading line");
        }

        socket = new DatagramSocket(cSendPort);

        byte[] buf = new byte[256];

        String request = "requestingIpAddress";

        buf = request.getBytes();

        address = InetAddress.getByName(serverAddress);

        System.out.println("Connected to Server - " + address.getCanonicalHostName().toString());

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, gRecPort);

        // send request
        socket.send(packet);

        System.out.println("Packet sent to Gateway successfully.");

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Response packet received: " + received);

        socket.close();
    }
}
