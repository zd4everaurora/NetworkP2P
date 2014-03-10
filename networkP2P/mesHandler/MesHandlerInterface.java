package networkP2P.mesHandler;

import networkP2P.PeerMessage;
import networkP2P.PeerSocket;

/**
 * This is the interface for all message handler.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public interface MesHandlerInterface {
	public void handleMessage(PeerSocket ps, PeerMessage pm);
}
