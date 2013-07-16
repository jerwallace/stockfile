package sandbox.heartbeat;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HeartBeatDriver
{

    public static void main(String args[]) throws IOException
    {

        MulticastSocket sock1 = new MulticastSocket(4044);
        MulticastSocket sock2 = new MulticastSocket(4055);
        MulticastSocket sock3 = new MulticastSocket(4066);

        InetAddress local = InetAddress.getLocalHost();

        Players ulist = new Players();

        Users user1 = new Users("user1", "127.0.0.1", "127.0.0.1", 4044);
        Users user2 = new Users("user2", "127.0.0.1", "127.0.0.1", 4055);
        Users user3 = new Users("user3", "127.0.0.1", "127.0.0.1", 4066);

        ulist.addUser(user1);
        ulist.addUser(user2);
        ulist.addUser(user3);

        HeartBeat HB1 = new HeartBeat("HB1", sock1, local, 4044, ulist, 1000);
        HeartBeat HB2 = new HeartBeat("HB2", sock2, local, 4055, ulist, 1000);
        HeartBeat HB3 = new HeartBeat("HB3", sock3, local, 4066, ulist, 1000);

        Thread proc1 = new Thread(HB1);
        Thread proc2 = new Thread(HB2);
        Thread proc3 = new Thread(HB3);

        proc1.start();
        proc2.start();
        proc3.start();
        try
        {
            proc1.join();
            proc2.join();
            proc3.join();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(HeartBeatDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
