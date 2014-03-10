package networkP2P;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import networkP2P.utils.ConversionUtils;
import networkP2P.utils.GeneralUtils;

/**
 * This class stores the structure for the messages those are sent and received between peers.
 * The structure are as below except handshake message:
 * 		message length:   4 byte
 * 		message type:     1 byte
 * 		message payload:  message dependent
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class PeerMessage {

	private int mesLength;
	private byte mesType;
	private byte[] mesPayload;
	
	// Read message from socket
	public PeerMessage(Socket s) throws IOException {
		//warning: blocking here. try to check availability and return null
//		s.setSoTimeout(10000);
//		while(s.getInputStream().available() < 4);
		DataInputStream is = new DataInputStream(s.getInputStream());
//		DataOutputStream os = new DataOutputStream(s.getOutputStream());
		byte[] tmp = new byte[4];
		is.readFully(tmp);
/*		if (s.getInputStream().read(tmp) != 4)
			throw new IOException("");*/
		// check if it is handshake message
		if (ConversionUtils.BytesToString(tmp).equals("CEN5")) {
//			while(s.getInputStream().available() < 14);
			byte[] handShakeHeaderPart = new byte [14];
			is.readFully(handShakeHeaderPart);
/*			if (s.getInputStream().read(handShakeHeaderPart) != 14)
				throw new IOException("");*/
			String handShakeHeader = "CEN5" + ConversionUtils.BytesToString(handShakeHeaderPart);
			if(GeneralUtils.HANDSHAKE_HEADER.equals(handShakeHeader)){
				byte[] zeroBits = new byte[10];
				byte[] peerID = new byte[4];
				is.readFully(zeroBits);
				is.readFully(peerID);
/*				if(s.getInputStream().read(zeroBits) != 10)
					throw new IOException("");
				if(s.getInputStream().read(peerID) != 4)
					throw new IOException("");*/
				mesLength = 32;
				mesType = GeneralUtils.HANDSHAKE;
				mesPayload = peerID;
			}
			else
				throw new IOException("");
		}
		else {
			mesLength = ConversionUtils.BytesToInt(tmp);
//			while(s.getInputStream().available() < mesLength);
			byte[] type = new byte[1];
			is.readFully(type);
//			if (s.getInputStream().read(type) != 1)
//				throw new IOException("");
//			System.out.println("mesLength is: " + mesLength);
			mesType = ConversionUtils.BytesToByte(type);
			if(mesLength != 1){
				mesPayload = new byte[mesLength-1];
				is.readFully(mesPayload);
			}
/*			if (s.getInputStream().read(mesPayload) != mesLength - 1)
				throw new IOException("");*/
		}
	}
	
	// Composing new message
	public PeerMessage(byte type,byte[] payload) {
		mesType = type;
		mesPayload = payload;//warning: is clone needed?
		mesLength = (payload != null)? 1 + payload.length : 1;
			
	}
	
	public byte[] getMesBytes() {
		ByteArrayOutputStream messageData = new ByteArrayOutputStream(mesLength);
		DataOutputStream message = new DataOutputStream(messageData);
		try {
			if (mesType == GeneralUtils.HANDSHAKE) {
				message.writeBytes(GeneralUtils.HANDSHAKE_HEADER);
				message.write(GeneralUtils.ZERO_BITS);
				message.write(mesPayload);
			}
			else {
				message.writeInt(mesLength);
				message.writeByte(mesType);
				if(mesPayload != null)
					message.write(mesPayload);
			}
			message.close();
			return messageData.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	public int getMesLength() {
		return mesLength;
	}
	
	public int getPayloadLength() {
		return (mesPayload == null) ? 0 : mesPayload.length;
	}

//	public void setMesLength(int mesLength) {
//		this.mesLength = mesLength;
//	}

	public byte getMesType() {
		return mesType;
	}

	/*public void setMesType(byte mesType) {
		this.mesType = mesType;
	}*/

	public byte[] getMesPayload() {
		return mesPayload;
	}

	/*public void setMesPayload(byte[] mesPayload) {
		this.mesPayload = mesPayload;
	}*/
	
}
