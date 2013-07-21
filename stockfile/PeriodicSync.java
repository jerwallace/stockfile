package stockfile;

import java.io.IOException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import stockfile.controllers.DirectoryWatcher;
import stockfile.controllers.SFTPController;
import stockfile.controllers.StateController;
import stockfile.controllers.SyncController;
import stockfile.security.UserSession;

/**
 * Periodic sync is a runnable thread that stops and starts the directory
 * watcher and synchronizes the folder with the database.
 *
 * @author jeremywallace
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class PeriodicSync implements Runnable {

    // The watcher thread that scans for changes in the directory.
    private volatile Thread watcherThread;
    // A designated delay time.
    private int delayTime;

    /**
     * The constructor takes in a delay time initializes the thread. It also
     * connects to SFTP.
     *
     * @param delay The delay between synchronization.
     */
    public PeriodicSync(int delay) {
        this.delayTime = delay;
        try {
            watcherThread = new Thread(new DirectoryWatcher());
            try {
                SFTPController.getInstance(UserSession.getInstance().getCurrentUser().getUserName()).connect();
            } catch (JSchException | SftpException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        watcherThread.start();
    }

    /**
     * The run sync method runs the operations of a sync, which include pausing
     * the watcher and synchronizing the folders.
     */
    public void runSync() {

        // TODO Auto-generated method stub
        try {

//				System.out.println("State:"+watcherThread.getState());
//				System.out.println("Disabling watcher...");

            // Interrupt the thread.
            watcherThread.interrupt();

//				System.out.println("State:"+watcherThread.getState());
//				
            // Run syncronization process.
            System.out.println("===== SYNC PROCESS BEGIN =====");
            SyncController.getInstance().syncronize();
            System.out.println("===== SYNC PROCESS END =====");

            System.out.println("Loading directory state");
            StateController.getInstance().loadDirectoryState(UserSession.getInstance().getCurrentClient().getFullDir());

//				System.out.println("Starting watcher...");
//				System.out.println("State:"+watcherThread.getState());

            // Restart the watcher thread.
            watcherThread = new Thread(new DirectoryWatcher());
            watcherThread.start();
            System.out.println("Watching directories...");

//				System.out.println("State:"+watcherThread.getState());

        } catch (IllegalMonitorStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println();
        }
    }

    /**
     * Runs the thread with the specified delay.
     */
    @Override
    public void run() {

        while (true) {
            try {
                runSync();
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
