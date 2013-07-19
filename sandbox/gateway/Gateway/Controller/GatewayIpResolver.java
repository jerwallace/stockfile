/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sandbox.gateway.Gateway.Controller;

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
public class GatewayIpResolver implements Runnable
{

    private static int portNumber = 1050;

    @Override
    public void run()
    {
        try
        {
            InputStreamReader istream = new InputStreamReader(System.in);
            BufferedReader bufRead = new BufferedReader(istream);

            try
            {
                System.out.println("Enter the receive port number: ");
                String input = bufRead.readLine();
                portNumber = Integer.parseInt(input);
            }
            catch (IOException err)
            {
                System.out.println("Error reading line");
            }

            System.out.println("Server's Address is: " + InetAddress.getLocalHost() + " listening to port#: " + portNumber);

            GatewayIpResolverThread gwrt = new GatewayIpResolverThread(portNumber);

            Thread newThread = new Thread(gwrt);

            newThread.start();
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(GatewayIpResolver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
