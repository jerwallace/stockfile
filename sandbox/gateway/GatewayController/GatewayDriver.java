package sandbox.gateway.GatewayController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bahman
 */
public class GatewayDriver
{

    private static int hbSendPort;
    private static int hbReceivePort;
    private static int clientPort;

    public static void main(String args[]) throws IOException
    {
//        String input;
//        int numServers = 0;
//
//        InetAddress gatewayAddress = InetAddress.getLocalHost();
//
//        InputStreamReader istream = new InputStreamReader(System.in);
//        BufferedReader bufRead = new BufferedReader(istream);
//
//        System.out.println("Please enter the number of server instances running: ");
//        input = bufRead.readLine();
//        numServers = Integer.parseInt(input);
//
//        initializeGateway(numServers);
//
//        Gateway myGateway = new Gateway(numServers);
//        myGateway.initializeServers(numServers);
//        myGateway.printServersInfo();
//
//        System.out.println("Server's Address is: " + InetAddress.getLocalHost() + " listening to port#: " + clientPort);
//
//        GatewayIpResolverThread gwrt = new GatewayIpResolverThread(clientPort);
//
//        Thread newThread = new Thread(gwrt);
//
//        newThread.start();

        GatewayHeartbeat gwhb1 = new GatewayHeartbeat("gatewayThread1", "ec2-107-22-40-185.compute-1.amazonaws.com", 1050, 2050, 1500);
        //GatewayHeartbeat gwhb2 = new GatewayHeartbeat("gatewayThread2", "ec2-54-234-182-56.compute-1.amazonaws.com", 1100, 2100, 1000);

        Thread hbThread1 = new Thread(gwhb1);
        //Thread hbThread2 = new Thread(gwhb2);

        hbThread1.start();
        //hbThread2.start();

        try
        {
            hbThread1.join();
            //hbThread2.join();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(GatewayDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void initializeGateway(int numServers) throws IOException
    {
        //Gateway's client connection port number
        clientPort = 1050;
        //Gateway's heartbeat send port number
        hbSendPort = 2010;
        //Gateway's heartbeat receive port number
        hbReceivePort = 2015;

    }
}
