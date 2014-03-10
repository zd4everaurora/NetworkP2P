package networkP2P;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

public class ConnectionManager {
	private static ConnectionManager instance;
	private Hashtable <Integer, PeerSocket> EstablishedSockets;
	private ConnectionManager() {
		EstablishedSockets = new Hashtable <Integer, PeerSocket>();
	}
	public static ConnectionManager getInstance() {
		if (instance == null)
			instance = new ConnectionManager();
		return instance;
	}
	public static Hashtable <Integer, PeerSocket> getEstablishedSockets() {
		return getInstance().EstablishedSockets;
	}
	public static void close(){
		for (Iterator<?> it = getInstance().EstablishedSockets.keySet().iterator(); it.hasNext(); ) {
			int peerID = (Integer) it.next();
			try {
				getInstance().EstablishedSockets.get(peerID).close();
//				getInstance().EstablishedSockets.remove(peerID);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
