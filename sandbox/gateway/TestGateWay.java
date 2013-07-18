/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sandbox.gateway;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bahman
 */
public class TestGateWay
{

    public static void main(String args[])
    {

        HeartBeatListener HBL = new HeartBeatListener("127.0.0.1", 1036, 1000);
        Thread hbl = new Thread(HBL);
        hbl.start();

        HeartBeatSender HBS = new HeartBeatSender("127.0.0.1", "127.0.0.1", 1035, 1036, "PING", 1000);
        Thread hbs = new Thread(HBS);
        hbs.start();

        try
        {
            hbl.join();
            hbs.join();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(TestGateWay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
