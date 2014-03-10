package networkP2P;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

import networkP2P.utils.ConversionUtils;
import networkP2P.utils.GeneralUtils;
import networkP2P.utils.LoggerUtils;

/**
 * This class is the entrance of the process, the files those from website may have the 
 * similar and completely function. Will handle test in the second half of this semester.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class peerProcess extends Thread {
	private ServerSocket ss;
	public static int myPeerID = 0;
	
	/**
	 * The constructor will initialize the server socket and listening... and start a 
	 * thread to handle coming message.
	 * 
	 * @param sourceSocket
	 * @throws Exception
	 */
	public peerProcess(ServerSocket sourceSocket){
		ss = sourceSocket;

		System.out.println("A Thread is created from port: " + ss.getLocalPort());
		// handshake message and bitfield message
/*		try {

		} catch (Exception e) {
			e.printStackTrace();
		}*/
		this.start();
	}
	

	
	public static void main(String args[]) throws Exception{
		
		// 1. Validate Input
		if(!validateInput(args)) return;
		
		// 2. Get available peers from PeerInfo.cfg
		ArrayList <PeerInfo> peerArr = GeneralUtils.getInstance().readPeerFile();

		// 3. Start the sourcePeerID thread(s) with each destination
		raiseNewPeer(peerArr);
		
	}
	
	/**
	 * This method validates the input: peerID
	 * 
	 * @param args
	 * @return
	 */
	private static boolean validateInput (String args[]){
		System.out.println("validating input...");
		if (args[0] == null){
			System.out.println("Please enter a peerID to start...");
			return false;
		}
		GeneralUtils.setMyPeerID(Integer.valueOf(args[0]));
		myPeerID = Integer.valueOf(args[0]);
		new LoggerUtils(myPeerID);
		System.out.println("input is OK.");
		return true;
	}
	
	/**
	 * This method handles the handshake message at the beginning before other processes start.
	 * @param aMessage
	 * @return
	 */
	public static boolean validateHandShake (PeerMessage aMessage){
		if(GeneralUtils.HANDSHAKE_HEADER.equals(aMessage.getMesPayload())){
			return true;
		}
		return false;
	}
	
	/**
	 * This method add a new peer and make a new connection to it with handshake and bitfield
	 * message.
	 * 
	 * @param peerArr
	 * @param sourcePeerID
	 * @throws Exception
	 */
	private static void raiseNewPeer (ArrayList <PeerInfo> peerArr) throws Exception{
//		System.out.println("step3: starting thread(s)...");
		if(peerArr == null || peerArr.size() == 0){
			System.out.println("No available peer in the PeerInfo.cfg");
			return;
		}
		ServerSocket ss = null;
		int count = 0;

		try {
//			System.out.println("peerArr.size(): " + peerArr.size());
			for (count = 0; count < peerArr.size(); count++){
				if(myPeerID == peerArr.get(count).getPeerID()){
					ss = new ServerSocket(peerArr.get(count).getPort());
					BitManager.getInstance(peerArr.get(count).getHasFile());
					break;
				}
			}
			BitManager bitMan = BitManager.getInstance();
			for(int i = 0; i < peerArr.size(); i++){
				if(myPeerID != peerArr.get(i).getPeerID())
					bitMan.addPeerInfo(peerArr.get(i).peerID, peerArr.get(i).hasFile);
			}
			
			// 1. initialize the peer as server to listen to new message to accept
			new peerProcess(ss);
			
	
			if(count == peerArr.size()){
				System.out.println("Peer ID NOT Found!");
				return;
			}
			
			// 2. initialize the peer as client to send/receive handShake and bitfield message
			for(int i = 0; i < count; i++){
				PeerSocket pSocket = new PeerSocket(peerArr.get(i));
				ConnectionManager.getEstablishedSockets().put(peerArr.get(i).peerID, pSocket);
				// sending handshake message
				PeerMessage handShakeMessage = new PeerMessage(GeneralUtils.HANDSHAKE,ConversionUtils.IntToBytes(myPeerID));
				pSocket.sendData(handShakeMessage.getMesBytes());
				LoggerUtils.getLogger().info(String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(myPeerID) + " makes a connection to Peer " + 
						String.valueOf(peerArr.get(i).getPeerID()));
				PeerListener pl = new PeerListener(pSocket);
//				System.out.println("add Socket: " + pSocket.s);
//				ConnectionManager.getInstance().getEstablishedSockets().put(pSocket.s.getInetAddress().getHostAddress(), pl);
				pl.start();
/*				// sending bitfield message
				if(validateHandShake(pSocket.recvData())){
					LoggerUtils.getLogger().info(String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(myPeerID) + " makes a connection to Peer " + 
							String.valueOf(peerArr.get(i).getPeerID()));
					// to send the bitfield message here
					if(peerArr.get(count).getHasFile() != 0){
						String bitField = String.valueOf(1 + BitManager.getInstance().pieceNum) + GeneralUtils.BITFIELD + BitManager.getInstance().myBitFields;
						pSocket.sendData(bitField.getBytes());
						// handle the reply
						PeerMessage pMessage = pSocket.recvData();
						if(pMessage != null){
							MesHandlerFactory.getMesHandler(GeneralUtils.bytesToInt(pMessage.getMesType())).handleMessage(pSocket, pMessage);
						}
					}
				}
				else {
					System.out.println("Can not validate the HandShake message!");
				}*/
			}
		} catch (UnknownHostException uhe) {
			throw uhe;
		} catch (IOException ioe) {
			throw ioe;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		// listening
		try {
//			System.out.println("Start to listening...");
			ss.setSoTimeout(100);
			while (!ChokeManager.isShutdown()) {
				
				try {
					sleep(2000);
//					System.out.println("position 1");
					Socket clientSocket = ss.accept();
					PeerListener pl = new PeerListener(clientSocket);
					pl.start();
				} catch (SocketTimeoutException etimeout) {
					continue;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
			ss.close();
			System.out.println("Peer process shut down.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
