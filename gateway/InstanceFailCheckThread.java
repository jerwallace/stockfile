package gateway;

import gateway.models.InstanceList;
import gateway.models.ServerInstance;
import java.util.Map;

/**
 * Class InstanceFailCheckThread extends Thread class. It creates a single
 * thread that is continuously checking the InstanceList iterating through the
 * list of all the servers to see which one has status "DOWN" changed by its
 * heartbeat thread as a result of heartbeat timeout. If it notices a server is
 * down, it rearranges the server setup and promotes another server (if
 * possible) to the place of the failed server.
 * <p/>
 * @author Bahman
 */
public class InstanceFailCheckThread extends Thread {

	private long timer;

	/**
	 * Public constructor for the thread
	 * <p/>
	 * @param checkInterval - Sleep time between each check iteration
	 */
	public InstanceFailCheckThread(long checkInterval) {
		super();
		this.timer = checkInterval;
	}

	/**
	 * Private method called by the thread only to rearranges the instance list
	 * in case of a server failure. If master fails, promotes a slave to its
	 * place and so on to the end of the list.
	 * <p/>
	 * @param serverName
	 */
	private void updateInstanceList(String serverName) {
		//If instance list is not empty, attempt to rearrange
		if (!InstanceList.getInstance().getServerMap().isEmpty()) {
			//remove the failed server from the server list
			InstanceList.getInstance().getServerMap().remove(serverName);
			//remove the failed server from the order chain list
			InstanceList.getInstance().getServerArrangement().remove(serverName);
		}

		//The next few lines are only to display some cute messages when a server goes down
		int liveInstances = InstanceList.getInstance().getServerMap().size();
		if (liveInstances >= 2) {
			System.out.println("But that's okay, you still have " + liveInstances + " servers alive...");
		} else if (liveInstances == 1) {
			System.out.println("Watch out, you only have ONE working server...");
		}
	}

	@Override
	public void run() {
		while (true) {
			ServerInstance newInstance;

			//If server list is not empty iterate through it and check for failed instances
			if (!InstanceList.getInstance().getServerMap().isEmpty()) {
				try {
					for (Map.Entry<String, ServerInstance> entry : InstanceList.getInstance().getServerMap().entrySet()) {
						newInstance = entry.getValue();

						//If the instance status is equal to "DOWN" the server is dead
						if ("DOWN".equals(newInstance.getStatus())) {
							//Get the name of the server
							String serverName = newInstance.getServerName();

							//get the position of the DOWN server from the order chain list
							int position = InstanceList.getInstance().getServerArrangement().indexOf(serverName);

							System.err.println(serverName + " is DOWN at position: " + position);

							//update the arrangement and the list of all the alive servers
							updateInstanceList(serverName);
						}
					}
				} catch (java.util.ConcurrentModificationException ex) {
					continue;
				}

				try {
					Thread.sleep(this.timer);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			} //If all servers are down, throw an error message and shutdown gateway
			else {
				System.out.println("OOPS!!!");
				System.err.println("ALL SERVERS DOWN...");
				System.exit(0);
			}
		}
	}
}
