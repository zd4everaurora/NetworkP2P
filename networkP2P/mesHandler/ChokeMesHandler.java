package networkP2P.mesHandler;

import java.util.Date;

import networkP2P.PeerMessage;
import networkP2P.PeerSocket;
import networkP2P.utils.GeneralUtils;
import networkP2P.utils.LoggerUtils;

/**
 * This message handler handles the message "Choke".
 * On receiving the Choke message, we know that peer A choked peer B and no longer 
 * deal with the request message from peer B. Peer B will stop sending the request message.
 * Here we are peer B.
 * 
 * @author DaZ ZiJ YuS
 *
 */
public class ChokeMesHandler implements MesHandlerInterface{

	@Override
	public void handleMessage(PeerSocket ps, PeerMessage pm) {
		// do nothing currently
		String tmp = String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(GeneralUtils.getMyPeerID()) + " is choked by " +
				"[" + ps.pi.peerID + "]";
		LoggerUtils.getLogger().info(String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(GeneralUtils.getMyPeerID()) + " is choked by " +
				"[" + ps.pi.peerID + "]"); 
		return;
	}

}
