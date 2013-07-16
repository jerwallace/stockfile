/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.server;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import stockfile.api.sync.SFTP;
import stockfile.client.UserSession;
import stockfile.dao.FileDAO;
import stockfile.models.FileList;
import stockfile.models.Manifest;
import stockfile.models.StockFile;
import stockfile.models.Manifest.Operation;
import stockfile.models.User;

/**
 *
 * @author WallaceJ
 */
public class Sync   {
   
    private FileDAO fileDAO = new FileDAO();
    private HashMap<String, Operation> syncList;
    
    public Sync ()  {
 
    }
    
    private void generateSyncList() {
        
        Manifest serverManifest = getServerManifest();
        Manifest clientManifest = FileList.getManifest();
        System.out.println(serverManifest);
        syncList = new HashMap<>();
        
        if (serverManifest.isEqual(clientManifest))
        {
            syncList = null;
        
        } else if (clientManifest==null) {
        	for (Map.Entry<String,StockFile> serverManifestEntry : serverManifest.getManifestMap().entrySet()) {
        		syncList.put(serverManifestEntry.getKey(), Operation.DOWNLOAD);
        	}
        	
        } else
        {
            for (Map.Entry<String, StockFile> clientManifestEntry : clientManifest.getManifestMap().entrySet())
            {
                float clientVersion = clientManifestEntry.getValue().getVersion();
                
                if (!serverManifest.containsFile(clientManifestEntry.getKey()))
                {
                    syncList.put(clientManifestEntry.getKey(), Operation.UPLOAD);
                    
                } else {
                    
                    StockFile serverManifestEntry = serverManifest.getFile(clientManifestEntry.getKey());
                    float serverVersion = serverManifestEntry.getVersion();
                    
                    if (serverVersion==clientVersion) {
                        if (clientManifestEntry.getValue().getLastModified().isAfter(serverManifestEntry.getLastModified())) {
                            FileList.getManifest().getFile(clientManifestEntry.getKey()).incrementVersion();
                            syncList.put(clientManifestEntry.getKey(), Operation.UPLOAD);
                        }
                    } else {
                        if (clientManifestEntry.getValue().getLastModified().isAfter(serverManifestEntry.getLastModified())) {
                            syncList.put(clientManifestEntry.getKey(), Operation.DUPLICATE);
                        } else {
                            syncList.put(clientManifestEntry.getKey(), Operation.DOWNLOAD);
                        }
                    }
                    serverManifest.removeFile(clientManifestEntry.getKey());
                }
            }
            
            for (Map.Entry<String, StockFile> otherManifestEntry : serverManifest.getManifestMap().entrySet()) {
                syncList.put(otherManifestEntry.getKey(), Operation.DOWNLOAD);    
            }
            
        }
        
    }
    
    public void syncronize() {
        
        generateSyncList();
        
        if (syncList!=null) {
            for (Map.Entry<String, Operation> syncItem : this.syncList.entrySet()) {
                try {
                    switch (syncItem.getValue()) {
                        case DOWNLOAD: 
                                SFTP.getInstance().get(syncItem.getKey());
                                break;
                        case UPLOAD:
                                SFTP.getInstance().send(syncItem.getKey());
                                fileDAO.updateFile(FileList.getManifest().getFile(syncItem.getKey()));
                                break;
                        case DUPLICATE:
                                SFTP.getInstance().send(syncItem.getKey());
                                fileDAO.updateFile(FileList.getManifest().getFile(syncItem.getKey()));
                                break;
                        default: break;
                    }
                } catch (Exception ex) {
                            Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    private Manifest getServerManifest()   {
        try {
            Manifest serverManifest = fileDAO.generateManifest(new User("testuser"));
            return serverManifest;
        } catch (SQLException ex) {
            Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
