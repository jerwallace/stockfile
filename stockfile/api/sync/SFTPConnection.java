package stockfile.api.sync;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import stockfile.client.UserSession;
import stockfile.dao.connection.Utils;

public class SFTPConnection {

    private Properties props;
    private static SFTPConnection sftp_connection = null;
    private JSch jsch = new JSch();
    private Session session = null;
    private Channel channel = null;
    private ChannelSftp ch_sftp = null;
    private String userRoot = null;
    
    public SFTPConnection() {
            
    }
        /**
     * Static method returns a single instance of MySQLConnection.
     * @return  a single instance of MySQLConnection
     */
    public static SFTPConnection getInstance()  {
        if (sftp_connection == null) {
            sftp_connection = new SFTPConnection(); 
        }
        return sftp_connection;
    }
    
    public void connect() throws Exception {
            
            
            
            try {
                props = Utils.readProperties("/stockfile/config/stockfile_ftp.properties");
            } catch (IOException ex) {
                Logger.getLogger(SFTPConnection.class.getName()).log(Level.SEVERE, null, ex);
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

}