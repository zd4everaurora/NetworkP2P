package networkP2P;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import networkP2P.utils.GeneralUtils;
import networkP2P.utils.LoggerUtils;

/**
 * This class manages Chokes information for this peer. It determines UnchokeList,
 * and OptimisticUnchokePeer every pre-defined time from InterestedList.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class ChokeManager{
	
	private static ChokeManager chokeMan;
	public int numberOfPreferredNeighbors = 0;
	public int UnchokingInterval = 0;
	public int OptimisticUnchokingInterval = 0;
	public volatile boolean shutdown = false;

	public Timer timer1;
	public Timer timer2;
	// the list for those who are interested in me
	public Hashtable<PeerInfo, Integer> interestedList;
	// the list for unChoked peer by me
	public List<PeerInfo> unChokeList;
	// the  optimistically unchoked neighbor
	public PeerInfo OPTUnchokePeer;
	
	/**
	 * The constructor of the class. It will store some properties and list for Chock and Unchock. 
	 * @throws IOException
	 */
	private ChokeManager() {
		numberOfPreferredNeighbors = GeneralUtils.getCommonProperties().NUMBER_OF_PREFERRED_NEIGHBORS;
		UnchokingInterval = GeneralUtils.getCommonProperties().UNCHOKING_INTERVAL;
		OptimisticUnchokingInterval = GeneralUtils.getCommonProperties().OPTIMISTIC_UNCHOKING_INTERVAL;
		interestedList = new Hashtable<PeerInfo, Integer>();
		unChokeList = new LinkedList<PeerInfo>();
		OPTUnchokePeer = new PeerInfo();
		timer1 = new Timer();
		timer2 = new Timer();
		UnchokeList unchokeList = new UnchokeList();
		OPTUnchokePeer optUnchokePeer = new OPTUnchokePeer();
		// The timer will be starting once ChokeManager is initialized.
		timer1.scheduleAtFixedRate(unchokeList, 1000, UnchokingInterval * 1000);
		timer2.scheduleAtFixedRate(optUnchokePeer, 1000, OptimisticUnchokingInterval * 1000);
	}
	
	/**
	 * This inner class executes and refresh the unchokeList every pre-defined time.
	 * 
	 * @author DAZ ZIJ YUS
	 *
	 */
	static class UnchokeList extends TimerTask{

		/* (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			List<PeerInfo> peerInfo = null;
			try {
				peerInfo = new ArrayList<PeerInfo>();
				if (!BitManager.getInstance().isCompleted) {
					Set<Entry<PeerInfo, Integer>> set = chokeMan.interestedList.entrySet();
					Map.Entry[] entries = set.toArray(new Map.Entry[set.size()]);
					Arrays.sort(entries, new Comparator<Object>() {
						public int compare(Object a0, Object a1) {
							Object value1 = ((Map.Entry) a0).getValue();
							Object value2 = ((Map.Entry) a1).getValue();
							return ((Comparable<Object>) value2).compareTo(value1);
						}
					});

					for (int i = 0; i < chokeMan.numberOfPreferredNeighbors && i < chokeMan.interestedList.size(); i++) {
						peerInfo.add((PeerInfo) entries[i].getKey());
					}
				} else {
					if(BitManager.getInstance().isAllDownloadCompleted()){
						close();
						return;
					}
					Random random = new Random();						
					Hashtable<PeerInfo, Integer> temp = (Hashtable<PeerInfo, Integer>) chokeMan.interestedList.clone();
					for(int i = 0; i < chokeMan.numberOfPreferredNeighbors && i < chokeMan.interestedList.size(); i++){
						int curr = random.nextInt(temp.size());
						int count = 0;
						for (Iterator<?> it = temp.keySet().iterator(); it.hasNext(); count++) {
							if(count == curr){
								PeerInfo pi = (PeerInfo) it.next();
								peerInfo.add(pi);
								temp.remove(pi);
								break;
							}
						}
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				refreshUnchokeList(peerInfo);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}
	
	/**
	 * This inner class executes and refresh the OPTUnchokePeer every pre-defined time.
	 * 
	 * @author DAZ ZIJ YUS
	 *
	 */
	static class OPTUnchokePeer extends TimerTask{

		/* (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			PeerInfo aPeerInfo = new PeerInfo();
			ChokeManager chokeman = ChokeManager.getInstance();
			int randomRange = chokeman.interestedList.size() - chokeman.numberOfPreferredNeighbors - 1;
			if(randomRange > 0){
				Random random = new Random();
				int randomNum = random.nextInt(randomRange+1);
				Iterator it = chokeman.interestedList.entrySet().iterator();
				int count = 0;
				while (it.hasNext() && count != randomNum) {
					Entry entry = (Entry) it.next();
					if (!chokeman.unChokeList.contains((PeerInfo) entry.getKey())) {
						count++;
					}
				}
				while(it.hasNext()){
					Entry entry = (Entry) it.next();
					if(!getInstance().unChokeList.contains((PeerInfo) entry.getKey())){
						aPeerInfo = (PeerInfo) entry.getKey();
						break;
					}
				}
				try {
					refreshOPTUnchokePeer(aPeerInfo);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
	}

	/**
	 * This is to obtain an instance of the class.
	 * @return An instance of ChockManager
	 * @throws IOException
	 */
	private static ChokeManager getInstance() {
		if(chokeMan == null)
			chokeMan = new ChokeManager();
		return chokeMan;
	}
	
	/**
	 * This method will add a new peer into the interested list
	 * @param aPeerInfo
	 */
	public static void addToInList(PeerInfo peerInfo){
		getInstance();
		if(!chokeMan.interestedList.contains(peerInfo))
			chokeMan.interestedList.put(peerInfo, 0);
	}
	
	/**
	 * This method will remove a peer from the interested list i.e. receiving 
	 * NotInterested message
	 * from that peer.
	 * @param aPeerInfo
	 */
	public static void removeFromInList(PeerInfo aPeerInfo){
		getInstance();		
		if(chokeMan.interestedList.containsKey(aPeerInfo))
//			System.out.println("removing from interested list: " + aPeerInfo.peerID);
			chokeMan.interestedList.remove(aPeerInfo);
	}
	
	/**
	 * This method will add pre-defined number of peers into choke list, 
	 * these peers must from interested list.
	 * @param aPeerInfo
	 * @throws IOException 
	 */
	public static void refreshUnchokeList(List<PeerInfo> peerInfo) throws IOException{
		List<PeerInfo> toChoke = new LinkedList<PeerInfo>();
		/*		for(PeerInfo u : getInstance().unChokeList){
			temp.add(u.clone());
		}*/
		// avoid cocurrent modification exception here
		for (PeerInfo u : getInstance().unChokeList){
			if(!peerInfo.contains(u)){
				System.out.println("to choke the peer:" + u.peerID);
//				getInstance().unChokeList.remove(u);
				toChoke.add(u);
				// send choke message to the peers who is no longer in the list
//				String choke = "0001" + GeneralUtils.CHOKE;
				PeerMessage chockMessage = new PeerMessage(GeneralUtils.CHOKE,null);
				PeerSocket pSocket;
				try {
					pSocket = ConnectionManager.getEstablishedSockets().get(u.peerID);
					pSocket.sendData(chockMessage.getMesBytes());
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		if(toChoke.size() > 0){
			for(PeerInfo u : toChoke){
				getInstance().unChokeList.remove(u);
			}
		}
		for (PeerInfo p : peerInfo) {
			if(!getInstance().unChokeList.contains(p)) {
				getInstance().unChokeList.add(p);
				// send unchoke message to the peers who is added into the list
//				String unchoke = "0001" + GeneralUtils.UNCHOKE;
				PeerMessage unchokeMessage = new PeerMessage(GeneralUtils.UNCHOKE,null);
				PeerSocket pSocket;
				try {
					pSocket = ConnectionManager.getEstablishedSockets().get(p.peerID);
					pSocket.sendData(unchokeMessage.getMesBytes());
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}

			List<String> stringList = new ArrayList<String>();
			for(PeerInfo pi : getInstance().unChokeList){
				stringList.add(String.valueOf(pi.peerID));
			}
			LoggerUtils.getLogger().info(String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(GeneralUtils.getMyPeerID()) + " has the preferred" +
					"neighbors [" + GeneralUtils.listToString(stringList) + "]"); 
		}
		for (Iterator<?> it = getInstance().interestedList.keySet().iterator(); it.hasNext(); ) {
			PeerInfo tmpPI = (PeerInfo)it.next();
//			System.out.println("Adding to interested list: "+ tmpPI.peerID);
			getInstance().interestedList.put(tmpPI, 0);
		}
	}
	
	/**
	 * This method is to refresh the optimistically unchoked peer and send Unchoke message to it
	 * @param aPeerInfo
	 * @throws IOException 
	 */
	public static void refreshOPTUnchokePeer(PeerInfo aPeerInfo) throws IOException{
		if(getInstance().OPTUnchokePeer != aPeerInfo){
			getInstance().OPTUnchokePeer = aPeerInfo;
			PeerSocket pSocket;
			// send unchoke message to the peer
//			String unchoke = "0001" + GeneralUtils.UNCHOKE;
			PeerMessage unchokeMessage = new PeerMessage(GeneralUtils.UNCHOKE,null);
			try {
				pSocket = ConnectionManager.getEstablishedSockets().get(aPeerInfo.peerID);
				pSocket.sendData(unchokeMessage.getMesBytes());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			LoggerUtils.getLogger().info(String.valueOf("[" + new Date()) + "]: " + "Peer " + String.valueOf(GeneralUtils.getMyPeerID()) + " has the optimistically" +
					"unchoked neighbor [" + aPeerInfo.peerID + "]"); 
		}
	}
	
	/**
	 * This method will remove a peer from chock list, i.e. receiving NotInterested message
	 * from that peer.
	 * @param aPeerInfo
	 */
	public static void removeFromUnChokeList(PeerInfo aPeerInfo){
		getInstance();
		if(chokeMan.unChokeList.contains(aPeerInfo))
//			System.out.println("removing from unchoke list: " + aPeerInfo.peerID);
			chokeMan.unChokeList.remove(aPeerInfo);
	}
	
	/**
	 * This method is to set the optimistically unchoked neighbor, which is randomly among 
	 * the neighbors that are choked at that moment but are interested in its data.
	 * @param aPeerInfo
	 */
	public void setOPTUnchoke(PeerInfo aPeerInfo){
		OPTUnchokePeer = aPeerInfo;
	}
	
	/**
	 * This method is to get the optimistically unchoked neighbor
	 * @return
	 */
	public PeerInfo getOPTUnchoke(){
		return OPTUnchokePeer;
	}
	
	/**
	 * This method is to get all my neighbors those are interested in me.
	 * @return
	 */
	public static Hashtable<PeerInfo, Integer> getInterestedList() {
		getInstance();
		return chokeMan.interestedList;
	}

	/**
	 * This method is to get all my neighbors those are unchoked by me
	 * @return
	 */
	public static List<PeerInfo> getUnChokeList() {
		return getInstance().unChokeList;
	}
	
	/**
	 * This method is to stop the timmer and close the chock Manager
	 * @throws IOException 
	 * 
	 */
	public static void close() throws IOException{
		System.out.println("close called");
		getInstance().shutdown = true;
		getInstance().timer1.cancel();
		getInstance().timer2.cancel();
		ConnectionManager.getInstance().close();
		BitManager.getInstance().close();
	}

	public static boolean isShutdown() {
		return getInstance().shutdown;
	}
}   
