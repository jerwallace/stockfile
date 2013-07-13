/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.api;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.joda.time.DateTime;
import stockfile.api.sync.SFTP;
import stockfile.dao.FileDAO;

/**
 * Runnable class that controls the OnlineStockInfo object by calling it to
 * update all Stock information every 2 minutes
 */
public class FileScanner implements Runnable
{

    Collection files;
    File thisDir;
    String directory;
    FileDAO dbFiles = new FileDAO();
    
    public void collectFiles() throws Exception
    {
        try {
            dbFiles.getFiles();
        } catch (SQLException ex) {
            Logger.getLogger(FileScanner.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        thisDir = new File(directory);
        files = FileUtils.listFiles(
        
                thisDir,
                new RegexFileFilter("^(.*?)"),
                DirectoryFileFilter.DIRECTORY);
        
        Iterator iterator = files.iterator();
        while (iterator.hasNext())
        {
            StockFile thisFile = new StockFile(thisDir.toString(), iterator.next().toString(), 1, null, "", "");
            FileList.getManifest().insertFile(thisFile.getFileName(), thisFile);
            try {

                if (!dbFiles.inDatabase(thisFile)) {
                    dbFiles.createFile(thisFile);
                    try {
                        SFTP.getInstance().send(thisFile.getFullPath());
                    } catch (Exception e)  {
                        System.err.println("Error sending file "+thisFile.getFullPath()+".");
                    }
                } else {
                    dbFiles.updateFile(thisFile);
                }
                
            } catch (SQLException sqlex) {
                System.err.println("SQL Exception: "+sqlex);
            }
            System.out.println(thisFile);
            SFTP.getInstance().recieveFiles();
        }
    }

    /**
     * initialize a thread to update StockList every 120000 ms (2 minutes)
     */
    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                try {
                    //                System.out.println("filescanner");
                                    collectFiles();
                } catch (Exception ex) {
                    Logger.getLogger(FileScanner.class.getName()).log(Level.SEVERE, null, ex);
                }
                //System.out.println(FileList.getManifest());
                //generateManifest();
                Thread.sleep(6000);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(FileScanner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public FileScanner(String directory)
    {
        this.directory = directory;
    }
}
