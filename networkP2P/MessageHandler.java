package networkP2P;

import java.io.IOException;
import java.net.UnknownHostException;

import networkP2P.mesHandler.MesHandlerFactory;

/**
 * This class will run a new thread to handle the coming message based on the message type.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class MessageHandler extends Thread {
	
	private PeerSocket clientSocket;
	private PeerMessage peerMes;
	
	public MessageHandler(PeerSocket peerSocket, PeerMessage aPeerMes){
		clientSocket = peerSocket;
		peerMes = aPeerMes;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
/*			peerSocket = new PeerSocket(clientSocket);
			PeerMessage peerMes = peerSocket.recvData();*/
			MesHandlerFactory.getMesHandler(peerMes.getMesType())
				.handleMessage(clientSocket, peerMes);
		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
}
