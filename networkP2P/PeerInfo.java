package networkP2P;

/**
 * This class stores the structure for a peer, containing PeerID, HostName, 
 * Port and whether it has the whole file or not.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class PeerInfo {
	public int peerID;
	public String hostName;
	public int port;
	public int hasFile;
	public int getPeerID() {
		return peerID;
	}
	public void setPeerID(int peerID) {
		this.peerID = peerID;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getHasFile() {
		return hasFile;
	}
	public void setHasFile(int hasFile) {
		this.hasFile = hasFile;
	}
	public PeerInfo clone(){
		PeerInfo p = new PeerInfo();
		p.peerID = this.peerID;
		p.hostName = this.hostName;
		p.port = this.port;
		p.hasFile = this.hasFile;
		return p;
	}

}
