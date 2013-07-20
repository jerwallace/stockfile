package sandbox.gateway.InstanceController;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bahman
 */
public class InstanceDriver
{

    public static void main(String args[])
    {
        InstanceHeartbeat ihb1 = new InstanceHeartbeat("instance1", "localhost", 1050, 1035, 1000);
//        InstanceHeartbeat ihb2 = new InstanceHeartbeat("instance2", "localhost", 1070, 1055, 1500);

        Thread hbThread1 = new Thread(ihb1);
//        Thread hbThread2 = new Thread(ihb2);

        hbThread1.start();
//        hbThread2.start();

        try
        {
            hbThread1.join();
//            hbThread2.join();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(InstanceDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
