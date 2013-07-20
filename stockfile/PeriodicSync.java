package stockfile;

import java.io.IOException;

import stockfile.controllers.DirectoryWatcher;
import stockfile.controllers.SFTPController;
import stockfile.controllers.SyncController;

public class PeriodicSync implements Runnable {
	
	private SyncController syncTools = new SyncController();
	private volatile Thread watcherThread;
	private int delayTime;
	
	public PeriodicSync (int delay) {
		this.delayTime = delay;
	}
	
	@Override
	public void run() {
		
		try {
			watcherThread = new Thread(new DirectoryWatcher());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		watcherThread.start();

		while (true) {
			// TODO Auto-generated method stub
			try {
				
				SFTPController.getInstance().connect();
				System.out.println("===== SYNC PROCESS BEGIN =====");
				syncTools.syncronize();
				System.out.println("===== SYNC PROCESS END =====");
				
				System.out.println("Watching directories...");

				Thread.sleep(delayTime);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
}
