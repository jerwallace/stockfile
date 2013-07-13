/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.server;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import stockfile.api.Manifest;
import stockfile.api.SyncApi;
import stockfile.client.UserSession;
import stockfile.dao.FileDAO;

/**
 *
 * @author WallaceJ
 */
public class SyncApiImpl extends AbstractApiImpl implements SyncApi  {
   
    FileDAO fileDAO = new FileDAO();
    public SyncApiImpl () throws RemoteException {
        super();
    }
    
    public Manifest getServerManifest()   {
        try {
            Manifest serverManifest = fileDAO.generateManifest(UserSession.getInstance().getCurrentUser());
            return serverManifest;
        } catch (SQLException ex) {
            Logger.getLogger(SyncApiImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
