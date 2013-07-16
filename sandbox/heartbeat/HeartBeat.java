package sandbox.heartbeat;

import java.net.*;
import java.io.*;

public class HeartBeat extends Thread
{// sends a heartbeat message to the multicast group every 60 seconds

    private String name;
    private MulticastSocket Csock;
    private InetAddress maddr;
    private int port;
    private Players ulist;
    private DatagramPacket hbMsg;
    private long TmHB = 1000;  //heartbeat frequency in milliseconds

    public HeartBeat(String ThreadName, MulticastSocket Csock,
            InetAddress maddr,
            int port,
            Players ulist,
            long timer)
    {
        this.name = ThreadName;
        this.ulist = ulist;
        this.Csock = Csock;
        this.maddr = maddr;
        this.port = port;
        this.TmHB = timer;
    }

    public void doHbCheck()
    {// checks the HB status of all users and send leave if > 5
        int hb;
        // first increment the hb of all
        ulist.incHB();
        // now keep removing all over 5
        // checkHB() returns the ip+port token or null if done
        String test = ulist.checkHB();
        while (test != null)
        {
            // send a leave message for this client
            String line = "3|" + test; //leave flag plus user
            DatagramPacket lvMsg = new DatagramPacket(line.getBytes(),
                    line.length(),
                    maddr,
                    port);
            try
            {
                Csock.send(lvMsg);
            }
            catch (IOException e)
            {
            }
            test = ulist.checkHB();
        }
    }

    @Override
    public void run()
    {
        // setup the hb datagram packet then run forever
        // setup the line to ignore the loopback we want to get it too
        String line = "5|";

        hbMsg = new DatagramPacket(line.getBytes(),
                line.length(),
                maddr,
                port);


        // continually loop and send this packet every TmHB seconds
        while (true)
        {
            try
            {
                Csock.send(hbMsg);
                System.out.println(this.name + " sent a heart beat!");
                doHbCheck();
                Thread.sleep(TmHB);
            }
            catch (IOException e)
            {
                System.err.println("Server can't send heartbeat");
                System.exit(-1);
            }
            catch (InterruptedException e)
            {
            }
        }
    }// end run
}// end class

