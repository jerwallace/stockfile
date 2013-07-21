package stockfile.controllers;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import stockfile.dao.connection.Utils;
import stockfile.exceptions.ApplicationFailedException;
import stockfile.models.FileList;
import stockfile.models.StockFile;
import stockfile.security.UserSession;

public class SFTPController
{

    private Properties props;
    private static SFTPController sftp_connection = null;
    private JSch jsch = new JSch();
    private Session session = null;
    private Channel channel = null;
    private ChannelSftp ch_sftp = null;
    private String userRoot = null;
    private Set<String> blackList;

    private SFTPController()
    {
        this.blackList = new HashSet<>();
        this.blackList.add("stockdata.pbj");
        this.blackList.add(".DS_Store");
        //this.blackList.add(UserSession.getInstance().getCurrentUser().getHomeDirectory());
        
        try {
			loadConfiguration();
		} catch (ApplicationFailedException e) {
			System.err.println(e);
		}
        
        this.userRoot = props.getProperty("ftpRootDir") + UserSession.getInstance().getCurrentUser().getUserName();
    }

    public boolean inBlackList(String filename) {
    	for (String blackListWord : this.blackList) {
    		if (filename.matches("(?i).*"+blackListWord+".*")) {
    			return true;
    		}
    	}
    	return false;
    }
    /**
     * Static method returns a single instance of MySQLConnection.
     * <p/>
     * @return a single instance of MySQLConnection
     */
    public static SFTPController getInstance()
    {
        if (sftp_connection == null)
        {
            sftp_connection = new SFTPController();
        }
        return sftp_connection;
    }
    
    public void connectSSH() throws JSchException {
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
    }
    
    public void openSFTPChannel() throws JSchException {
    	//
        //Open the SFTP channel
        //
        System.out.println("Opening Channel.");
        channel = session.openChannel("sftp");
        channel.connect();
        ch_sftp = (ChannelSftp) channel;
    }
    
    public void loadConfiguration() throws ApplicationFailedException {
    	try
        {
            props = Utils.readProperties("/stockfile/config/stockfile_ftp.properties");
        }
        catch (IOException ex)
        {
            throw new ApplicationFailedException("No connection properties could be found.");
        }
    }
    
    public void cycleServers() {
    	// TODO: Add server implementation.
    }

    public void connect() throws JSchException, SftpException
    {
    	
        	connectSSH();

            openSFTPChannel();

            goToHomeDir();
            
    }
    
    public void close() {
    	if (ch_sftp.isConnected()) {
    		ch_sftp.disconnect();
    	}
    	if (channel.isConnected()) {
    		channel.disconnect();
    	}
    	if (session.isConnected()) {
    		session.disconnect();
    	}
    }
    
    public void reconnect() throws InterruptedException, SftpException {

    	try {
    		close();
    		cycleServers();
    		connect();
    	} catch (JSchException ex) {
    		System.err.println("Unable to connect to FTP server. ");
    		System.out.println("Attempting reconnect in 10 seconds.");
    		Thread.sleep(1000);
    		reconnect();
    	}
    	
    }
    
    public void goToHomeDir() throws SftpException
    {
    	try {
        System.out.println("Changing to FTP remote dir: " + userRoot);
        ch_sftp.cd(userRoot);
    	} catch (SftpException e) {
    		if (e.id == 2)
            {
                System.out.println("Creating remote directory: " + userRoot);
                ch_sftp.mkdir(userRoot);
                goToHomeDir();
            } else {
            	System.err.println("Problem getting remote folder.");
            	throw new ApplicationFailedException("Problem getting remote folder.");
            }
    	}
    }


    public boolean send(String filename) throws SftpException, IOException
    {
    	filename = FilenameUtils.separatorsToUnix(filename);
        if (!inBlackList(filename))
        {
            System.out.println("Attempting to upload file:" + filename);
            StockFile f = FileList.getInstance().getManifest().getFile(filename);
            System.out.println(f);
            if (f.exists()) {
	            if (f.isDirectory())
	            {
	            	try {
	            		ch_sftp.mkdir(f.getFullRemotePath());
	            	} catch (SftpException ex) {
	            		System.out.println("Directory already exists. Skipping entry.");
	            	}
	            }
	            else
	            {
	                ch_sftp.put(new FileInputStream(f), f.getFullRemotePath());
	            }
	            return true;
            } else {
            	System.err.println("Upload Error: File does not exist. Removing file from file list.");
            	FileList.getInstance().getManifest().removeFile(FilenameUtils.separatorsToSystem(filename));
            	return false;
            	
            }
            
        }
        else
        {
            System.out.println(filename + " upload ignored.");
            return false;
        }
    }

    public boolean get(String filename) throws SftpException, FileNotFoundException, IOException
    {
    	
        if (!inBlackList(filename))
        {
        	System.out.println("Looking up: "+filename);
            StockFile f = new StockFile(filename,null);
            
            System.out.println("Attempting to download file: " + f.getFullRemotePath());
            System.out.println(f);
            
            if (!filename.matches("(.*)\\.[A-Za-z0-9]{2,12}")) {
            	f.mkdirs();
            } else {
            	f.getParentFile().mkdirs();
            	ch_sftp.get(f.getFullRemotePath(), new FileOutputStream(f));
            }

            return true;
        }
        else
        {

            System.out.println(filename + " download ignored.");
            return false;
        }
    }
    
    
    public final void delete(String filename) throws SftpException, JSchException {
    	
    	StockFile f = FileList.getInstance().getManifest().getFile(filename);

    	System.out.println("Attempting to delete the file or folder: " + f.getFullRemotePath());
    	
    	ChannelExec chanExec = (ChannelExec) session.openChannel("exec");
    	chanExec.setCommand("/bin/rm -rf " + f.getFullRemotePath());
    	chanExec.connect();
    	chanExec.disconnect();
    	
    }
    
    private final static String dupFileName(String filename) {
    	Random randomGenerator = new Random();
    	String filenameBits[] = filename.split(".");
    	filenameBits[0] = filenameBits[0]+"_sfdup"+randomGenerator.nextInt(100);
    	return StringUtils.join(filenameBits);
    	
    }
    
    public void duplicate(String filename) throws IOException, SftpException {

        	StockFile f = null;
        	
            do {
            	f = new StockFile(dupFileName(filename),null);
            } while (f.exists());
            
            FileUtils.copyFile(FileList.getInstance().getManifest().getFile(filename), f);
            send(f.getRelativePath());

    }
}
