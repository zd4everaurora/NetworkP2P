package networkP2P;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class is the structure for the connections between the peers.
 * It contains the socket with I/O stream and the peerInfo to connect with.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class PeerSocket {
	public Socket s;
	public DataInputStream is;
	public DataOutputStream os;
	public PeerInfo pi;
	
	public PeerSocket(Socket soc) throws IOException {
		is = new DataInputStream(soc.getInputStream());
		os = new DataOutputStream(soc.getOutputStream());
		s = soc;
/*		for (PeerInfo aPI : GeneralUtils.getInstance().readPeerFile()){
			if(aPI.getHostName().equals(soc.getInetAddress().getHostName())){
				pi = aPI;
			}
		}
		if(pi == null)
			System.out.println("PI NULL!!!");
		else
			System.out.println("PI is" + pi.peerID);*/
	}
	
	public PeerSocket(PeerInfo aPeerInfo) throws UnknownHostException, IOException {
		try {
			pi = aPeerInfo;
			System.out.println("Creating new socket to host and port: " + pi.getHostName()+ " " + pi.getPort());
			s = new Socket(pi.getHostName(), pi.getPort());
			is = new DataInputStream(s.getInputStream());
			os = new DataOutputStream(s.getOutputStream());
		} catch (UnknownHostException uhe) {
			throw uhe;
		} catch (IOException ioe) {
			throw ioe;
		}
	}

	/**
	 * This method will receive the data from the peer in the connection.
	 * @return
	 * @throws IOException
	 */
	public PeerMessage recvData() throws IOException {
		if (s.isClosed() || s.getInputStream().available()<=0)
			return null;
		PeerMessage msg = new PeerMessage(s);
		return msg;
	}
	
	/**
	 * This method will send the data from the peer in the connection.
	 * @param bits
	 * @throws IOException
	 */
	public void sendData(byte[] bits) throws IOException {
		if (s.isClosed())
			return;
		os.write(bits);
		os.flush();
	}
	
	/**
	 * This method is to close the socket when no connection needed.
	 * @throws IOException
	 */
	public void close() throws IOException {
		is.close();
		os.close();
		s.close();
	}
	
}
