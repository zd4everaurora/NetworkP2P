package networkP2P;

import java.io.IOException;
import java.net.Socket;

public class PeerListener extends Thread{
	
//	private Socket s;
	private PeerSocket peerSocket;
	
	public PeerListener(Socket clientSocket) {
//		s = clientSocket;
		try {
			peerSocket = new PeerSocket(clientSocket);
			System.out.println(peerSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PeerListener(PeerSocket aPeerSocket){
		peerSocket = aPeerSocket;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		

		PeerMessage peerMes;
		try {
/*			if (peerSocket == null){
				
			}*/
			while(!ChokeManager.isShutdown()){
				if((peerMes = peerSocket.recvData()) == null)
					continue;
//				System.out.println("Continue to listening...");
//				System.out.println("Received peer message of type " + peerMes.getMesType());
				MessageHandler mesHandler = new MessageHandler(peerSocket, peerMes);
				mesHandler.start();	
			}
		} catch (IOException e) {
//			e.printStackTrace();
		} /*finally {
			if(peerSocket != null)
				try {
					peerSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			System.out.println("Socket closed");
		}*/
	
	}

	public PeerSocket getPeerSocket() {
		return peerSocket;
	}
	
}
