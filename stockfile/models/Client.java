/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.models;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.commons.io.FilenameUtils;

/**
 * Class describing a Client object
 * <p/>
 * @author MrAtheist
 */
public class Client {

    private String type;
    private String description;
    private String manufacturer;
    private String modelNo;
    private String homeDir;
    private String ipAddress;
    private String macAddress;

    public Client() {
    }

    public Client(String type, String description, String manufacturer, String modelNo, String homeDir) throws UnknownHostException, SocketException {

        this.type = type;
        this.description = description;
        this.manufacturer = manufacturer;
        this.modelNo = modelNo;
        this.homeDir = homeDir;
        InetAddress myIpAddress = InetAddress.getLocalHost();
        NetworkInterface nwi = NetworkInterface.getByInetAddress(myIpAddress);
        byte myMacAddress[] = nwi.getHardwareAddress();
        this.ipAddress = myIpAddress.getHostAddress();
        this.macAddress = convertByteArrayString(myMacAddress);
    }

    public Client(String type, String description, String manufacturer, String modelNo, String homeDir, String ipAddress, String macAddress) throws UnknownHostException, SocketException {

        this.type = type;
        this.description = description;
        this.manufacturer = manufacturer;
        this.modelNo = modelNo;
        this.homeDir = homeDir;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
    }

    public Client(String type) {
        this.type = type;
        this.description = "Stockfile Server";
        this.manufacturer = "Amazon Inc";
        this.modelNo = "EC2 Cloud";
        this.homeDir = "/stockfiles";
    }

    /**
     * Converts a given byte[] array to a String object
     * @param byteArray
     * @return 
     */
    public static String convertByteArrayString(byte[] byteArray) {

        if (byteArray == null)
            return null;

        StringBuilder sb = new StringBuilder(18);
        for (byte b : byteArray) {
            
            if (sb.length() > 0)
                sb.append(':');
            
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer the manufacturer to set
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * @return the modelNo
     */
    public String getModelNo() {
        return modelNo;
    }

    /**
     * @param modelNo the modelNo to set
     */
    public void setModelNo(String modelNo) {
        this.modelNo = modelNo;
    }

    /**
     * @return IP Address
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @return MAC Address
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * @param ipAddress the last known IP Address for this client
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * @param macAddress the MAC Address for this client
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * @return the homeDir
     */
    public String getHomeDir() {
        return homeDir;
    }

    /**
     * @param homeDir the homeDir to set
     */
    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

    /**
     * @return the full directory with respect to the home directory
     */
    public String getFullDir() {
        return FilenameUtils.separatorsToSystem(System.getProperty("user.home") + "/" + this.homeDir);
    }

    @Override
    public String toString() {

        return "Client Type: " + getType()
                + "\nDescription: " + getDescription()
                + "\nManufacturer: " + getManufacturer()
                + "\nModel Number: " + getModelNo()
                + "\nIP Address: " + getIpAddress()
                + "\nMAC Address: " + getMacAddress();
    }
}
