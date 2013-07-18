/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sandbox.gateway;

import java.net.InetAddress;

/**
 * @author Bahman
 */
public class Server
{

    private String serverName;
    private InetAddress myIpAddress;
    private InetAddress mySlaveIpAddress;
    private int hbSendPort;
    private int hbReceivePort;
    private String status;

    public Server(String name, InetAddress myAddress, InetAddress slaveAddress, int hbSendPortNum, int hbReceivePortNum, String serverStatus)
    {
        this.serverName = name;
        this.myIpAddress = myAddress;
        this.mySlaveIpAddress = slaveAddress;
        this.hbReceivePort = hbReceivePortNum;
        this.hbSendPort = hbSendPortNum;
        this.status = serverStatus;
    }

    public InetAddress getMyIpAddress()
    {
        return this.myIpAddress;
    }

    public InetAddress getMySlaveIpAddress()
    {
        return this.mySlaveIpAddress;
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

    public void setMySlaveIpAddress(InetAddress mySlaveIpAddress)
    {
        this.mySlaveIpAddress = mySlaveIpAddress;
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
