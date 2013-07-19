package stockfile.controllers;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import stockfile.dao.connection.Utils;
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

    public SFTPController()
    {
        this.blackList = new HashSet<>();
        this.blackList.add("/stockdata.pbj");
        this.blackList.add("\\stockdata.pbj");
        this.blackList.add(UserSession.getInstance().getCurrentUser().getHomeDirectory());
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

    public void connect() throws Exception
    {

        try
        {
            props = Utils.readProperties("/stockfile/config/stockfile_ftp.properties");
        }
        catch (IOException ex)
        {
            Logger.getLogger(SFTPController.class.getName()).log(Level.SEVERE, null, ex);
        }

        userRoot = props.getProperty("ftpRootDir") + UserSession.getInstance().getCurrentUser().getUserName();

        //
        //Now connect and SFTP to the SFTP Server
        //
        try
        {

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
            ch_sftp = (ChannelSftp) channel;

            try
            {
                goToHomeDir();
            }
            catch (SftpException e)
            {
                if (e.id == 2)
                {
                    System.out.println("Creating remote directory: " + userRoot);
                    ch_sftp.mkdir(userRoot);
                    goToHomeDir();
                }
                else
                {
                    System.err.println(e);
                }
            }



        }
        catch (Exception e)
        {
            Class<? extends Exception> cls = e.getClass();
            System.err.println("Unable to connect to FTP server. " + cls.getName() + " | " + e.toString());
            throw e;
        }
    }

    public void goToHomeDir() throws SftpException
    {
        System.out.println("Changing to FTP remote dir: " + userRoot);
        ch_sftp.cd(userRoot);
    }


    public boolean send(String filename) throws SftpException, IOException
    {
        if (!this.blackList.contains(filename))
        {
            System.out.println("Attempting to upload file:" + filename);
            StockFile f = FileList.getInstance().getManifest().getFile(filename);
            System.out.println(f);

            if (f.isDirectory())
            {
                ch_sftp.mkdir(f.getFullRemotePath());
            }
            else
            {
                ch_sftp.put(new FileInputStream(f), f.getFullRemotePath(), ChannelSftp.OVERWRITE);
            }
            return true;
        }
        else
        {
            System.out.println(filename + " upload ignored.");
            return false;
        }
    }

    public boolean get(String filename) throws SftpException, FileNotFoundException, IOException
    {
        if (!this.blackList.contains(filename))
        {
            System.out.println("Attempting to download file: " + filename);
            StockFile f = FileList.getInstance().getManifest().getFile(filename);
            System.out.println(f);

            if (!f.exists())
            {
                f.mkdirs();
            }

            System.out.println("Getting file " + f.getFullRemotePath());

            ch_sftp.get(f.getFullRemotePath(), new FileOutputStream(f));

            return true;
        }
        else
        {

            System.out.println(filename + " download ignored.");
            return false;
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
