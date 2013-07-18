/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sandbox.gateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bahman
 */
public class GatewayDriver
{

    public static int hbSendPort;
    public static int hbReceivePort;
    public static int clientPort;

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
        try
        {
            initializeGateway(numServers);
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(GatewayDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(GatewayDriver.class.getName()).log(Level.SEVERE, null, ex);
        }

        Gateway myGateway = new Gateway(numServers);
        myGateway.initializeServers(numServers);
    }

    public static void initializeGateway(int numServers) throws UnknownHostException, IOException
    {
        String input;
        InetAddress gatewayAddress = InetAddress.getLocalHost();

        InputStreamReader istream = new InputStreamReader(System.in);
        BufferedReader bufRead = new BufferedReader(istream);

        System.out.println("Enter gateway's client connection port number: ");
        input = bufRead.readLine();
        clientPort = Integer.parseInt(input);

        System.out.println("Enter gateway's HeartBeat Send port number: ");
        input = bufRead.readLine();
        hbSendPort = Integer.parseInt(input);

        System.out.println("Enter gateway's HeartBeat Receive port number: ");
        input = bufRead.readLine();
        hbReceivePort = Integer.parseInt(input);
    }
}
