package gateway.models;

import java.net.InetAddress;

/**
 * Public class that describes an Amazon EC2 Server Instance.
 * <p/>
 * @author Bahman
 */
public class ServerInstance
{

    private String serverName;
    private InetAddress myIpAddress;
    private String publiDnsName;
    private int hbSendPort;
    private int hbReceivePort;
    private String status;

    /**
     * Public main class constructor.
     * <p/>
     * @param name             - Name of the instance
     * @param privateIP        - Private IP address of the instance
     * @param publicDNSName    - Public DNS name of the instance
     * @param hbSendPortNum    - Port number to send UDP heartbeat
     * @param hbReceivePortNum - Port number to receive UDP heartbeat
     * @param serverStatus     - Status of server
     */
    public ServerInstance(String name, InetAddress privateIP, String publicDNSName, int hbSendPortNum, int hbReceivePortNum, String serverStatus)
    {
        this.serverName = name;
        this.myIpAddress = privateIP;
        this.publiDnsName = publicDNSName;
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

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPubliDnsAddress()
    {
        return this.publiDnsName;
    }

    public void setPubliDnsAddress(String publiDnsAddress)
    {
        this.publiDnsName = publiDnsAddress;
    }
}
