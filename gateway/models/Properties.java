package gateway.models;

import java.util.ArrayList;

/**
 * Public class that describes the overall properties of the Gateway and is
 * access by gateway driver when starting the gateway to create threads, assign
 * ports and addresses all contained in this class.
 * <p/>
 * @author Bahman
 */
public final class Properties
{

    private ArrayList<String> privateIpList;
    private ArrayList<String> publicIpList;
    private int gatewayHeartBeatPort;
    private int gatewayIpResolverPort;
    private int gatewayDNSresolverPort;
    private int heartbeatInterval;
    private int failCheckInterval;

    /**
     * Public main class constructor.
     * <p/>
     * @param gatewayStartPortNumber - Starting port number for the gateway to
     *                               incrementally assign to each heartbeat
     *                               thread
     * @param ipResolverPortNumber   - Port number for thread that listens and
     *                               resolves DNS requests from the client
     * @param heartbeatSendInterval  - Interval time between transmitting
     *                               heartbeats
     * @param failCheckTimeInterval  - Period which if times out, means a server
     *                               instance is DOWN
     */
    public Properties(int gatewayStartPortNumber, int ipResolverPortNumber, int DNSresolverPort, int heartbeatSendInterval, int failCheckTimeInterval)
    {
        this.privateIpList = new ArrayList<>();
        this.publicIpList = new ArrayList<>();
        this.gatewayHeartBeatPort = gatewayStartPortNumber;
        this.gatewayIpResolverPort = ipResolverPortNumber;
        this.gatewayDNSresolverPort = DNSresolverPort;
        this.heartbeatInterval = heartbeatSendInterval;
        this.failCheckInterval = failCheckTimeInterval;
        initialize();
    }

    /**
     * Adds the private and public DNS of the three default EC2 instances
     */
    public void initialize()
    {
        addIpAddress("ec2-50-16-42-197.compute-1.amazonaws.com", "sf1.stockfile.ca");
        addIpAddress("ec2-107-22-40-185.compute-1.amazonaws.com", "sf2.stockfile.ca");
        addIpAddress("ec2-54-234-182-56.compute-1.amazonaws.com", "sf3.stockfile.ca");
    }

    public void addIpAddress(String privateDNS, String publicDNS)
    {
        this.privateIpList.add(privateDNS);
        this.publicIpList.add(publicDNS);
    }

    public int getNumberofIps()
    {
        return this.privateIpList.size();
    }

    public int getGatewayPort()
    {
        return gatewayHeartBeatPort;
    }

    public String getPrivateDnsAddress(int index)
    {
        return this.privateIpList.get(index);
    }

    public String getPublicDnsAddress(int index)
    {
        return this.publicIpList.get(index);
    }

    public int getGatewayIpResolverPort()
    {
        return gatewayIpResolverPort;
    }

    public int getHeartbeatInterval()
    {
        return heartbeatInterval;
    }

    public int getFailCheckInterval()
    {
        return failCheckInterval;
    }

    public int getGatewayDNSresolverPort()
    {
        return gatewayDNSresolverPort;
    }
}
