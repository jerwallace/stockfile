/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.server;

import java.io.File;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

/**
 * Runnable class that controls the OnlineStockInfo object by calling it to
 * update all Stock information every 2 minutes
 */
public class FileScanner implements Runnable
{

    Collection files;
    File thisDir;
    String directory = "data/stockfile_repos";

    public void collectFiles()
    {
        thisDir = new File(directory);
        files = FileUtils.listFiles(
                thisDir,
                new RegexFileFilter("^(.*?)"),
                DirectoryFileFilter.DIRECTORY);
        System.out.println(files);
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
                collectFiles();

                Thread.sleep(1200);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(FileScanner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
