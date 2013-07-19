/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sandbox.gateway.GatewayController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 *
 * @author Bahman
 */
public class GatewayDriver
{

    private static int hbSendPort;
    private static int hbReceivePort;
    private static int clientPort;

    public static void main(String args[]) throws IOException
    {

        String input;
        int numServers = 0;

        InetAddress gatewayAddress = InetAddress.getLocalHost();

        InputStreamReader istream = new InputStreamReader(System.in);
        BufferedReader bufRead = new BufferedReader(istream);

        System.out.println("Please enter the number of server instances running: ");
        input = bufRead.readLine();
        numServers = Integer.parseInt(input);

        initializeGateway(numServers);

        Gateway myGateway = new Gateway(numServers);
        myGateway.initializeServers(numServers);
        myGateway.printServersInfo();

        System.out.println("Server's Address is: " + InetAddress.getLocalHost() + " listening to port#: " + clientPort);

        GatewayIpResolverThread gwrt = new GatewayIpResolverThread(clientPort);

        Thread newThread = new Thread(gwrt);

        newThread.start();
    }

    public static void initializeGateway(int numServers) throws IOException
    {
        //Gateway's client connection port number
        clientPort = 1050;
        //Gateway's heartbeat send port number
        hbSendPort = 2010;
        //Gateway's heartbeat receive port number
        hbReceivePort = 2015;
//        try
//        {
//            String input;
//            InetAddress gatewayAddress = InetAddress.getLocalHost();
//
//            InputStreamReader istream = new InputStreamReader(System.in);
//            BufferedReader bufRead = new BufferedReader(istream);
//
//            System.out.println("Enter gateway's client connection port number: ");
//            input = bufRead.readLine();
//            clientPort = Integer.parseInt(input);
//
//            System.out.println("Enter gateway's HeartBeat Send port number: ");
//            input = bufRead.readLine();
//            hbSendPort = Integer.parseInt(input);
//
//            System.out.println("Enter gateway's HeartBeat Receive port number: ");
//            input = bufRead.readLine();
//            hbReceivePort = Integer.parseInt(input);
//        }
//        catch (IOException err)
//        {
//            System.out.println("Error reading line");
//        }
    }
}
