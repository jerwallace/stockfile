package instance.heartbeat;

/**
 * Basic Driver that runs a heart beat on a server.
 *
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class InstanceHBDriver
{

    public static void main(String args[])
    {
    	try {
        String name = args[0];
        int port = Integer.parseInt(args[1]);

        System.out.println("name: " + name + " Port Number: " + port);

        new InstanceHBThread(port).start();
    	} catch (Exception e) {
    		System.err.println("Please enter a server name and a port number: Heartbeat sf1.stockfile.ca 2050");
    	}
    }
}
