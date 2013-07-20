package gateway.models;

import java.util.ArrayList;

/**
 * @author Bahman
 */
public final class Properties
{

    private ArrayList<String> privateIpList;
    private ArrayList<String> publicIpList;
    private int gatewayHeartBeatPort;
    private int gatewayIpResolverPort;
    private int heartbeatInterval;
    private int failCheckInterval;

    public Properties(int gatewayPortNumber, int ipResolverPortNumber, int heartbeatCheckInterval, int failCheckTimeInterval)
    {
        this.privateIpList = new ArrayList<>();
        this.publicIpList = new ArrayList<>();
        this.gatewayHeartBeatPort = gatewayPortNumber;
        this.gatewayIpResolverPort = ipResolverPortNumber;
        this.heartbeatInterval = heartbeatCheckInterval;
        this.failCheckInterval = failCheckTimeInterval;
        initialize();
    }

    public void addIpAddress(String privateDNS, String publicDNS)
    {
        this.privateIpList.add(privateDNS);
        this.publicIpList.add(publicDNS);
    }

    public void removeIpAddress(String ip)
    {
        this.privateIpList.remove(ip);
    }

    public int getNumberofIps()
    {
        return this.privateIpList.size();
    }

    public void initialize()
    {
        addIpAddress("ec2-50-16-42-197.compute-1.amazonaws.com", "sf1.stockfile.ca");
        addIpAddress("ec2-107-22-40-185.compute-1.amazonaws.com", "sf2.stockfile.ca");
        addIpAddress("ec2-54-234-182-56.compute-1.amazonaws.com", "sf3.stockfile.ca");
    }

    public int getGatewayPort()
    {
        return gatewayHeartBeatPort;
    }

    public void setGatewayPort(int gatewayPort)
    {
        this.gatewayHeartBeatPort = gatewayPort;
    }

    public String getPrivateDnsAddress(int index)
    {
        return this.privateIpList.get(index);
    }

    public String getPublicDnsAddress(int index)
    {
        return this.publicIpList.get(index);
    }

    public void setGatewayIpResolverPort(int gatewayIpResolverPort)
    {
        this.gatewayIpResolverPort = gatewayIpResolverPort;
    }

    public int getGatewayHeartBeatPort()
    {
        return gatewayHeartBeatPort;
    }

    public int getGatewayIpResolverPort()
    {
        return gatewayIpResolverPort;
    }

    public void setGatewayHeartBeatPort(int gatewayHeartBeatPort)
    {
        this.gatewayHeartBeatPort = gatewayHeartBeatPort;
    }

    public int getHeartbeatInterval()
    {
        return heartbeatInterval;
    }

    public int getFailCheckInterval()
    {
        return failCheckInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval)
    {
        this.heartbeatInterval = heartbeatInterval;
    }

    public void setFailCheckInterval(int failCheckInterval)
    {
        this.failCheckInterval = failCheckInterval;
    }
}
