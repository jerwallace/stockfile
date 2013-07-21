package stockfile.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import stockfile.controllers.DirectoryWatcher;
import stockfile.controllers.SFTPController;
import stockfile.controllers.StateController;
import stockfile.controllers.SyncController;
import stockfile.models.FileList;
import stockfile.models.StockFile;
import stockfile.security.UserSession;

/**
 * Periodic sync is a runnable thread that stops and starts the directory watcher and syncronizes the folder with the database.
 * @author jeremywallace
 * @project StockFile, CICS 525
 * @organization University of British Columbia
 * @date July 20, 2013
 */
public class ServerTransfer implements Runnable {
	
	// The watcher thread that scans for changes in the directory.
	private String stockfileDir = "/stockfiles";
	// A designated delay time.
	private int delayTime;
	
	/**
	 * The constructor takes in a delay time initializes the thread. It also connects to SFTP.
	 * @param delay The delay between syncronization.
	 * @throws IOException 
	 * @throws SftpException 
	 * @throws JSchException 
	 */
	public ServerTransfer (int delay) throws JSchException, SftpException, IOException {
		this.delayTime = delay;
		SFTPController.getInstance().connect();
	}
	
	/**
	 * The run sync method runs the operations of a sync, which include pausing the watcher and syncronizing the folders.
	 */
	public void runSync() {
		
			// TODO Auto-generated method stub
			try {
				
				System.out.println("===== TRANSFER PROCESS BEGIN =====");
				
				Path startPath = Paths.get(stockfileDir);
	            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>()
	            {
	                @Override
	                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
	                {
	                	
	                    return FileVisitResult.CONTINUE;
	                }

	                @Override
	                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	                {
	                    File thisFile = new File(file.toString());
	                    try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    return FileVisitResult.CONTINUE;
	                }

	                @Override
	                public FileVisitResult visitFileFailed(Path file, IOException e)
	                {
	                	System.err.println("Visiting file "+file.toString()+" failed.");
	                    return FileVisitResult.CONTINUE;
	                }
	            });
				
				System.out.println("===== TRANSFER PROCESS END =====");

			} catch (Exception e) {
				e.printStackTrace();
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
