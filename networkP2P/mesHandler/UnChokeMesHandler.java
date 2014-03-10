package networkP2P.mesHandler;

import java.io.IOException;
import java.util.Date;

import networkP2P.BitManager;
import networkP2P.PeerMessage;
import networkP2P.PeerSocket;
import networkP2P.utils.GeneralUtils;
import networkP2P.utils.LoggerUtils;

/**
 * This message handler handles the message "UnChoke".
 * When receiving this message from peer A to peer B. We understand that peer B is in the 
 * interested and unchoked list in peer A, and peer B can start send "request" message to
 * A to request the pieces that A has but B does not have.
 * Here we are peer B.
 * 
 * @author DaZ ZiJ YuS
 *
 */
public class UnChokeMesHandler implements MesHandlerInterface{

	@Override
	public void handleMessage(PeerSocket ps, PeerMessage pm) {
//		System.out.println("In unchoke handler!");
		try {
			LoggerUtils.getLogger().info(String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(GeneralUtils.getMyPeerID()) + " is unchoked by " +
					"[" + ps.pi.peerID + "]"); 
//			String request = "0005" + GeneralUtils.REQUEST + BitManager.getInstance().getInterestedPiece(ps.pi);
			byte[] interestedPieceIndex = BitManager.getInstance().getInterestedPiece(ps.pi);
			if(interestedPieceIndex != null) {
				PeerMessage requestMessage = new PeerMessage(GeneralUtils.REQUEST,interestedPieceIndex);
				ps.sendData(requestMessage.getMesBytes());
			}
			else {
				PeerMessage notInterestedMessage = new PeerMessage(GeneralUtils.NOT_INTERESTED,null);
				ps.sendData(notInterestedMessage.getMesBytes());
			}
//			System.out.println("At the end of unchoke handler!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
