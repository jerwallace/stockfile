package stockfile.server;

import com.jcraft.jsch.ChannelExec;

import stockfile.controllers.SFTPController;

public class ServerSFTPController extends SFTPController {
	
	public ServerSFTPController() {
		super("");
	}
	
	public void dumpDatabase() {
		ChannelExec chanExec = (ChannelExec) session.openChannel("exec");
    	chanExec.setCommand("mysqldump " + f.getFullRemotePath());
    	chanExec.connect();
    	chanExec.disconnect();
	}
	
}
