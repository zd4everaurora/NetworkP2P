package networkP2P.mesHandler;

import java.io.IOException;

import networkP2P.BitManager;
import networkP2P.PeerMessage;
import networkP2P.PeerSocket;
import networkP2P.utils.GeneralUtils;

/**
 * This message handler handles the message "bitfield".
 * Once a handshake message is handled, the bitfield message is sent from Peer A, 
 * to let Peer B know which file pieces it has.
 * Peer B will also send its 'bitfield' message to Peer A, unless it has no pieces.
 * Here we are Peer B.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class BitfieldMesHandler implements MesHandlerInterface{

	@Override
	public void handleMessage(PeerSocket ps, PeerMessage pm) {
//		System.out.println("In bitfield handler!");
		BitManager bitM = null;
		try {
			bitM = BitManager.getInstance();
			bitM.setNeighborBitFields(ps.pi.peerID, pm.getMesPayload());
			// interested to that message
			if(bitM.isInterested(pm.getMesPayload())){
				PeerMessage interestedMessage = new PeerMessage(GeneralUtils.INTERESTED,null);
				ps.sendData(interestedMessage.getMesBytes());
			}
			
			// not interested to that message
			else {
				PeerMessage notInterestedMessage = new PeerMessage(GeneralUtils.NOT_INTERESTED,null);
				ps.sendData(notInterestedMessage.getMesBytes());
			}
//			System.out.println("At the end of bitfield handler!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
