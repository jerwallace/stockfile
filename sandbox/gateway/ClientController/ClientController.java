package sandbox.gateway.ClientController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Bahman
 */
public class ClientController
{

    private static DatagramSocket socket = null;
    protected BufferedReader in = null;
    private static String serverAddress = "localhost";
    private static int cSendPort;
    private static int gRecPort;
    private static InetAddress address = null;
    private static String input = "";

    public static void main(String args[]) throws SocketException, UnknownHostException, IOException
    {

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
