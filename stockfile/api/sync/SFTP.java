package stockfile.api.sync;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import stockfile.dao.connection.Utils;

public class SFTP {

    private Properties props;
    private static SFTP sftp_connection = null;
    private JSch jsch = new JSch();
    private Session session = null;
    private Channel channel = null;
    private ChannelSftp ch_sftp = null;
    private String userRoot = null;
    
    public SFTP() {
            
    }
        /**
     * Static method returns a single instance of MySQLConnection.
     * @return  a single instance of MySQLConnection
     */
    public static SFTP getInstance()  {
        if (sftp_connection == null) {
            sftp_connection = new SFTP(); 
        }
        return sftp_connection;
    }
    
    public void connect() throws Exception {
            
            try {
                props = Utils.readProperties("/stockfile/config/stockfile_ftp.properties");
            } catch (IOException ex) {
                Logger.getLogger(SFTP.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            userRoot = props.getProperty("ftpRootDir")+"testuser/";
            
            //
            //Now connect and SFTP to the SFTP Server
            //
            try {
                
                System.out.println("Connection to FTP.");
                //Create a session sending through our username and password
                session = jsch.getSession(
                        props.getProperty("username"), 
                        props.getProperty("ftpHost"), 
                        new Integer(props.getProperty("ftpPort")));
                
                System.out.println("Session created.");
                session.setPassword(props.getProperty("password"));
                //Security.addProvider(new com.sun.crypto.provider.SunJCE());

                
                //Setup Strict HostKeyChecking to no so we dont get the
                //unknown host key exception
                //
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();
                System.out.println("Session connected.");

                //
                //Open the SFTP channel
                //
                System.out.println("Opening Channel.");
                channel = session.openChannel("sftp");
                channel.connect();
                ch_sftp = (ChannelSftp)channel;
                
                System.out.println("Changing to FTP remote dir: " + userRoot);
                ch_sftp.cd(userRoot);
                
                
            } catch (Exception e) {
                System.err.println("Unable to connect to FTP server. "+e.toString());
                throw e;
            } 
    }
    
    public void send(String filename) throws Exception {
            try {
                File f = new File(filename);
                System.out.println("Storing file as remote filename: " + f.getName());
                ch_sftp.put(new FileInputStream(f), f.getName());
            } catch (Exception e) {
                System.err.println("Storing remote file failed. "+e.toString());
                throw e;
            }
    }
    
    public void recieveFiles() throws Exception {
        
       //System.out.println("Downloading file "+filename);
       Vector files = ch_sftp.ls(userRoot);
       for (int i=0; i<files.size(); i++) {
         com.jcraft.jsch.ChannelSftp.LsEntry lsEntry = (com.jcraft.jsch.ChannelSftp.LsEntry) files.get(i);
         //System.out.println(lsEntry.getFilename());
         File f = new File("c:\\Users\\wallacej\\Stockfile\\"+lsEntry.getFilename());
         //ch_sftp.get(lsEntry.getFilename(), new FileOutputStream(f));
       }
     
    } 

}