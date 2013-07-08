package SCPConnect;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author WallaceJ
 */
import com.jcraft.jsch.*;
import java.io.*;
import java.util.*;

/**
 * This class is used to demonstrate the usage of the
 * JCraft JSch package to SFTP files.
 *
 * @author  Tim Archer 04/20/07
 * @version $Revision: 1.1 $
 */
public class SCPExample {

    /** Creates a new instance of TestCommonsNet */
    public SCPExample() {
    }

    /**
     * main - Unit test program
     * @param args Command line arguments
     *
     */
    public static void main(String args[]) {
        try {
            String ftpHost = "ec2-107-21-128-167.compute-1.amazonaws.com";
            int ftpPort = 22;
            String ftpUserName = "stockdata";
            String ftpPassword = "M$$data13!";
            String ftpRemoteDirectory = "/home/stockdata/";
            String fileToTransmit = "c:\\Users\\wallacej\\quad.jpg";

            //
            //First Create a JSch session
            //
            System.out.println("Creating session.");
            JSch jsch = new JSch();
            Session session = null;
            Channel channel = null;
            ChannelSftp c = null;

            //
            //Now connect and SFTP to the SFTP Server
            //
            try {
                //Create a session sending through our username and password
                session = jsch.getSession(ftpUserName, ftpHost, ftpPort);
                System.out.println("Session created.");
                session.setPassword(ftpPassword);
                //Security.addProvider(new com.sun.crypto.provider.SunJCE());

                //
                //Setup Strict HostKeyChecking to no so we dont get the
                //unknown host key exception
                //
                java.util.Properties config = new java.util.Properties();
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
                c = (ChannelSftp)channel;
            } catch (Exception e) {
                System.err.println("Unable to connect to FTP server. "+e.toString());
                throw e;
            }            

            //
            //Change to the remote directory
            //
            System.out.println("Changing to FTP remote dir: " + ftpRemoteDirectory);
            c.cd(ftpRemoteDirectory);            

            //
            //Send the file we generated
            //
            try {
                File f = new File(fileToTransmit);
                System.out.println("Storing file as remote filename: " + f.getName());
                c.put(new FileInputStream(f), f.getName());
            } catch (Exception e) {
                System.err.println("Storing remote file failed. "+e.toString());
                throw e;
            }

            //
            //Get the list of files in the remote server directory
            //
            Vector files = c.ls(ftpRemoteDirectory);

            //
            //Log if we have nothing to download
            //
            if (files.size() == 0) {
                System.out.println("No files are available for download.");
            }
            //
            //Otherwise download all files except for the . and .. entries
            //
            else {
                for (int i=0; i<files.size(); i++) {
                    com.jcraft.jsch.ChannelSftp.LsEntry lsEntry = (com.jcraft.jsch.ChannelSftp.LsEntry) files.get(i);

                    if (!lsEntry.getFilename().equals(".") && !lsEntry.getFilename().equals("..")) {
                        System.out.println("Downloading file "+lsEntry.getFilename());

                        String outputFileName = "c:\\temp\\"+lsEntry.getFilename();

                        //Get the write and write it to our local file system
                        File f = new File(outputFileName);
                        c.get(lsEntry.getFilename(), new FileOutputStream(f));

                        //
                        //Remove the file from the server
                        //
                        /*
                        c.rm(lsEntry.getFilename());
                        */
                    }
                }
            }

            //
            //Disconnect from the FTP server
            //
            try {
                c.quit();
            } catch (Exception exc) {
                System.err.println("Unable to disconnect from FTP server. " + exc.toString());
            }            

        } catch (Exception e) {
            System.err.println("Error: "+e.toString());
        }

        System.out.println("Process Complete.");
        System.exit(0);
    }
}