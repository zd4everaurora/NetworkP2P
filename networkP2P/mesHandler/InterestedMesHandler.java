package networkP2P.mesHandler;

//import java.io.IOException;
//import java.net.Socket;
import java.util.Date;

import networkP2P.ChokeManager;
import networkP2P.PeerMessage;
import networkP2P.PeerSocket;
import networkP2P.utils.GeneralUtils;
import networkP2P.utils.LoggerUtils;

/**
 * This message handler handles the message "Interested".
 * On receiving interested message from peer A, we know that we have some piece(s) that peer A
 * does not contain, we will add peer A into our interested list.
 * @author DAZ ZIJ YUS
 *
 */
public class InterestedMesHandler implements MesHandlerInterface{

	@Override
	public void handleMessage(PeerSocket ps, PeerMessage pm) {
		 

		//			ChokeManager chokeMan = ChokeManager.getInstance();
		if(!ChokeManager.getInterestedList().containsKey(ps.pi)){
			LoggerUtils.getLogger().info(String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(GeneralUtils.getMyPeerID()) + " received the 'interested' " +
					"message from [" + ps.pi.peerID + "]");
			ChokeManager.addToInList(ps.pi);

		}
	}
}
