/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.server;

import java.rmi.RemoteException;
import stockfile.api.Manifest;
import stockfile.api.SyncApi;

/**
 *
 * @author WallaceJ
 */
public class SyncApiImpl extends AbstractApiImpl implements SyncApi  {
   
    public SyncApiImpl () throws RemoteException {
        super();
    }
    
    public Manifest getServerManifest()   {
        return null;
    }
}
