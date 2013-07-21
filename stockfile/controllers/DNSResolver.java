package stockfile.controllers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import stockfile.exceptions.ApplicationFailedException;

/**
 * The DNS Resolver sends a UDP packet to the StockFile Gateway, which determines the current live server.
 * It can give the IP address of three servers, The current master, the instances slave or the localhost.
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class DNSResolver {

	private static DNSResolver dnsResolver;
	private int timeout = 3000;
	private String currentServer;
	private int attempt = 0;
	private ServerType type;
	private String message;
	private int port;
	
	public enum ServerType {
		MASTER,SLAVE,LOCAL_HOST
	}
	
	/**
	 * The constructor accepts a type of address to retrieve.
	 * @param type The type of server address to recieve, MASTER, SLAVE OR LOCAL_HOST
	 * @throws ApplicationFailedException
	 */
	private DNSResolver(ServerType type) throws ApplicationFailedException {
		this.type = type;
		this.getServerName();
	}
	
	/**
     * Static method returns a single instance of DNS Resolver
     * <p/>
     * @return a single instance of DNS Resolver
	 * @throws ApplicationFailedException 
     */
    public static DNSResolver getInstance(ServerType type) throws ApplicationFailedException
    {
        if (dnsResolver == null)
        {
        	dnsResolver = new DNSResolver(type);
        }
        return dnsResolver;
    }
    
    /**
     * This method performs a DNS request and recieves the current server.
     * @throws IOException
     */
    private final void dnsRequest() throws IOException {
    	
    	switch (type) {
    		
    		case LOCAL_HOST:
    			setCurrentServer("localhost");
    			return;
    		case SLAVE:
    			setMessage("Who do I back up?");
    			setPort(2025);
    			break;
    		default:
    			setMessage("Who's master Server?");
    			setPort(2010);
    			break;
    		
    	}
    	
    	//Create a UDP socket to send datagram packet request to agteway
        DatagramSocket socket = new DatagramSocket();

        // send request
        byte[] buf = new byte[2];
        buf = getMessage().getBytes();

        //Get the Public IP address of the gateway
        InetAddress address = InetAddress.getByName("gateway.stockfile.ca");

        //Create request packet using gateway ip address as destination
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, getPort());
        //Send request packet
        
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.setSoTimeout(timeout);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("DNS Resolved to: " + received);

        //Close UDP socket
        socket.close();
        
        setCurrentServer(received+".stockfile.ca");
        this.attempt = 0;
    }

    /**
     * This method runs a DNS request to determine which server is alive.
     * @throws ApplicationFailedException If the gateway server is down.
     */
    private final void getServerName() throws ApplicationFailedException {
    	while (this.attempt<3) { 
			try {
				this.dnsRequest();
				return;
			} catch (IOException e) {
				this.attempt++;
				continue;
			}
		}
    	throw new ApplicationFailedException("Gateway is down.");
    }
    
    /**
     * Returns the name of the server that was requested.
     * @return The [MASTER | SLAVE | LOCALHOST] server domain name.
     * @throws ApplicationFailedException If the gateway is down or all servers are down.
     */
	public String getServerDefault() throws ApplicationFailedException {
		getServerName();
		System.out.println("Server connected to: "+currentServer);
		return currentServer;
	}
	
	/**
	 * Set the current server that will be given to the requestor.
	 * @param currentServer The current server
	 */
	private final void setCurrentServer(String currentServer) {
		this.currentServer = currentServer;
	}

	/**
	 * Get the port that is used to send UDP messages.
	 * @return Port number to use.
	 */
	private int getPort() {
		return port;
	}

	/**
	 * Set the port that is used to send UDP messages
	 * @param port Port number to use.
	 */
	private void setPort(int port) {
		this.port = port;
	}

	/**
	 * Get the message to send over UDP
	 * @return The message to send.
	 */
	private String getMessage() {
		return message;
	}

	/**
	 * Set the message to send over UDP
	 * @param message The message to send.
	 */
	private void setMessage(String message) {
		this.message = message;
	}
	
}
