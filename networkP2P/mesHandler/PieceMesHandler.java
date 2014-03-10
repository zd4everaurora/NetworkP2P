/**
 * This message handler handles the message "Piece".
 * When peer A send request message to peer B, peer B will send the piece message that
 * contains the actual piece, after completely downloading the piece, peer A sends another
 * 'request' message to peer B, until peer A is choked by peer B or peer B does not have 
 * any more interesting pieces.
 * Here we are peer A.
 * 
 * @author DAZ ZIJ YUS
 * 
 */

package networkP2P.mesHandler;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import networkP2P.BitManager;
import networkP2P.ChokeManager;
import networkP2P.ConnectionManager;
import networkP2P.PeerMessage;
import networkP2P.PeerSocket;
import networkP2P.utils.ConversionUtils;
import networkP2P.utils.GeneralUtils;
import networkP2P.utils.LoggerUtils;


public class PieceMesHandler implements MesHandlerInterface{

	@Override
	public void handleMessage(PeerSocket ps, PeerMessage pm) {
		try {
			BitManager bitMan = BitManager.getInstance();
			byte[] index = new byte[4];
			byte[] content = new byte[pm.getPayloadLength() - 4];
			System.arraycopy(pm.getMesPayload(), 0, index, 0, index.length);
			System.arraycopy(pm.getMesPayload(), 4, content, 0, content.length);
			LoggerUtils.getLogger().info(String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(GeneralUtils.getMyPeerID()) + " has downloaded the piece [" +
					ConversionUtils.BytesToInt(index) + "] from [" + ps.pi.peerID + "]"); 
			//record for calculating download speed from each interested peer
//			ChokeManager chokeMan = ChokeManager.getInstance();
			if(ChokeManager.getInterestedList().containsKey(ps.pi)) {
				int count = ChokeManager.getInterestedList().get(ps.pi);
				count++;
				ChokeManager.getInterestedList().put(ps.pi, count);
			}
			// decide whether already have, store if not
			boolean broadcast = bitMan.setActualPiece(ConversionUtils.BytesToInt(index), content);
			
			// broadcast the have message to other peers
			if(broadcast){
				PeerMessage haveMessage = new PeerMessage(GeneralUtils.HAVE, index);
				for (Iterator<Integer> it = ConnectionManager.getEstablishedSockets().keySet().iterator(); it.hasNext(); ) {
					int peerID = it.next();
					PeerSocket psH = ConnectionManager.getEstablishedSockets().get(peerID);
					if(psH.s != null && !psH.s.isClosed()){
						psH.sendData(haveMessage.getMesBytes());
					}
				}

				if(bitMan.isDownloadCompleted(GeneralUtils.getMyPeerID())){
					LoggerUtils.getLogger().info(String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(GeneralUtils.getMyPeerID()) + " has downloaded the complete file");
					System.out.println("download completed!");
					try {
						BitManager.getInstance().writeToDisk();
					} catch (IOException e) {
						e.printStackTrace();
					}
	//				for (Iterator<?> it = BitManager.getInstance().peerNeighborBitsInfo.keySet().iterator(); it.hasNext();) {
	//					System.out.println(BitManager.getInstance().peerNeighborBitsInfo.get(it.next()));
	//				}
				}
			}
			
			byte[] pieceIndex;
			// get interested piece to request
			if((pieceIndex = bitMan.getInterestedPiece(ps.pi)) != null){
				PeerMessage requestMessage = new PeerMessage(GeneralUtils.REQUEST,pieceIndex);
				ps.sendData(requestMessage.getMesBytes());
			}
			else
			{
				PeerMessage notInterestedMessage = new PeerMessage(GeneralUtils.NOT_INTERESTED,null);
				ps.sendData(notInterestedMessage.getMesBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
