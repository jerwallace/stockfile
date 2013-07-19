/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sandbox.gateway.models.Servers;

import java.net.InetAddress;

/**
 * @author Bahman
 */
public class Server
{

    private String serverName;
    private InetAddress myIpAddress;
    private String masterName;
    private int hbSendPort;
    private int hbReceivePort;
    private String status;

    public Server(String name, InetAddress myAddress, String MasterName, int hbSendPortNum, int hbReceivePortNum, String serverStatus)
    {
        this.serverName = name;
        this.myIpAddress = myAddress;
        this.masterName = MasterName;
        this.hbReceivePort = hbReceivePortNum;
        this.hbSendPort = hbSendPortNum;
        this.status = serverStatus;
    }

    public InetAddress getMyIpAddress()
    {
        return this.myIpAddress;
    }

    public String getMyMastersName()
    {
        return this.masterName;
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

    public void setMyMastersName(String MasterName)
    {
        this.masterName = MasterName;
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
}
