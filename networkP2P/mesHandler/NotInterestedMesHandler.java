package networkP2P.mesHandler;

import java.util.Date;

import networkP2P.ChokeManager;
import networkP2P.PeerMessage;
import networkP2P.PeerSocket;
import networkP2P.utils.GeneralUtils;
import networkP2P.utils.LoggerUtils;

/**
 * This message handler handles the message "NotInterested".
 * On receiving NotInterested message from peer A, we know that we do not have any piece that
 * peer A wants to get. So we need to remove peer A from interested list as well as unChoke
 * list if possible.
 * @author DAZ ZIJ YUS
 *
 */
public class NotInterestedMesHandler implements MesHandlerInterface{

	@Override
	public void handleMessage(PeerSocket ps, PeerMessage pm) {
		LoggerUtils.getLogger().info(String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(GeneralUtils.getMyPeerID()) + " received the 'not interested' " +
				"message from [" + ps.pi.peerID + "]"); 

		//			ChokeManager chokeMan = ChokeManager.getInstance();
		ChokeManager.removeFromUnChokeList(ps.pi);
		ChokeManager.removeFromInList(ps.pi);
/*		try {
			if(BitManager.getInstance().isCompleted) {
				BitManager.getInstance().markAsComplete(ps.pi.peerID);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

}
