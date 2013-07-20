package gateway;

import Gateway.models.InstanceList;
import Gateway.models.ServerInstance;
import java.util.Map;

public class InstanceFailCheckThread extends Thread
{

    private long timer;

    public InstanceFailCheckThread(long checkInterval)
    {
        super();
        this.timer = checkInterval;
    }

    private void updateInstanceList(String serverName)
    {
        if (!InstanceList.getInstance().getServerMap().isEmpty())
        {
            InstanceList.getInstance().getServerMap().remove(serverName);
            InstanceList.getInstance().getServerArrangement().remove(serverName);
        }

        int liveInstances = InstanceList.getInstance().getServerMap().size();
        if (liveInstances >= 2)
        {
            System.out.println("But that's okay, you still have " + liveInstances + " servers alive...");
        }
        else if (liveInstances == 1)
        {
            System.out.println("Watch out, you only have ONE working server...");
        }
    }

    @Override
    public void run()
    {
//        try
//        {
//            Thread.sleep(1000);
//        }
//        catch (InterruptedException ex)
//        {
//            Logger.getLogger(InstanceFailCheckThread.class.getName()).log(Level.SEVERE, null, ex);
//        }

        while (true)
        {
            ServerInstance newInstance;

            if (!InstanceList.getInstance().getServerMap().isEmpty())
            {
                try
                {
                    for (Map.Entry<String, ServerInstance> entry : InstanceList.getInstance().getServerMap().entrySet())
                    {
                        newInstance = entry.getValue();

                        if ("DOWN".equals(newInstance.getStatus()))
                        {
                            String serverName = newInstance.getServerName();

                            int position = InstanceList.getInstance().getServerArrangement().indexOf(serverName);

                            System.err.println(serverName + " is DOWN" + " at position: " + position);

                            updateInstanceList(serverName);
                        }
                    }
                }
                catch (java.util.ConcurrentModificationException ex)
                {
                    //System.err.println("Concurrent Exception!");
                    continue;
                }

                try
                {
                    Thread.sleep(this.timer);
                }
                catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
            }
            else
            {
                System.out.println("OOPS!!!");
                System.err.println("ALL SERVERS DOWN...");
                System.exit(0);
            }
        }

    }
}
