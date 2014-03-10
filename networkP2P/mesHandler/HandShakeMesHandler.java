package networkP2P.mesHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import networkP2P.BitManager;
import networkP2P.ConnectionManager;
import networkP2P.PeerMessage;
import networkP2P.PeerSocket;
import networkP2P.utils.ConversionUtils;
import networkP2P.utils.GeneralUtils;
import networkP2P.utils.LoggerUtils;

public class HandShakeMesHandler implements MesHandlerInterface{

	@Override
	public void handleMessage(PeerSocket ps, PeerMessage pm) {
//		System.out.println("In HandShake Handler");
		int myPeerID = GeneralUtils.getMyPeerID();
		if(ps.pi == null){
			int peerID;
			try {
				peerID = ConversionUtils.BytesToInt(pm.getMesPayload());
				ConnectionManager.getEstablishedSockets().put(peerID, ps);
				ps.pi = GeneralUtils.getPeerIDInfo().get(peerID);
				// Send back handshake
				PeerMessage handshakeMessage = new PeerMessage(GeneralUtils.HANDSHAKE,ConversionUtils.IntToBytes(myPeerID));
				ps.sendData(handshakeMessage.getMesBytes());
				LoggerUtils.getLogger().info(String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(myPeerID) + " is connected from Peer " + 
						String.valueOf(peerID));
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		BitManager bitMan = null;
		boolean sendBitField = false;
//		System.out.println("***********************");
		try {
			bitMan = BitManager.getInstance();
			if(bitMan.hasAnyPiece()){
//				System.out.println(String.valueOf(1 + BitManager.getInstance().pieceNum));
//				String bitField = String.format("%04d", 1 + BitManager.getInstance().pieceNum) + GeneralUtils.BITFIELD + new String(bitMan.myBitFields, "UTF8");
				PeerMessage bitFieldMessage = new PeerMessage(GeneralUtils.BITFIELD,bitMan.myBitFields);
				ps.sendData(bitFieldMessage.getMesBytes());
				System.out.println("BitField sent");
			}
//			System.out.println("The end of handshake handler");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
