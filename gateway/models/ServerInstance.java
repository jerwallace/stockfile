package Gateway.models;

import java.net.InetAddress;

/**
 * @author Bahman
 */
public class ServerInstance
{

    private String serverName;
    private InetAddress myIpAddress;
    private String publiDnsAddress;
    private int hbSendPort;
    private int hbReceivePort;
    private String status;

    public ServerInstance(String name, InetAddress myAddress, String publicDNSAddress, int hbSendPortNum, int hbReceivePortNum, String serverStatus)
    {
        this.serverName = name;
        this.myIpAddress = myAddress;
        this.publiDnsAddress = publicDNSAddress;
        this.hbReceivePort = hbReceivePortNum;
        this.hbSendPort = hbSendPortNum;
        this.status = serverStatus;
    }

    public InetAddress getMyIpAddress()
    {
        return this.myIpAddress;
    }

    public int getHbSendPort()
    {
        return this.hbSendPort;
    }

    public int getHbReceivePort()
    {
        return this.hbReceivePort;
    }

    public String getStatus()
    {
        return this.status;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public void setMyIpAddress(InetAddress myIpAddress)
    {
        this.myIpAddress = myIpAddress;
    }

    public void setHbSendPort(int hbSendPort)
    {
        this.hbSendPort = hbSendPort;
    }

    public void setHbReceivePort(int hbReceivePort)
    {
        this.hbReceivePort = hbReceivePort;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPubliDnsAddress()
    {
        return this.publiDnsAddress;
    }

    public void setPubliDnsAddress(String publiDnsAddress)
    {
        this.publiDnsAddress = publiDnsAddress;
    }
}
