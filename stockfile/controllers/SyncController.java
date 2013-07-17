/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class SyncController   {
   
    private FileDAO fileDAO = new FileDAO();
    private Map<String, Operation> syncList;
    
    public SyncController ()  {
 
    }
    
    private void generateSyncList() {
    	
    	Map<String,StockFile> serverManifest = getServerManifest().manifest;
        Map<String,StockFile> clientManifest = FileList.getInstance().getManifest().manifest;
        
    	this.syncList = new TreeMap<>();
    	
        if (getServerManifest().isEqual(FileList.getInstance().getManifest()))
        {
            syncList = null;
        
        } else if (FileList.getInstance().getManifest()==null) {
        	for (String key : serverManifest.keySet()) {
        		syncList.put(key, Operation.DOWNLOAD);
        	}
        } else
        {

        	for (String key : clientManifest.keySet()) {
            {
                StockFile clientFile = clientManifest.get(key);
                
                if (!serverManifest.containsKey(key))
                {
                    syncList.put(key, Operation.UPLOAD);
                    
                } else {
                    
                    StockFile servFile = serverManifest.get(key);
                    
                    if (servFile.getVersion()==clientFile.getVersion()) {
                        if (clientFile.getLastModified().isAfter(servFile.getLastModified())) {
                            FileList.getInstance().getManifest().getFile(key).incrementVersion();
                            syncList.put(key, Operation.UPLOAD);
                        }
                    } else {
                        if (clientManifest.get(key).getLastModified().isAfter(servFile.getLastModified())) {
                            syncList.put(key, Operation.DUPLICATE);
                        } else {
                            syncList.put(key, Operation.DOWNLOAD);
                        }
                    }
                    serverManifest.remove(key);
                }
            }
            
            for (String servkey : serverManifest.keySet()) {
                syncList.put(servkey, Operation.DOWNLOAD);    
            }
        	
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
                                SFTPController.getInstance().get(syncItem.getKey());
                                break;
                        case UPLOAD:
                                SFTPController.getInstance().send(syncItem.getKey());
                                fileDAO.updateFile(FileList.getInstance().getManifest().getFile(syncItem.getKey()));
                                break;
                        case DUPLICATE:
                                SFTPController.getInstance().send(syncItem.getKey());
                                fileDAO.updateFile(FileList.getInstance().getManifest().getFile(syncItem.getKey()));
                                break;
                        default: break;
                    }
                } catch (Exception ex) {
                            Logger.getLogger(SyncController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    private Manifest getServerManifest()   {
        try {
            Manifest serverManifest = fileDAO.generateManifest();
            return serverManifest;
        } catch (SQLException ex) {
            Logger.getLogger(SyncController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
