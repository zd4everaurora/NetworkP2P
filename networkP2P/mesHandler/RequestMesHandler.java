package networkP2P.mesHandler;

import java.io.IOException;
import java.util.Date;

import networkP2P.BitManager;
import networkP2P.ChokeManager;
import networkP2P.PeerMessage;
import networkP2P.PeerSocket;
import networkP2P.utils.ConversionUtils;
import networkP2P.utils.GeneralUtils;
import networkP2P.utils.LoggerUtils;

/**
 * This message handler handles the message "Request".
 * When peer A send request message to peer B, peer B will send the piece message that
 * contains the actual piece, after completely downloading the piece, peer A sends another
 * 'request' message to peer B, until peer A is choked by peer B or peer B does not have 
 * any more interesting pieces.
 * Here we are peer B.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class RequestMesHandler implements MesHandlerInterface{

	@Override
	public void handleMessage(PeerSocket ps, PeerMessage pm) {
		try {
//			LoggerUtils.getLogger().info(String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(GeneralUtils.getMyPeerID()) + " has received request for the piece [" +
//					ConversionUtils.BytesToInt(pm.getMesPayload()) + "] from [" + ps.pi.peerID + "]");
			if(ChokeManager.getUnChokeList().contains(ps.pi)){
				byte[] pieceContent = BitManager.getInstance().getSpecifiedPiece(pm.getMesPayload());
				if(pieceContent != null){
//					String piece = String.valueOf(1+GeneralUtils.getCommonProperties().PIECE_SIZE) + GeneralUtils.REQUEST + actualPiece;
					PeerMessage pieceMessage = new PeerMessage(GeneralUtils.PIECE,ConversionUtils.JoinBytes(pm.getMesPayload(), pieceContent));
					ps.sendData(pieceMessage.getMesBytes());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
