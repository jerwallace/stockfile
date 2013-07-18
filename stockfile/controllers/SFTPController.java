package stockfile.controllers;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import stockfile.dao.connection.Utils;
import stockfile.models.FileList;
import stockfile.models.StockFile;
import stockfile.security.UserSession;

public class SFTPController {

    private Properties props;
    private static SFTPController sftp_connection = null;
    private JSch jsch = new JSch();
    private Session session = null;
    private Channel channel = null;
    private ChannelSftp ch_sftp = null;
    private String userRoot = null;
    
    public SFTPController() {
            
    }
        /**
     * Static method returns a single instance of MySQLConnection.
     * @return  a single instance of MySQLConnection
     */
    public static SFTPController getInstance()  {
        if (sftp_connection == null) {
            sftp_connection = new SFTPController(); 
        }
        return sftp_connection;
    }
    
    public void connect() throws Exception {
            
            try {
                props = Utils.readProperties("/stockfile/config/stockfile_ftp.properties");
            } catch (IOException ex) {
                Logger.getLogger(SFTPController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            userRoot = props.getProperty("ftpRootDir")+UserSession.getInstance().getCurrentUser().getUserName();
            
            //
            //Now connect and SFTP to the SFTP Server
            //
            try {
                
                System.out.println("Connection to FTP.");
                //Create a session sending through our username and password
                session = jsch.getSession(
                        props.getProperty("username"), 
                        props.getProperty("ftpMaster"), 
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
                
                try {
                	goToHomeDir();
                } catch (SftpException e) {
                	if (e.id==2) {
                		System.out.println("Creating remote directory: "+userRoot);
                		ch_sftp.mkdir(userRoot);
                		goToHomeDir();
                	} else {
                		System.err.println(e);
                	}
                }
               
                
                
            } catch (Exception e) {
            	Class cls = e.getClass(); 
                System.err.println("Unable to connect to FTP server. "+cls.getName()+" | "+e.toString());
                throw e;
            } 
    }
    
    public void goToHomeDir() throws SftpException {
    	System.out.println("Changing to FTP remote dir: " + userRoot);
    	ch_sftp.cd(userRoot);
    }
    
    public void send(String filename) throws Exception {
    	   //System.out.println("Manifest contents:"+FileList.getInstance().getManifest());
           System.out.println("Attempting to send file: "+filename);
    	   try {
                StockFile f = FileList.getInstance().getManifest().getFile(filename);
                System.out.println("Storing file as remote filename: " + userRoot+ f.getRelativePath());
                if (f.isDirectory()) {
                	ch_sftp.mkdir(userRoot+f.getRelativePath());
                } else {
                	ch_sftp.put(new FileInputStream(f), userRoot+f.getRelativePath(), ChannelSftp.OVERWRITE);
                }
            } catch (Exception e) {
                System.err.println("Storing remote file failed. "+e.toString());
                throw e;
            }
    }
    
    public void get(String filename) {
        
        try {

        	StockFile f = new StockFile(UserSession.getInstance().getCurrentUser().getHomeDirectory(),filename); 
        	System.out.println(f.getPath());
        	if (!f.exists()) {
        		f.mkdirs();
        		f.createNewFile();
        	}

            System.out.println("Getting file "+ f.getPath());
            ch_sftp.get(UserSession.getInstance().getCurrentUser().getHomeDirectory()+f.getPath(), new FileOutputStream(f));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SFTPController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SftpException ex) {
            Logger.getLogger(SFTPController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

    }
    
//    public void duplicate(String filename) {
//        
//        try {
//            File f = new File("c:\\Users\\wallacej\\Stockfile\\"+filename); 
//            System.out.println("Getting file "+ filename);
//            ch_sftp.get(filename, new FileOutputStream(f));
//        } catch (FileNotFoundException ex) {
//                Logger.getLogger(SFTP.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SftpException ex) {
//            Logger.getLogger(SFTP.class.getName()).log(Level.SEVERE, null, ex);
//        } 
//        
//        
//    }

}