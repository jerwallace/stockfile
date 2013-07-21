package stockfile.controllers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import stockfile.exceptions.ApplicationFailedException;

public class DNSResolver {

	private static DNSResolver dnsResolver;
	private int timeout = 3000;
	private String serverDefault;
	private int attempt = 0;
	private ServerType type;
	private String message;
	private int port;
	
	public enum ServerType {
		Master,Slave,LocalHost
	}
	
	private DNSResolver(ServerType type) throws ApplicationFailedException {
		this.type = type;
		this.getServerName();
	}
	
	/**
     * Static method returns a single instance of MySQLConnection.
     * <p/>
     * @return a single instance of MySQLConnection
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
    
    private final void requestDNS() throws IOException {
    	
    	switch (type) {
    		
    		case LocalHost:
    			setServerDefault("localhost");
    			return;
    		case Slave:
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
        
        setServerDefault(received+".stockfile.ca");
        this.attempt = 0;
    }

    private final void getServerName() throws ApplicationFailedException {
    	while (this.attempt<3) { 
			try {
				this.requestDNS();
				return;
			} catch (IOException e) {
				this.attempt++;
				continue;
			}
		}
    	throw new ApplicationFailedException("Gateway is down.");
    }
    
	public String getServerDefault() throws ApplicationFailedException {
		getServerName();
		System.out.println("Server connected to: "+serverDefault);
		return serverDefault;
	}

	private final void setServerDefault(String serverDefault) {
		this.serverDefault = serverDefault;
	}

	private int getPort() {
		return port;
	}

	private void setPort(int port) {
		this.port = port;
	}

	private String getMessage() {
		return message;
	}

	private void setMessage(String message) {
		this.message = message;
	}
	
}
