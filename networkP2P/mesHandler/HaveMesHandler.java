package networkP2P.mesHandler;

import java.io.IOException;
import java.util.Date;

import networkP2P.BitManager;
import networkP2P.PeerMessage;
import networkP2P.PeerSocket;
import networkP2P.utils.ConversionUtils;
import networkP2P.utils.GeneralUtils;
import networkP2P.utils.LoggerUtils;

/**
 * This message handler handles the message "Have".
 * At the time when peer A received a completed piece from its neighbor, A will send 'HAVE'
 * message with the piece index to all its neighbors once this piece A does not have before.
 * On receiving 'HAVE' message, for example, peer B will check if it is interested in this piece.
 * peer B will send 'INTERESTED' message if it does not have the piece. Only if peer B contains
 * all pieces in peer A, it will send 'NOT_INTERESTED' to peer A. 
 * Here we are peer B.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class HaveMesHandler implements MesHandlerInterface{

	@Override
	public void handleMessage(PeerSocket ps, PeerMessage pm) {
		BitManager bitM = null;
		try {
			LoggerUtils.getLogger().info(String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(GeneralUtils.getMyPeerID()) + " received the 'hava' " +
					"message from [" + ps.pi.peerID + "] for the piece [" + ConversionUtils.BytesToInt(pm.getMesPayload()) + "]"); 
			bitM = BitManager.getInstance();
			bitM.updatePeerInfo(ps.pi.peerID, ConversionUtils.BytesToInt(pm.getMesPayload()));
			// interested to that message
			if(!bitM.isHave(pm.getMesPayload())){
				PeerMessage interestedMessage = new PeerMessage(GeneralUtils.INTERESTED,null);
				ps.sendData(interestedMessage.getMesBytes());
			}
//			else if(!bitM.isInterested(ps.pi.getPeerID(), pm.getMesPayload())){
//				PeerMessage notInterestedMessage = new PeerMessage(GeneralUtils.NOT_INTERESTED,null);
//				ps.sendData(notInterestedMessage.getMesBytes());
//			}
		} catch (IOException e) {
//			e.printStackTrace();
		}
	}

}
