package stockfile;

import java.io.IOException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import stockfile.controllers.DirectoryWatcher;
import stockfile.controllers.SFTPController;
import stockfile.controllers.StateController;
import stockfile.controllers.SyncController;

public class PeriodicSync implements Runnable {
	
	private volatile Thread watcherThread;
	private int delayTime;
	
	public PeriodicSync (int delay) {
		this.delayTime = delay;
		try {
			watcherThread = new Thread(new DirectoryWatcher());
			try {
				SFTPController.getInstance().connect();
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
	
	public void runSync() {
		
			// TODO Auto-generated method stub
			try {
				
				System.out.println("State:"+watcherThread.getState());
				System.out.println("Disabling watcher...");
				watcherThread.interrupt();
				System.out.println("State:"+watcherThread.getState());
				System.out.println("===== SYNC PROCESS BEGIN =====");
				SyncController.getInstance().syncronize();
				System.out.println("===== SYNC PROCESS END =====");
				
				System.out.println("Loading directory state");
				StateController.getInstance().loadDirectoryState();
				
				System.out.println("Starting watcher...");
				System.out.println("State:"+watcherThread.getState());
				watcherThread = new Thread(new DirectoryWatcher());
				watcherThread.start();
				//watcherThread.notify();
				System.out.println("State:"+watcherThread.getState());
				System.out.println("Watching directories...");

			} catch (IllegalMonitorStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}
	
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
