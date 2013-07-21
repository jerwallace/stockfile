package stockfile.controllers;

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

import stockfile.controllers.DNSResolver.ServerType;
import stockfile.dao.connection.Utils;
import stockfile.exceptions.ApplicationFailedException;
import stockfile.models.FileList;
import stockfile.models.StockFile;
import stockfile.security.UserSession;

/**
 * SFTP Controller contains all of the commands used to send and receive files over SSH.
 * @author Jeremy Wallace, Bahman Razmpa, Peter Lee
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class SFTPController
{
	
    // Connection properties
    private Properties props;
    private static SFTPController sftp_connection = null;
    
    // We use the JSch library to connect over SSH.
    private JSch jsch = new JSch();
    
    // Session, Channel and SFTP objects.
    protected Session session = null;
    protected Channel channel = null;
    protected ChannelSftp ch_sftp = null;
    
    // The remote root for the user.
    private String userRoot = null;
    
    // A blacklist of files that are not transferred
    private Set<String> blackList;
    
    // Current reconnection attempt #.
    private int reconnectionAttempt = 0;
    
    /**
     * The constructor sets up the home directory and the blacklists for the connection.
     * @param homeFolder
     */
    protected SFTPController(String homeFolder)
    {
    	
        this.blackList = new HashSet<>();
        this.blackList.add("stockdata.pbj");
        this.blackList.add(".DS_Store");
        //this.blackList.add(UserSession.getInstance().getCurrentUser().getHomeDirectory());
        
        // Load properties
        try {
			loadConfiguration();
		} catch (ApplicationFailedException e) {
			System.err.println(e);
		}
        
        // Set user root to their home folder.
        this.userRoot = props.getProperty("ftpRootDir") + homeFolder;
    }

    /**
     * Checks if the file is in the blacklist.
     * @param filename Filename to check.
     * @return true if the file is in the blacklist.
     */
    public boolean inBlackList(String filename) {
    	for (String blackListWord : this.blackList) {
    		if (filename.matches("(?i).*"+blackListWord+".*")) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Static method returns a single instance of SFTPConnection
     * <p/>
     * @return a single instance of SFTPConnection
     */
    public static SFTPController getInstance(String homeFolder)
    {
        if (sftp_connection == null)
        {
            sftp_connection = new SFTPController(homeFolder);
        }
        return sftp_connection;
    }
    
    /**
     * Establishes an SSH connection and initializes the channel.
     * @throws JSchException If the session can not be established.
     * @throws ApplicationFailedException If the servers are unavailable and the application has failed.
     */
    private void connectSSH() throws JSchException, ApplicationFailedException {
    	System.out.println("Connection to FTP.");
        
    	// Create a new session withe the connection details.
    	// The server address is requested from the DNS Resolver.
        session = jsch.getSession(
                props.getProperty("username"),
                DNSResolver.getInstance(ServerType.MASTER).getServerDefault(),
                new Integer(props.getProperty("ftpPort")));

        System.out.println("Session created.");
        session.setPassword(props.getProperty("password"));
        
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        
        // Connect through the session with the credentials provided.
        session.connect();
        System.out.println("Session connected.");
    }
    
    /**
     * Open the SFTP channel after connecting to an open session.
     * @throws JSchException If the session is unavailable or if an SFTP channel can not be opened.
     */
    private void openSFTPChannel() throws JSchException {
    	//
        //Open the SFTP channel
        //
        System.out.println("Opening Channel.");
        channel = session.openChannel("sftp");
        channel.connect();
        ch_sftp = (ChannelSftp) channel;
    }
    
    /**
     * Load the configuration details from the utilies file.
     * @throws ApplicationFailedException If there are no credentials supplied.
     */
    private void loadConfiguration() throws ApplicationFailedException {
    	try
        {
            props = Utils.readProperties("/stockfile/config/stockfile_ftp.properties");
        }
        catch (IOException ex)
        {
            throw new ApplicationFailedException("No connection properties could be found.");
        }
    }
    
    /**
     * Run the entire connection process to establish an SFTP connection and cd to the home directory.
     * @throws JSchException If the channel or session fails.
     * @throws SftpException If the SFTP channel fails or there is a problem finding the home directory.
     */
    public void connect() throws JSchException, SftpException
    {
    	
        	connectSSH();

            openSFTPChannel();

            goToHomeDir();
            
    }
    
    /**
     * Close the connection
     */
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
    
    /**
     * Reconnects to SFTP in the event of a failure.
     * @throws InterruptedException
     * @throws SftpException
     * @throws IOException
     */
    public void reconnect() throws InterruptedException, SftpException, ApplicationFailedException {
    	
    	reconnectionAttempt++;
    	
    	try {
    		close();
    		connect();
    	} catch (JSchException ex) {
    		if (reconnectionAttempt<Integer.parseInt(props.getProperty("numReconnectionAttempts"))) {
    			System.err.println("Unable to connect to FTP server. Attempting reconnect in 10 seconds.");
        		Thread.sleep(1000);
        		System.out.println("Attempt "+reconnectionAttempt+" of "+props.getProperty("numReconnectionAttempts"));
    			reconnect();
    		} else {
    			throw new ApplicationFailedException("Connection to SFTP failed.");
    		}
    	}
    	
    }
    
    /**
     * Changes root directory to the users home stockfile directory.
     * @throws SftpException If there was a problem getting or creating this directory.
     */
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

    /**
     * Uploads a file over SFTP
     * @param filename The file name in the local PBJ file list to send.
     * @return True if the file was sent.
     * @throws SftpException If there is an error during transmission.
     * @throws IOException If there was a file stream error.
     */
    public boolean upload(String filename) throws SftpException, IOException
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
    
    /**
     * Downloads a file from the remote server.
     * @param filename The remote file to download.
     * @return True if the file is downloaded.
     * @throws SftpException If there is a problem downloading the file.
     * @throws FileNotFoundException If the file is not found on the server.
     * @throws IOException If there is a problem with the file stream.
     */
    public boolean download(String filename) throws SftpException, FileNotFoundException, IOException
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
    
    /**
     * Deletes a file on the server.
     * TODO: Make this more secure!
     */
    public final void delete(String filename) throws SftpException, JSchException {
    	
    	StockFile f = FileList.getInstance().getManifest().getFile(filename);

    	System.out.println("Attempting to delete the file or folder: " + f.getFullRemotePath());
    	
    	ChannelExec chanExec = (ChannelExec) session.openChannel("exec");
    	chanExec.setCommand("/bin/rm -rf /stockfiles/" + f.getRelativePath());
    	chanExec.connect();
    	chanExec.disconnect();
    	
    }
    
    /**
     * Helper function: Creates a duplicate file name based on StockFile standards.
     * @param filename File name to change.
     * @return The duplicate file name.
     */
    private final static String dupFileName(String filename) {
    	Random randomGenerator = new Random();
    	String filenameBits[] = filename.split(".");
    	filenameBits[0] = filenameBits[0]+"_sfdup"+randomGenerator.nextInt(100);
    	return StringUtils.join(filenameBits);
    	
    }
    
    /**
     * Duplicates a file locally and then uploads.
     * @param filename File name to duplicate
     * @throws IOException If a file stream error occurs.
     * @throws SftpException If an error in uploading occurs.
     */
    public void duplicate(String filename) throws IOException, SftpException {

        	StockFile f = null;
        	
            do {
            	f = new StockFile(dupFileName(filename),null);
            } while (f.exists());
            
            FileUtils.copyFile(FileList.getInstance().getManifest().getFile(filename), f);
            upload(f.getRelativePath());

    }
}
